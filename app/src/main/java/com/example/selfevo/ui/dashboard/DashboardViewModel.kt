package com.example.selfevo.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selfevo.data.local.entity.HabitEntity
import com.example.selfevo.data.model.PlayerCard
import com.example.selfevo.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: HabitRepository
) : ViewModel() {

    val playerCard: StateFlow<PlayerCard?> = repository.getPlayerCardStream()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val habits: StateFlow<List<HabitEntity>> = repository.getHabitsStream()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _levelUpEvent = MutableSharedFlow<Int>()
    val levelUpEvent: SharedFlow<Int> = _levelUpEvent

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            repository.refreshHabits()
            repository.refreshPlayerCard()
            repository.syncPendingLogs()
        }
    }

    fun completeHabit(habitId: String) {
        viewModelScope.launch {
            val oldOvr = playerCard.value?.ovr ?: 0
            val updatedCard = repository.completeHabit(habitId)
            val newOvr = updatedCard?.ovr ?: 0

            if (newOvr > oldOvr) {
                _levelUpEvent.emit(newOvr)
            }
        }
    }
}
