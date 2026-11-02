package com.example.selfevo.ui.dashboard

import com.example.selfevo.data.db.entity.HabitEntity
import com.example.selfevo.data.model.PlayerCard

data class DashboardUiState(
    val playerCard: PlayerCard? = null,
    val habits: List<HabitEntity> = emptyList(),
    val isLoading: Boolean = false,
    val lastSyncTime: String = "Never",
    val errorMessage: String? = null
)
