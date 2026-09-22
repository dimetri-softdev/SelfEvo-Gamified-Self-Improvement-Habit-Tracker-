package com.example.selfevo.data.repository

import com.example.selfevo.data.db.dao.HabitDao
import com.example.selfevo.data.db.dao.PlayerCardDao
import com.example.selfevo.data.db.dao.SyncQueueDao
import com.example.selfevo.data.db.entity.HabitEntity
import com.example.selfevo.data.db.entity.SyncQueueEntity
import com.example.selfevo.data.model.PlayerCard
import com.example.selfevo.data.remote.SelfEvoApiService
import com.example.selfevo.data.remote.dto.HabitLogRequest
import com.example.selfevo.data.remote.dto.NetworkPlayerCardDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.util.UUID

class HabitRepository(
    private val habitDao: HabitDao,
    private val playerCardDao: PlayerCardDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: SelfEvoApiService
) {

    fun getHabitsStream(userId: String = "default_user"): Flow<List<HabitEntity>> =
        habitDao.getAllHabitsFlow(userId)

    fun getPlayerCardStream(userId: String = "default_user"): Flow<PlayerCard?> =
        playerCardDao.getPlayerCardFlow(userId)

    suspend fun refreshHabits(userId: String = "default_user") {
        try {
            resetDailyHabits(userId)
            val today = LocalDate.now().toString()
            val response = apiService.getHabits()
            if (response.isSuccessful) {
                response.body()?.let { dtoList ->
                    val entities = dtoList.map { dto ->
                        HabitEntity(
                            id = dto.id,
                            userId = userId,
                            title = dto.title,
                            description = dto.description,
                            attributeType = dto.attributeType,
                            isCompletedToday = dto.isCompletedToday,
                            lastCompletedDate = if (dto.isCompletedToday) today else null,
                            frequency = dto.frequency ?: "Daily",
                            reminderTime = dto.reminderTime ?: "07:00",
                            syncStatus = "SYNCED"
                        )
                    }
                    habitDao.insertHabits(entities)
                }
            } else {
                // If offline or API fails, ensure local flags are reset for new day
                val localHabits = habitDao.getAllHabits(userId)
                localHabits.forEach { habit ->
                    if (habit.lastCompletedDate != today) {
                        habitDao.updateHabit(habit.copy(isCompletedToday = false))
                    }
                }
            }
        } catch (e: Exception) {
            // Offline fallback
            val today = LocalDate.now().toString()
            val localHabits = habitDao.getAllHabits(userId)
            localHabits.forEach { habit ->
                if (habit.lastCompletedDate != today && habit.isCompletedToday) {
                    habitDao.updateHabit(habit.copy(isCompletedToday = false))
                }
            }
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
                        resolvedCard.physical > remoteDto.physical) {

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

    suspend fun completeHabit(habitId: String, userId: String = "default_user"): PlayerCard? {
        val habit = habitDao.getHabitById(habitId) ?: return null
        if (habit.isCompletedToday) return playerCardDao.getPlayerCard(userId)

        val today = LocalDate.now().toString()

        // 1. Mark as completed locally
        val updatedHabit = habit.copy(
            isCompletedToday = true,
            lastCompletedDate = today
        )
        habitDao.updateHabit(updatedHabit)

        // 2. Increment active FUT stats locally (+2 pts)
        val activeCard = playerCardDao.getPlayerCard(userId) ?: PlayerCard(id = userId, playerName = "User Player")
        val updatedCard = activeCard.incrementStat(habit.attributeType)
        playerCardDao.insertPlayerCard(updatedCard)

        // 3. Sync to remote
        try {
            val response = apiService.logHabit(HabitLogRequest(habitId = habitId))
            if (!response.isSuccessful) {
                syncQueueDao.addItemToQueue(SyncQueueEntity(habitId = habitId, operation = "LOG_COMPLETION"))
            }
        } catch (e: Exception) {
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
                break
            }
        }
    }

    suspend fun addHabit(userId: String, title: String, description: String, attributeType: String, frequency: String, reminderTime: String) {
        val habit = HabitEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            title = title,
            description = description,
            attributeType = attributeType,
            frequency = frequency,
            reminderTime = reminderTime,
            syncStatus = "PENDING"
        )
        habitDao.insertHabit(habit)
    }

    fun getHabitsForTodayStream(userId: String = "default_user"): Flow<List<HabitEntity>> {
        return habitDao.getAllHabitsFlow(userId).map { habits ->
            val todayName = LocalDate.now().dayOfWeek.name
            habits.filter { habit ->
                // Filter by frequency
                when (habit.frequency.uppercase()) {
                    "DAILY" -> true
                    "WEEKENDS" -> todayName == "SATURDAY" || todayName == "SUNDAY"
                    "WEEKDAYS" -> todayName != "SATURDAY" && todayName != "SUNDAY"
                    else -> habit.frequency.equals(todayName, ignoreCase = true)
                }
            }
        }
    }

    private suspend fun resetDailyHabits(userId: String) {
        val today = LocalDate.now().toString()
        val localHabits = habitDao.getAllHabits(userId)
        val toUpdate = localHabits.filter { it.isCompletedToday && it.lastCompletedDate != today }
        if (toUpdate.isNotEmpty()) {
            habitDao.insertHabits(toUpdate.map { it.copy(isCompletedToday = false) })
        }
    }

    suspend fun updatePlayerName(userId: String, name: String) {
        val currentCard = playerCardDao.getPlayerCard(userId) ?: PlayerCard(id = userId, playerName = name)
        playerCardDao.insertPlayerCard(currentCard.copy(playerName = name))
    }
}
