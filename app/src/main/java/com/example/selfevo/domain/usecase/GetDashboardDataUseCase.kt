package com.example.selfevo.domain.usecase

import com.example.selfevo.data.db.entity.HabitEntity
import com.example.selfevo.data.model.PlayerCard
import com.example.selfevo.domain.repository.AuthRepository
import com.example.selfevo.domain.repository.HabitRepository
import com.example.selfevo.domain.repository.PlayerCardRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class DashboardData(
    val userId: String,
    val playerCard: PlayerCard?,
    val habits: List<HabitEntity>
)

class GetDashboardDataUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val habitRepository: HabitRepository,
    private val playerCardRepository: PlayerCardRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<DashboardData> {
        val currentUserIdFlow = authRepository.currentUserFlow.map { it?.uid ?: "default_user" }
        return currentUserIdFlow.flatMapLatest { userId ->
            combine(
                playerCardRepository.getPlayerCardStream(userId),
                habitRepository.getHabitsForTodayStream(userId)
            ) { card, habits ->
                DashboardData(
                    userId = userId,
                    playerCard = card,
                    habits = habits
                )
            }
        }
    }
}
