package com.example.selfevo.data.repository

import com.example.selfevo.data.local.dao.HabitDao
import com.example.selfevo.data.local.dao.PlayerCardDao
import com.example.selfevo.data.local.dao.SyncQueueDao
import com.example.selfevo.data.local.entity.HabitEntity
import com.example.selfevo.data.local.entity.SyncQueueEntity
import com.example.selfevo.data.model.PlayerCard
import com.example.selfevo.data.remote.SelfEvoApiService
import com.example.selfevo.data.remote.dto.HabitLogRequest
import kotlinx.coroutines.flow.Flow

class HabitRepository(
    private val habitDao: HabitDao,
    private val playerCardDao: PlayerCardDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: SelfEvoApiService
) {

    fun getHabitsStream(): Flow<List<HabitEntity>> = habitDao.getAllHabitsFlow()

    fun getPlayerCardStream(userId: String = "default_user"): Flow<PlayerCard?> =
        playerCardDao.getPlayerCardFlow(userId)

    suspend fun refreshHabits() {
        try {
            val response = apiService.getHabits()
            if (response.isSuccessful) {
                response.body()?.let { dtoList ->
                    val entities = dtoList.map { dto ->
                        HabitEntity(
                            id = dto.id,
                            title = dto.title,
                            description = dto.description,
                            attributeType = dto.attributeType,
                            isCompletedToday = dto.isCompletedToday,
                            frequency = "Daily",
                            reminderTime = "07:00",
                            syncStatus = "SYNCED"
                        )
                    }
                    habitDao.insertHabits(entities)
                }
            }
        } catch (e: Exception) {
            // Offline or network error; fall back to local Room database cache
        }
    }

    suspend fun refreshPlayerCard(userId: String = "default_user") {
    try {
        val response = apiService.getPlayerCard()
        if (response.isSuccessful) {
            val remoteDto = response.body() ?: return
            val localCard = playerCardDao.getPlayerCard()

            if (localCard == null) {
                val card = PlayerCard(
                    id = remoteDto.id.ifBlank { userId },
                    playerName = remoteDto.playerName,
                    pace = remoteDto.pace,
                    shooting = remoteDto.shooting,
                    passing = remoteDto.passing,
                    skill = remoteDto.dribbling,
                    defending = remoteDto.defending,
                    physical = remoteDto.physical
                )
                playerCardDao.insertPlayerCard(card)
            } else {
                val resolvedCard = localCard.copy(
                    pace = maxOf(localCard.pace, remoteDto.pace),
                    shooting = maxOf(localCard.shooting, remoteDto.shooting),
                    passing = maxOf(localCard.passing, remoteDto.passing),
                    skill = maxOf(localCard.skill, remoteDto.dribbling),
                    defending = maxOf(localCard.defending, remoteDto.defending),
                    physical = maxOf(localCard.physical, remoteDto.physical)
                )

                playerCardDao.insertPlayerCard(resolvedCard)

                if (resolvedCard.pace > remoteDto.pace ||
                    resolvedCard.shooting > remoteDto.shooting ||
                    resolvedCard.passing > remoteDto.passing ||
                    resolvedCard.skill > remoteDto.dribbling ||
                    resolvedCard.defending > remoteDto.defending ||
                    resolvedCard.physical > remoteDto.physical
                ) {
                    apiService.syncPlayerCard(
                        NetworkPlayerCardDto(
                            id = resolvedCard.id,
                            playerName = resolvedCard.playerName,
                            pace = resolvedCard.pace,
                            shooting = resolvedCard.shooting,
                            passing = resolvedCard.passing,
                            dribbling = resolvedCard.skill,
                            defending = resolvedCard.defending,
                            physical = resolvedCard.physical
                        )
                    )
                }
            }
        }
    } catch (e: Exception) {
        // Offline fallback
    }
}

    suspend fun completeHabit(habitId: String): PlayerCard? {
        val habit = habitDao.getHabitById(habitId) ?: return null
        if (habit.isCompletedToday) return playerCardDao.getPlayerCard()

        // 1. Mark as completed locally
        val updatedHabit = habit.copy(isCompletedToday = true)
        habitDao.updateHabit(updatedHabit)

        // 2. Increment active FUT stats locally based on attribute type
        val activeCard = playerCardDao.getPlayerCard() ?: PlayerCard(playerName = "User Player")
        val updatedCard = when (habit.attributeType.uppercase()) {
            "PACE" -> activeCard.copy(pace = (activeCard.pace + 1).coerceAtMost(99))
            "SHOOTING" -> activeCard.copy(shooting = (activeCard.shooting + 1).coerceAtMost(99))
            "PASSING" -> activeCard.copy(passing = (activeCard.passing + 1).coerceAtMost(99))
            "SKILL", "DRIBBLING" -> activeCard.copy(skill = (activeCard.skill + 1).coerceAtMost(99))
            "DEFENDING" -> activeCard.copy(defending = (activeCard.defending + 1).coerceAtMost(99))
            "PHYSICAL" -> activeCard.copy(physical = (activeCard.physical + 1).coerceAtMost(99))
            else -> activeCard
        }
        playerCardDao.insertPlayerCard(updatedCard)

        // 3. Sync to remote or add to offline queue
        try {
            val response = apiService.logHabit(HabitLogRequest(habitId = habitId))
            if (!response.isSuccessful) {
                // If endpoint fails but network is alive, add to offline queue
                syncQueueDao.addItemToQueue(SyncQueueEntity(habitId = habitId, operation = "LOG_COMPLETION"))
            }
        } catch (e: Exception) {
            // Network is totally down, safely queue the operation offline
            syncQueueDao.addItemToQueue(SyncQueueEntity(habitId = habitId, operation = "LOG_COMPLETION"))
        }

        return updatedCard
    }

    suspend fun syncPendingLogs() {
        val pendingItems = syncQueueDao.getAllPendingItems()
        for (item in pendingItems) {
            try {
                val response = apiService.logHabit(HabitLogRequest(habitId = item.habitId))
                if (response.isSuccessful) {
                    syncQueueDao.deleteItemFromQueue(item)
                }
            } catch (e: Exception) {
                break // Stop sync if network is still down
            }
        }
    }
}
