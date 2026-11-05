package com.example.selfevo.data.repository

import android.util.Log
import com.example.selfevo.data.db.dao.CustomAttributeDao
import com.example.selfevo.data.db.dao.HabitDao
import com.example.selfevo.data.db.dao.SyncQueueDao
import com.example.selfevo.data.db.entity.CustomAttributeEntity
import com.example.selfevo.data.db.entity.HabitEntity
import com.example.selfevo.data.remote.SelfEvoApiService
import com.example.selfevo.data.remote.dto.CreateHabitRequest
import com.example.selfevo.data.remote.dto.HabitLogRequest
import com.example.selfevo.domain.repository.HabitRepository
import com.example.selfevo.domain.repository.PlayerCardRepository
import com.example.selfevo.domain.repository.SyncRepository
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val customAttributeDao: CustomAttributeDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: SelfEvoApiService,
    private val syncRepository: SyncRepository,
    private val playerCardRepository: PlayerCardRepository
) : HabitRepository {

    private val TAG = "SelfEvo_HabitRepo"

    override fun getHabitsStream(userId: String): Flow<List<HabitEntity>> =
        habitDao.getAllHabitsFlow(userId)

    override fun getHabitsForTodayStream(userId: String): Flow<List<HabitEntity>> =
        habitDao.getAllHabitsFlow(userId)

    override fun getCustomAttributesStream(userId: String): Flow<List<CustomAttributeEntity>> =
        customAttributeDao.getCustomAttributesFlow(userId)

    override suspend fun refreshHabits(userId: String): Boolean = withContext(Dispatchers.IO) {
        Log.d(TAG, "Refreshing habits for user: $userId")
        try {
            playerCardRepository.ensurePlayerCardExists(userId)
            resetDailyHabits(userId)
            val today = LocalDate.now().toString()
            val response = apiService.getHabits(today)
            if (!response.isSuccessful) {
                Log.w(TAG, "Habit refresh failed with HTTP ${response.code()}")
                return@withContext false
            }
            val dtoList = response.body() ?: return@withContext false

            val pendingItems = syncQueueDao.getAllPendingItems()
            // Room is authoritative for queued writes until the server confirms them.
            val pendingOperations = pendingItems.mapNotNull { item ->
                try {
                    item to JsonParser.parseString(item.operation).asJsonObject
                } catch (e: Exception) {
                    Log.w(TAG, "Skipping malformed sync queue operation")
                    null
                }
            }
            val pendingDeleteHabitIds = pendingOperations.filter { (_, op) ->
                op.stringValue("type") == "DELETE_HABIT" && op.stringValue("userId") == userId
            }.map { (item, _) -> item.habitId }.toSet()
            val pendingCompletions = pendingOperations.filter { (_, op) ->
                op.stringValue("type") == "LOG_COMPLETION" &&
                    op.stringValue("userId") == userId &&
                    op.stringValue("completedOn") == today
            }.map { (item, _) -> item.habitId }.toSet()

            val filteredDtoList = dtoList.filterNot { pendingDeleteHabitIds.contains(it.id) }
            val remoteIds = filteredDtoList.map { it.id }.toSet()

            val localHabits = habitDao.getAllHabits(userId)
            val habitsToDelete = localHabits.filter {
                it.syncStatus == "SYNCED" && !remoteIds.contains(it.id)
            }
            for (h in habitsToDelete) {
                habitDao.deleteHabitById(h.id, userId)
            }

            val entities = filteredDtoList.map { dto ->
                HabitEntity(
                    id = dto.id,
                    userId = userId,
                    title = dto.title,
                    description = dto.description,
                    attributeType = dto.attributeType,
                    isCompletedToday = dto.isCompletedToday || dto.id in pendingCompletions,
                    lastCompletedDate = if (dto.isCompletedToday || dto.id in pendingCompletions) today else null,
                    frequency = dto.frequency,
                    reminderTime = dto.reminderTime,
                    daysOfWeek = dto.daysOfWeek ?: "Daily",
                    targetDuration = dto.targetDuration ?: "30 mins",
                    syncStatus = "SYNCED"
                )
            }
            habitDao.insertHabits(entities)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Habit refresh failed; keeping local data", e)
            false
        }
    }

    override suspend fun markHabitCompleted(habitId: String, userId: String): HabitEntity? = withContext(Dispatchers.IO) {
        val habit = habitDao.getHabitById(habitId, userId) ?: return@withContext null
        // Avoid awarding attribute progress twice for a habit already completed today.
        if (habit.isCompletedToday) return@withContext null

        val today = LocalDate.now().toString()
        if (habitDao.markCompletedToday(habitId, userId, today) == 0) {
            return@withContext null
        }

        try {
            val response = apiService.logHabit(HabitLogRequest(habitId = habitId, completedOn = today))
            if (!response.isSuccessful || response.body()?.success != true) {
                syncRepository.queueCompletion(userId, habitId, today)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Habit completion queued for sync", e)
            syncRepository.queueCompletion(userId, habitId, today)
        }

        habit.copy(isCompletedToday = true, lastCompletedDate = today)
    }

    override suspend fun addHabit(
        userId: String,
        title: String,
        description: String,
        attributeType: String,
        secondaryAttributeType: String?,
        frequency: String,
        reminderTime: String,
        daysOfWeek: String,
        targetDuration: String
    ) = withContext(Dispatchers.IO) {
        val habit = HabitEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            title = title,
            description = description,
            attributeType = attributeType,
            secondaryAttributeType = secondaryAttributeType,
            frequency = frequency,
            reminderTime = reminderTime,
            daysOfWeek = daysOfWeek,
            targetDuration = targetDuration,
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
                    reminderTime = habit.reminderTime,
                    daysOfWeek = habit.daysOfWeek,
                    targetDuration = habit.targetDuration
                )
            )
            if (response.isSuccessful) {
                habitDao.updateHabit(habit.copy(syncStatus = "SYNCED"))
            } else {
                syncRepository.queueHabit(
                    userId = userId,
                    habitId = habit.id,
                    title = habit.title,
                    description = habit.description,
                    attributeType = habit.attributeType,
                    frequency = habit.frequency,
                    reminderTime = habit.reminderTime
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "New habit queued for sync", e)
            syncRepository.queueHabit(
                userId = userId,
                habitId = habit.id,
                title = habit.title,
                description = habit.description,
                attributeType = habit.attributeType,
                frequency = habit.frequency,
                reminderTime = habit.reminderTime
            )
        }
    }

    override suspend fun updateHabit(
        habitId: String,
        userId: String,
        title: String,
        description: String,
        attributeType: String,
        secondaryAttributeType: String?,
        frequency: String,
        reminderTime: String,
        daysOfWeek: String,
        targetDuration: String
    ) = withContext(Dispatchers.IO) {
        val existing = habitDao.getHabitById(habitId, userId)
        val updated = (existing ?: HabitEntity(
            id = habitId,
            userId = userId,
            title = title,
            description = description,
            attributeType = attributeType,
            secondaryAttributeType = secondaryAttributeType,
            frequency = frequency,
            reminderTime = reminderTime,
            daysOfWeek = daysOfWeek,
            targetDuration = targetDuration
        )).copy(
            title = title,
            description = description,
            attributeType = attributeType,
            secondaryAttributeType = secondaryAttributeType,
            frequency = frequency,
            reminderTime = reminderTime,
            daysOfWeek = daysOfWeek,
            targetDuration = targetDuration,
            syncStatus = "PENDING"
        )
        habitDao.updateHabit(updated)
    }

    override suspend fun deleteHabit(habitId: String, userId: String) = withContext(Dispatchers.IO) {
        val existingHabit = habitDao.getHabitById(habitId, userId)
        habitDao.deleteHabit(habitId)

        if (existingHabit?.syncStatus == "PENDING") {
            val pendingItems = syncQueueDao.getAllPendingItems()
            for (item in pendingItems) {
                if (item.habitId == habitId) {
                    syncQueueDao.deleteItemFromQueue(item)
                }
            }
            return@withContext
        }

        try {
            val response = apiService.deleteHabit(habitId)
            if (!response.isSuccessful && response.code() != 404 && response.code() != 400) {
                syncRepository.queueDeleteHabit(userId, habitId)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Delete habit queued for sync", e)
            syncRepository.queueDeleteHabit(userId, habitId)
        }
    }

    override suspend fun addCustomAttribute(
        userId: String,
        title: String,
        abbreviation: String,
        colorHex: String
    ) = withContext(Dispatchers.IO) {
        val customAttr = CustomAttributeEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            title = title,
            abbreviation = abbreviation.uppercase().take(3),
            colorHex = colorHex,
            score = 0 // Starts at rating 0 per user requirement
        )
        customAttributeDao.insertCustomAttribute(customAttr)
    }

    private suspend fun resetDailyHabits(userId: String) {
        val today = LocalDate.now().toString()
        val localHabits = habitDao.getAllHabits(userId)
        val toUpdate = localHabits.filter { it.isCompletedToday && it.lastCompletedDate != today }
        if (toUpdate.isNotEmpty()) {
            habitDao.insertHabits(toUpdate.map { it.copy(isCompletedToday = false) })
        }
    }

    private fun JsonObject.stringValue(name: String): String? =
        get(name)?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isString }?.asString
}
