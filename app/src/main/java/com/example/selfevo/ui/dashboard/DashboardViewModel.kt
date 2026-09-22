package com.example.selfevo.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selfevo.data.auth.AuthRepository
import com.example.selfevo.data.local.entity.HabitEntity
import com.example.selfevo.data.model.PlayerCard
import com.example.selfevo.data.repository.HabitRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DashboardViewModel(
    private val repository: HabitRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val currentUserIdFlow = authRepository.currentUserFlow.map { it?.uid ?: "default_user" }

    @OptIn(ExperimentalCoroutinesApi::class)
    val playerCard: StateFlow<PlayerCard?> = currentUserIdFlow
        .flatMapLatest { userId -> repository.getPlayerCardStream(userId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val habits: StateFlow<List<HabitEntity>> = currentUserIdFlow
        .flatMapLatest { userId -> repository.getHabitsForTodayStream(userId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val currentUserId: String
        get() = authRepository.currentUser?.uid ?: "default_user"

    private val _levelUpEvent = MutableSharedFlow<Int>()
    val levelUpEvent: SharedFlow<Int> = _levelUpEvent

    private val _lastSyncTime = MutableStateFlow<String>("Never")
    val lastSyncTime: StateFlow<String> = _lastSyncTime

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            val userId = currentUserId
            repository.refreshHabits(userId)
            repository.refreshPlayerCard(userId)
            repository.syncPendingLogs()
            _lastSyncTime.value = DateTimeFormatter.ofPattern("HH:mm").format(LocalTime.now())
        }
    }

    fun completeHabit(habitId: String) {
        viewModelScope.launch {
            val userId = currentUserId
            val oldOvr = playerCard.value?.ovr ?: 0
            val updatedCard = repository.completeHabit(habitId, userId)
            val newOvr = updatedCard?.ovr ?: 0

            if (newOvr > oldOvr) {
                _levelUpEvent.emit(newOvr)
            }
        }
    }

    fun addHabit(title: String, attribute: String, frequency: String, reminder: String) {
        viewModelScope.launch {
            repository.addHabit(currentUserId, title, "Custom Habit", attribute, frequency, reminder)
        }
    }
}
