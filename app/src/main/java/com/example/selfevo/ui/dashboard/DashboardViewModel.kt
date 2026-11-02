package com.example.selfevo.ui.dashboard

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selfevo.data.db.entity.CustomAttributeEntity
import com.example.selfevo.data.db.entity.HabitEntity
import com.example.selfevo.data.model.PlayerCard
import com.example.selfevo.domain.repository.AuthRepository
import com.example.selfevo.domain.repository.HabitRepository
import com.example.selfevo.domain.repository.PlayerCardRepository
import com.example.selfevo.domain.repository.SyncRepository
import com.example.selfevo.domain.usecase.CompleteHabitUseCase
import com.example.selfevo.domain.usecase.GetDashboardDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
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
    private val authRepository: AuthRepository
) : ViewModel() {

    private val currentUserId: String
        get() = authRepository.currentUser?.uid ?: "default_user"

    private val _levelUpEvent = MutableSharedFlow<Int>()
    val levelUpEvent: SharedFlow<Int> = _levelUpEvent

    private val _lastSyncTime = MutableStateFlow("Never")
    val lastSyncTime: StateFlow<String> = _lastSyncTime

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
        refreshData()
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

    fun saveProfileImage(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri) ?: return@launch
                val file = File(context.filesDir, "profile_avatar_${currentUserId.replace("|", "_")}.jpg")
                file.outputStream().use { output ->
                    inputStream.copyTo(output)
                }
                val persistentUriString = Uri.fromFile(file).toString()
                playerCardRepository.updateAvatarUri(currentUserId, persistentUriString)
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Failed to save profile avatar image", e)
            }
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
}
