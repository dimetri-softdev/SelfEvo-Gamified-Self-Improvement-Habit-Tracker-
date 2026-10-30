package com.example.selfevo.data.repository

import android.util.Log
import com.example.selfevo.data.db.dao.HabitDao
import com.example.selfevo.data.db.dao.PlayerCardDao
import com.example.selfevo.data.db.dao.SyncQueueDao
import com.example.selfevo.data.db.entity.HabitEntity
import com.example.selfevo.data.db.entity.SyncQueueEntity
import com.example.selfevo.data.model.PlayerCard
import com.example.selfevo.data.remote.SelfEvoApiService
import com.example.selfevo.data.remote.dto.CreateHabitRequest
import com.example.selfevo.data.remote.dto.HabitLogRequest
import com.example.selfevo.data.remote.dto.PlayerNameUpdateRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import java.time.LocalDate
import java.util.UUID

/**
 * HabitRepository handles the logic for habit management and player stat progression.
 * It integrates the offline Room cache with the authenticated PostgreSQL-backed REST API.
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
     * Refreshes local habits from the authenticated REST API.
     */
    suspend fun refreshHabits(userId: String = "default_user"): Boolean {
        Log.d(TAG, "Refreshing habits for user: $userId")
        try {
            ensurePlayerCardExists(userId)
            resetDailyHabits(userId)
            val today = LocalDate.now().toString()
            val response = apiService.getHabits(today)
            if (!response.isSuccessful) {
                Log.w(TAG, "Habit refresh failed with HTTP ${response.code()}")
                return false
            }
            val dtoList = response.body() ?: return false
            val entities = dtoList.map { dto ->
                HabitEntity(
                    id = dto.id,
                    userId = userId,
                    title = dto.title,
                    description = dto.description,
                    attributeType = dto.attributeType,
                    isCompletedToday = dto.isCompletedToday,
                    lastCompletedDate = if (dto.isCompletedToday) today else null,
                    frequency = dto.frequency,
                    reminderTime = dto.reminderTime,
                    syncStatus = "SYNCED"
                )
            }
            habitDao.insertHabits(entities)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Habit refresh failed; keeping local data", e)
            return false
        }
    }

    /**
     * Guarantees that a local Player Card exists before remote data is available.
     */
    suspend fun ensurePlayerCardExists(userId: String = "default_user", name: String = "SelfEvo Player") {
        val card = playerCardDao.getPlayerCard(userId)
        if (card == null) {
            playerCardDao.insertPlayerCard(PlayerCard(id = userId, playerName = name))
        }
    }

    /**
     * Syncs the Player Card OVR and stats with the backend.
     */
    suspend fun refreshPlayerCard(userId: String = "default_user"): Boolean {
        Log.d(TAG, "Refreshing Player Card for user: $userId")
        try {
            ensurePlayerCardExists(userId)
            val response = apiService.getPlayerCard()
            if (!response.isSuccessful) {
                Log.w(TAG, "Player card refresh failed with HTTP ${response.code()}")
                return false
            }
            val remoteDto = response.body() ?: return false
            val localCard = playerCardDao.getPlayerCard(userId) ?: PlayerCard(id = userId, playerName = remoteDto.playerName)
            val resolvedCard = localCard.copy(
                playerName = remoteDto.playerName.ifBlank { localCard.playerName },
                pace = maxOf(localCard.pace, remoteDto.pace),
                shooting = maxOf(localCard.shooting, remoteDto.shooting),
                passing = maxOf(localCard.passing, remoteDto.passing),
                skill = maxOf(localCard.skill, remoteDto.dribbling),
                defending = maxOf(localCard.defending, remoteDto.defending),
                physical = maxOf(localCard.physical, remoteDto.physical)
            )
            playerCardDao.insertPlayerCard(resolvedCard)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Player card refresh failed; keeping local data", e)
            ensurePlayerCardExists(userId)
            return false
        }
    }

    /**
     * Core Logic: Logs a habit completion locally and queues a retryable server sync.
     */
    suspend fun completeHabit(habitId: String, userId: String = "default_user"): PlayerCard? {
        Log.d(TAG, "Action: Completing Habit ID $habitId")
        val habit = habitDao.getHabitById(habitId, userId) ?: return null
        if (habit.isCompletedToday) return playerCardDao.getPlayerCard(userId)

        val today = LocalDate.now().toString()
        if (habitDao.markCompletedToday(habitId, userId, today) == 0) {
            return playerCardDao.getPlayerCard(userId)
        }
        val activeCard = playerCardDao.getPlayerCard(userId) ?: PlayerCard(id = userId, playerName = "SelfEvo Player")
        val updatedCard = activeCard.incrementStat(habit.attributeType)
        playerCardDao.insertPlayerCard(updatedCard)
        Log.i(TAG, "Stat Growth: ${habit.attributeType} increased. New OVR: ${updatedCard.ovr}")

        try {
            val response = apiService.logHabit(HabitLogRequest(habitId = habitId, completedOn = today))
            if (!response.isSuccessful || response.body()?.success != true) {
                queueOperation("LOG_COMPLETION", userId, habitId, completedOn = today)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Habit completion queued for sync", e)
            queueOperation("LOG_COMPLETION", userId, habitId, completedOn = today)
        }

        return updatedCard
    }

    suspend fun syncPendingLogs(userId: String = "default_user"): Boolean {
        val pendingItems = syncQueueDao.getAllPendingItems()
        for (item in pendingItems) {
            val operation = try {
                JSONObject(item.operation)
            } catch (e: Exception) {
                Log.w(TAG, "Discarding legacy unscoped sync item ${item.id}")
                syncQueueDao.deleteItemFromQueue(item)
                continue
            }
            if (operation.optString("userId") != userId) continue

            try {
                val success = when (operation.optString("type")) {
                    "LOG_COMPLETION" -> {
                        val response = apiService.logHabit(
                            HabitLogRequest(
                                habitId = item.habitId,
                                completedOn = operation.getString("completedOn")
                            )
                        )
                        response.isSuccessful && response.body()?.success == true
                    }
                    "ADD_HABIT" -> {
                        val response = apiService.createHabit(
                            CreateHabitRequest(
                                id = item.habitId,
                                title = operation.getString("title"),
                                description = operation.getString("description"),
                                attributeType = operation.getString("attributeType"),
                                frequency = operation.getString("frequency"),
                                reminderTime = operation.getString("reminderTime")
                            )
                        )
                        if (response.isSuccessful) {
                            habitDao.getHabitById(item.habitId, userId)?.let {
                                habitDao.updateHabit(it.copy(syncStatus = "SYNCED"))
                            }
                        }
                        response.isSuccessful
                    }
                    "UPDATE_PLAYER_NAME" -> {
                        val response = apiService.updatePlayerName(
                            PlayerNameUpdateRequest(operation.getString("playerName"))
                        )
                        response.isSuccessful
                    }
                    else -> {
                        Log.w(TAG, "Discarding unknown sync operation in item ${item.id}")
                        syncQueueDao.deleteItemFromQueue(item)
                        true
                    }
                }
                if (success) {
                    syncQueueDao.deleteItemFromQueue(item)
                } else {
                    return false
                }
            } catch (e: Exception) {
                Log.w(TAG, "Pending sync failed; will retry later", e)
                return false
            }
        }
        return true
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

        try {
            val response = apiService.createHabit(
                CreateHabitRequest(
                    id = habit.id,
                    title = habit.title,
                    description = habit.description,
                    attributeType = habit.attributeType,
                    frequency = habit.frequency,
                    reminderTime = habit.reminderTime
                )
            )
            if (response.isSuccessful) {
                habitDao.updateHabit(habit.copy(syncStatus = "SYNCED"))
            } else {
                queueHabit(habit)
            }
        } catch (e: Exception) {
            Log.w(TAG, "New habit queued for sync", e)
            queueHabit(habit)
        }
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
        val currentCard = playerCardDao.getPlayerCard(userId) ?: PlayerCard(id = userId, playerName = name)
        val updatedCard = currentCard.copy(playerName = name)
        playerCardDao.insertPlayerCard(updatedCard)

        try {
            val response = apiService.updatePlayerName(PlayerNameUpdateRequest(name))
            if (!response.isSuccessful) queueOperation("UPDATE_PLAYER_NAME", userId, playerName = name)
        } catch (e: Exception) {
            Log.w(TAG, "Player name queued for sync", e)
            queueOperation("UPDATE_PLAYER_NAME", userId, playerName = name)
        }
    }

    private suspend fun queueHabit(habit: HabitEntity) {
        val operation = JSONObject()
            .put("type", "ADD_HABIT")
            .put("userId", habit.userId)
            .put("title", habit.title)
            .put("description", habit.description)
            .put("attributeType", habit.attributeType)
            .put("frequency", habit.frequency)
            .put("reminderTime", habit.reminderTime)
        syncQueueDao.addItemToQueue(SyncQueueEntity(habitId = habit.id, operation = operation.toString()))
    }

    private suspend fun queueOperation(
        type: String,
        userId: String,
        habitId: String = "",
        playerName: String = "",
        completedOn: String = ""
    ) {
        val operation = JSONObject()
            .put("type", type)
            .put("userId", userId)
            .put("playerName", playerName)
            .put("completedOn", completedOn)
        syncQueueDao.addItemToQueue(SyncQueueEntity(habitId = habitId, operation = operation.toString()))
    }
}
