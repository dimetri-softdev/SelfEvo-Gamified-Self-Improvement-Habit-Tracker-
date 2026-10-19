package com.example.selfevo.data.repository

import android.util.Log
import com.example.selfevo.data.db.dao.HabitDao
import com.example.selfevo.data.db.dao.PlayerCardDao
import com.example.selfevo.data.db.dao.SyncQueueDao
import com.example.selfevo.data.db.entity.HabitEntity
import com.example.selfevo.data.db.entity.SyncQueueEntity
import com.example.selfevo.data.model.PlayerCard
import com.example.selfevo.data.remote.SelfEvoApiService
import com.example.selfevo.data.remote.dto.HabitLogRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.util.UUID

/**
 * HabitRepository handles the logic for habit management and player stat progression.
 * It follows the Repository Pattern to provide a unified data interface for the UI.
 * This implementation is "Offline-First", prioritizing local Room DB updates before syncing to Cloud.
 */
class HabitRepository(
    private val habitDao: HabitDao,
    private val playerCardDao: PlayerCardDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: SelfEvoApiService
) {
    private val TAG = "SelfEvo_HabitRepo"

    fun getHabitsStream(userId: String = "default_user"): Flow<List<HabitEntity>> =
        habitDao.getAllHabitsFlow(userId)

    fun getPlayerCardStream(userId: String = "default_user"): Flow<PlayerCard?> =
        playerCardDao.getPlayerCardFlow(userId).map { card ->
            card ?: PlayerCard(id = userId, playerName = "SelfEvo Player")
        }

    /**
     * Refreshes local habits from the remote REST API without clearing user-created habits.
     */
    suspend fun refreshHabits(userId: String = "default_user") {
        Log.d(TAG, "Refreshing habits for user: $userId")
        try {
            ensurePlayerCardExists(userId)
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
                    Log.i(TAG, "Successfully synced ${entities.size} habits from cloud.")
                }
            } else {
                Log.w(TAG, "API refresh failed. Status: ${response.code()}. Using local fallback.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Offline fallback triggered: ${e.message}")
        }
    }

    /**
     * Guarantees that a Player Card always exists in Room DB so UI never stays loading.
     */
    suspend fun ensurePlayerCardExists(userId: String = "default_user", name: String = "SelfEvo Player") {
        val card = playerCardDao.getPlayerCard(userId)
        if (card == null) {
            playerCardDao.insertPlayerCard(PlayerCard(id = userId, playerName = name))
        }
    }

    /**
     * Syncs the Player Card OVR and individual stats with the backend.
     */
    suspend fun refreshPlayerCard(userId: String = "default_user") {
        Log.d(TAG, "Refreshing Player Card for user: $userId")
        try {
            ensurePlayerCardExists(userId)
            val response = apiService.getPlayerCard()
            if (response.isSuccessful) {
                val remoteDto = response.body() ?: return
                val localCard = playerCardDao.getPlayerCard(userId) ?: PlayerCard(id = userId, playerName = remoteDto.playerName)

                val resolvedCard = localCard.copy(
                    playerName = if (localCard.playerName.isNotBlank()) localCard.playerName else remoteDto.playerName,
                    pace = maxOf(localCard.pace, remoteDto.pace),
                    shooting = maxOf(localCard.shooting, remoteDto.shooting),
                    passing = maxOf(localCard.passing, remoteDto.passing),
                    skill = maxOf(localCard.skill, remoteDto.dribbling),
                    defending = maxOf(localCard.defending, remoteDto.defending),
                    physical = maxOf(localCard.physical, remoteDto.physical)
                )
                playerCardDao.insertPlayerCard(resolvedCard)
                Log.i(TAG, "Player Card synchronization complete.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Player Card sync error: ${e.message}")
            ensurePlayerCardExists(userId)
        }
    }

    /**
     * Core Logic: Logs a habit completion and increments stats.
     */
    suspend fun completeHabit(habitId: String, userId: String = "default_user"): PlayerCard? {
        Log.d(TAG, "Action: Completing Habit ID $habitId")
        val habit = habitDao.getHabitById(habitId) ?: return null
        if (habit.isCompletedToday) return playerCardDao.getPlayerCard(userId)

        val today = LocalDate.now().toString()

        val updatedHabit = habit.copy(isCompletedToday = true, lastCompletedDate = today)
        habitDao.updateHabit(updatedHabit)

        val activeCard = playerCardDao.getPlayerCard(userId) ?: PlayerCard(id = userId, playerName = "SelfEvo Player")
        val updatedCard = activeCard.incrementStat(habit.attributeType)
        playerCardDao.insertPlayerCard(updatedCard)
        Log.i(TAG, "Stat Growth: ${habit.attributeType} increased. New OVR: ${updatedCard.ovr}")

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
        Log.d(TAG, "New user habit created locally: $title linked to $attributeType")
    }

    fun getHabitsForTodayStream(userId: String = "default_user"): Flow<List<HabitEntity>> {
        return habitDao.getAllHabitsFlow(userId)
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
        Log.d(TAG, "Updating Player Name to: $name")
        val currentCard = playerCardDao.getPlayerCard(userId) ?: PlayerCard(id = userId, playerName = name)
        playerCardDao.insertPlayerCard(currentCard.copy(playerName = name))
    }
}
