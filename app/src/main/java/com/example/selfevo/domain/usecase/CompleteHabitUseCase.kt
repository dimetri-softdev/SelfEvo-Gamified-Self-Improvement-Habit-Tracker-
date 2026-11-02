package com.example.selfevo.domain.usecase

import com.example.selfevo.data.model.PlayerCard
import com.example.selfevo.domain.repository.HabitRepository
import com.example.selfevo.domain.repository.PlayerCardRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

data class HabitCompletionResult(
    val updatedCard: PlayerCard?,
    val isLevelUp: Boolean,
    val newOvr: Int
)

class CompleteHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val playerCardRepository: PlayerCardRepository
) {
    suspend operator fun invoke(habitId: String, userId: String): HabitCompletionResult {
        val currentCard = playerCardRepository.getPlayerCardStream(userId).firstOrNull()
        val oldOvr = currentCard?.ovr ?: 0

        val completedHabit = habitRepository.markHabitCompleted(habitId, userId) ?: return HabitCompletionResult(
            updatedCard = currentCard,
            isLevelUp = false,
            newOvr = oldOvr
        )

        val secAttr = completedHabit.secondaryAttributeType
        val updatedCard = if (!secAttr.isNullOrBlank()) {
            playerCardRepository.incrementStat(userId, completedHabit.attributeType, amount = 1)
            playerCardRepository.incrementStat(userId, secAttr, amount = 1)
        } else {
            playerCardRepository.incrementStat(userId, completedHabit.attributeType, amount = 2)
        }

        val newOvr = updatedCard?.ovr ?: oldOvr
        val isLevelUp = newOvr > oldOvr

        return HabitCompletionResult(
            updatedCard = updatedCard,
            isLevelUp = isLevelUp,
            newOvr = newOvr
        )
    }
}
