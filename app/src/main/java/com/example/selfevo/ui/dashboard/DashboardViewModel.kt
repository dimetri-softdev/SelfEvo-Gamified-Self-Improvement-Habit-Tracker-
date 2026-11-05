package com.example.selfevo.ui.dashboard

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selfevo.R
import com.example.selfevo.data.db.entity.CustomAttributeEntity
import com.example.selfevo.data.db.entity.HabitEntity
import com.example.selfevo.data.model.PlayerCard
import com.example.selfevo.data.repository.NotificationPreferencesRepository
import com.example.selfevo.data.repository.ProfileImageException
import com.example.selfevo.data.repository.ProfileImageRepository
import com.example.selfevo.data.repository.SelfEvoNotificationTopic
import com.example.selfevo.domain.repository.AuthRepository
import com.example.selfevo.domain.repository.HabitRepository
import com.example.selfevo.domain.repository.PlayerCardRepository
import com.example.selfevo.domain.repository.SyncRepository
import com.example.selfevo.domain.usecase.CompleteHabitUseCase
import com.example.selfevo.domain.usecase.GetDashboardDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val completeHabitUseCase: CompleteHabitUseCase,
    private val habitRepository: HabitRepository,
    private val playerCardRepository: PlayerCardRepository,
    private val syncRepository: SyncRepository,
    private val authRepository: AuthRepository,
    private val profileImageRepository: ProfileImageRepository,
    private val notificationPreferencesRepository: NotificationPreferencesRepository
) : ViewModel() {

    private val currentUserId: String
        get() = authRepository.currentUser?.uid ?: "default_user"

    private val _levelUpEvent = MutableSharedFlow<Int>()
    val levelUpEvent: SharedFlow<Int> = _levelUpEvent

    private val _lastSyncTime = MutableStateFlow("Never")
    val lastSyncTime: StateFlow<String> = _lastSyncTime

    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl: StateFlow<String?> = _profileImageUrl

    private val _profileImageError = MutableStateFlow<Int?>(null)
    val profileImageError: StateFlow<Int?> = _profileImageError

    private val _isUploadingProfileImage = MutableStateFlow(false)
    val isUploadingProfileImage: StateFlow<Boolean> = _isUploadingProfileImage

    private val _dailyRemindersEnabled = MutableStateFlow(
        notificationPreferencesRepository.isEnabled(SelfEvoNotificationTopic.DAILY_REMINDERS)
    )
    val dailyRemindersEnabled: StateFlow<Boolean> = _dailyRemindersEnabled

    private val _streakAlertsEnabled = MutableStateFlow(
        notificationPreferencesRepository.isEnabled(SelfEvoNotificationTopic.STREAK_ALERTS)
    )
    val streakAlertsEnabled: StateFlow<Boolean> = _streakAlertsEnabled

    private val _notificationError = MutableStateFlow<String?>(null)
    val notificationError: StateFlow<String?> = _notificationError

    private val _isLoading = MutableStateFlow(false)

    val customAttributes: StateFlow<List<CustomAttributeEntity>> = habitRepository
        .getCustomAttributesStream(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<DashboardUiState> = combine(
        getDashboardDataUseCase(),
        _lastSyncTime,
        _isLoading
    ) { data, lastSync, loading ->
        DashboardUiState(
            playerCard = data.playerCard,
            habits = data.habits,
            isLoading = loading,
            lastSyncTime = lastSync
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    val playerCard: StateFlow<PlayerCard?> = uiState
        .map { it.playerCard }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val habits: StateFlow<List<HabitEntity>> = uiState
        .map { it.habits }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            authRepository.currentUserFlow.collectLatest { user ->
                _profileImageUrl.value = null
                _profileImageError.value = null
                if (user != null) {
                    loadProfileImage(user.uid)
                }
            }
        }
        refreshData()
    }

    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            val userId = authRepository.currentUser?.uid
            if (userId.isNullOrBlank()) {
                _profileImageError.value = R.string.profile_photo_sign_in_required
                return@launch
            }

            _isUploadingProfileImage.value = true
            _profileImageError.value = null
            try {
                _profileImageUrl.value =
                    profileImageRepository.uploadProfileImage(userId, imageUri)
            } catch (e: Exception) {
                Log.e(TAG, "Profile image upload failed", e)
                _profileImageError.value = profileImageErrorMessage(e, isLoading = false)
            } finally {
                _isUploadingProfileImage.value = false
            }
        }
    }

    fun setNotificationEnabled(topic: SelfEvoNotificationTopic, enabled: Boolean) {
        viewModelScope.launch {
            _notificationError.value = null
            try {
                notificationPreferencesRepository.setEnabled(topic, enabled)
                when (topic) {
                    SelfEvoNotificationTopic.DAILY_REMINDERS ->
                        _dailyRemindersEnabled.value = enabled
                    SelfEvoNotificationTopic.STREAK_ALERTS ->
                        _streakAlertsEnabled.value = enabled
                }
            } catch (e: Exception) {
                Log.e(TAG, "Notification preference update failed for ${topic.topicName}", e)
                _notificationError.value =
                    e.localizedMessage ?: "Notification settings could not be updated."
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            refreshDataFor(currentUserId)
            _isLoading.value = false
        }
    }

    fun completeHabit(habitId: String) {
        viewModelScope.launch {
            val result = completeHabitUseCase(habitId, currentUserId)
            if (result.isLevelUp) {
                _levelUpEvent.emit(result.newOvr)
            }
        }
    }

    fun addHabit(
        title: String,
        primaryAttribute: String,
        secondaryAttribute: String? = null,
        frequency: String,
        reminder: String,
        daysOfWeek: String = "Daily",
        targetDuration: String = "30 mins"
    ) {
        viewModelScope.launch {
            habitRepository.addHabit(
                userId = currentUserId,
                title = title,
                description = "Custom Habit",
                attributeType = primaryAttribute,
                secondaryAttributeType = secondaryAttribute,
                frequency = frequency,
                reminderTime = reminder,
                daysOfWeek = daysOfWeek,
                targetDuration = targetDuration
            )
            refreshDataFor(currentUserId)
        }
    }

    fun editHabit(
        habitId: String,
        title: String,
        primaryAttribute: String,
        secondaryAttribute: String? = null,
        frequency: String,
        reminder: String,
        daysOfWeek: String = "Daily",
        targetDuration: String = "30 mins"
    ) {
        viewModelScope.launch {
            habitRepository.updateHabit(
                habitId = habitId,
                userId = currentUserId,
                title = title,
                description = "Custom Habit",
                attributeType = primaryAttribute,
                secondaryAttributeType = secondaryAttribute,
                frequency = frequency,
                reminderTime = reminder,
                daysOfWeek = daysOfWeek,
                targetDuration = targetDuration
            )
            refreshDataFor(currentUserId)
        }
    }

    fun deleteHabit(habitId: String) {
        viewModelScope.launch {
            habitRepository.deleteHabit(habitId, currentUserId)
            refreshDataFor(currentUserId)
        }
    }

    fun addCustomAttribute(title: String, abbreviation: String, colorHex: String) {
        viewModelScope.launch {
            habitRepository.addCustomAttribute(currentUserId, title, abbreviation, colorHex)
        }
    }

    fun updatePlayerName(name: String) {
        viewModelScope.launch {
            playerCardRepository.updatePlayerName(currentUserId, name)
        }
    }

    fun updateAvatarUri(uriString: String?) {
        viewModelScope.launch {
            playerCardRepository.updateAvatarUri(currentUserId, uriString)
        }
    }

    fun updateEquippedTheme(theme: String) {
        viewModelScope.launch {
            playerCardRepository.updateEquippedTheme(currentUserId, theme)
        }
    }

    fun updateCardSlots(
        slot1: String,
        slot2: String,
        slot3: String,
        slot4: String,
        slot5: String,
        slot6: String
    ) {
        viewModelScope.launch {
            playerCardRepository.updateCardSlots(currentUserId, slot1, slot2, slot3, slot4, slot5, slot6)
        }
    }

    private suspend fun refreshDataFor(userId: String) {
        val queueSynced = syncRepository.syncPendingLogs(userId)
        val habitsSynced = habitRepository.refreshHabits(userId)
        val cardSynced = playerCardRepository.refreshPlayerCard(userId)
        _lastSyncTime.value = if (queueSynced && habitsSynced && cardSynced) {
            DateTimeFormatter.ofPattern("HH:mm").format(LocalTime.now())
        } else {
            "Sync failed"
        }
    }

    private suspend fun loadProfileImage(userId: String) {
        try {
            _profileImageUrl.value = profileImageRepository.getProfileImageUrl(userId)
        } catch (e: Exception) {
            Log.e(TAG, "Profile image could not be loaded", e)
            _profileImageError.value = profileImageErrorMessage(e, isLoading = true)
        }
    }

    private fun profileImageErrorMessage(error: Throwable, isLoading: Boolean): Int {
        val profileImageError = generateSequence(error) { it.cause }
            .filterIsInstance<ProfileImageException>()
            .firstOrNull()
        if (profileImageError != null) {
            return when (profileImageError.reason) {
                ProfileImageException.Reason.SIGN_IN_REQUIRED ->
                    R.string.profile_photo_sign_in_required
                ProfileImageException.Reason.UNSUPPORTED_IMAGE ->
                    R.string.profile_photo_unsupported
                ProfileImageException.Reason.IMAGE_TOO_LARGE ->
                    R.string.profile_photo_too_large
                ProfileImageException.Reason.IMAGE_UNAVAILABLE ->
                    R.string.profile_photo_unavailable
                ProfileImageException.Reason.SERVICE_NOT_READY ->
                    R.string.profile_photo_service_unavailable
                ProfileImageException.Reason.UPLOAD_FAILED ->
                    R.string.profile_photo_upload_failed
                ProfileImageException.Reason.PROFILE_ACCESS_DENIED ->
                    R.string.profile_photo_access_denied
                ProfileImageException.Reason.PROFILE_SAVE_DENIED ->
                    R.string.profile_photo_permissions
                ProfileImageException.Reason.PROFILE_SAVE_FAILED ->
                    R.string.profile_photo_saved_but_not_linked
                ProfileImageException.Reason.PROFILE_LOAD_FAILED ->
                    R.string.profile_photo_load_failed
            }
        }

        val isNetworkError = generateSequence(error) { it.cause }.any {
            it is SocketTimeoutException ||
                it is UnknownHostException ||
                it is ConnectException ||
                it is IOException
        }
        return when {
            isNetworkError -> R.string.profile_photo_network_error
            isLoading -> R.string.profile_photo_load_failed
            else -> R.string.profile_photo_upload_failed
        }
    }

    private companion object {
        const val TAG = "SelfEvo_DashboardVM"
    }
}
