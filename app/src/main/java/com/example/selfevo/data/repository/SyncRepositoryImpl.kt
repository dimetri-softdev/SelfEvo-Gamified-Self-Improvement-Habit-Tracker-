package com.example.selfevo.data.repository

import android.util.Log
import com.example.selfevo.data.db.dao.HabitDao
import com.example.selfevo.data.db.dao.SyncQueueDao
import com.example.selfevo.data.db.entity.SyncQueueEntity
import com.example.selfevo.data.remote.SelfEvoApiService
import com.example.selfevo.data.remote.dto.CreateHabitRequest
import com.example.selfevo.data.remote.dto.HabitLogRequest
import com.example.selfevo.data.remote.dto.PlayerNameUpdateRequest
import com.example.selfevo.domain.repository.SyncRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val syncQueueDao: SyncQueueDao,
    private val habitDao: HabitDao,
    private val apiService: SelfEvoApiService
) : SyncRepository {

    private val TAG = "SelfEvo_SyncRepo"

    override suspend fun syncPendingLogs(userId: String): Boolean = withContext(Dispatchers.IO) {
        val pendingItems = syncQueueDao.getAllPendingItems()
        for (item in pendingItems) {
            val operation = try {
                JSONObject(item.operation)
            } catch (e: Exception) {
                Log.w(TAG, "Discarding legacy unscoped sync item ${item.id}")
                syncQueueDao.deleteItemFromQueue(item)
                continue
            }
            // Never replay one account's queued operations under another signed-in account.
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
                    "DELETE_HABIT" -> {
                        val response = apiService.deleteHabit(item.habitId)
                        response.isSuccessful || response.code() == 404 || response.code() == 400
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
                    // Keep this item and later items queued so WorkManager can retry them.
                    return@withContext false
                }
            } catch (e: Exception) {
                Log.w(TAG, "Pending sync failed; will retry later", e)
                return@withContext false
            }
        }
        return@withContext true
    }

    override suspend fun queueCompletion(userId: String, habitId: String, completedOn: String) = withContext(Dispatchers.IO) {
        val operation = JSONObject()
            .put("type", "LOG_COMPLETION")
            .put("userId", userId)
            .put("completedOn", completedOn)
        syncQueueDao.addItemToQueue(SyncQueueEntity(habitId = habitId, operation = operation.toString()))
    }

    override suspend fun queueDeleteHabit(userId: String, habitId: String) = withContext(Dispatchers.IO) {
        val pendingItems = syncQueueDao.getAllPendingItems()
        for (item in pendingItems) {
            if (item.habitId == habitId) {
                syncQueueDao.deleteItemFromQueue(item)
            }
        }
        val operation = JSONObject()
            .put("type", "DELETE_HABIT")
            .put("userId", userId)
        syncQueueDao.addItemToQueue(SyncQueueEntity(habitId = habitId, operation = operation.toString()))
    }

    override suspend fun queueHabit(
        userId: String,
        habitId: String,
        title: String,
        description: String,
        attributeType: String,
        frequency: String,
        reminderTime: String
    ) = withContext(Dispatchers.IO) {
        val operation = JSONObject()
            .put("type", "ADD_HABIT")
            .put("userId", userId)
            .put("title", title)
            .put("description", description)
            .put("attributeType", attributeType)
            .put("frequency", frequency)
            .put("reminderTime", reminderTime)
        syncQueueDao.addItemToQueue(SyncQueueEntity(habitId = habitId, operation = operation.toString()))
    }

    override suspend fun queuePlayerNameUpdate(userId: String, name: String) = withContext(Dispatchers.IO) {
        val operation = JSONObject()
            .put("type", "UPDATE_PLAYER_NAME")
            .put("userId", userId)
            .put("playerName", name)
        syncQueueDao.addItemToQueue(SyncQueueEntity(habitId = "", operation = operation.toString()))
    }
}
