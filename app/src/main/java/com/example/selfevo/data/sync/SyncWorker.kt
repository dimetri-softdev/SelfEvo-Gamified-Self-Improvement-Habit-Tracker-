package com.example.selfevo.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.selfevo.data.local.SelfEvoDatabase
import com.example.selfevo.data.remote.SelfEvoApiService
import com.example.selfevo.data.remote.dto.AuthResponse
import com.example.selfevo.data.remote.dto.HabitLogRequest
import com.example.selfevo.data.remote.dto.HabitLogResponse
import com.example.selfevo.data.remote.dto.LoginRequest
import com.example.selfevo.data.remote.dto.NetworkHabitDto
import com.example.selfevo.data.remote.dto.NetworkPlayerCardDto
import com.example.selfevo.data.repository.HabitRepository
import retrofit2.Response

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 1. Initialize data layer handles inside background process context
            val database = SelfEvoDatabase.getDatabase(applicationContext)
            val habitDao = database.habitDao()
            val playerCardDao = database.playerCardDao()
            val syncQueueDao = database.syncQueueDao()

            // Dynamic API Placeholder for standalone worker instantiation logic
            val apiServicePlaceholder = object : SelfEvoApiService {
                override suspend fun login(request: LoginRequest): Response<AuthResponse> =
                    Response.success(AuthResponse("dummy", request.email, "Player"))

                override suspend fun getHabits(): Response<List<NetworkHabitDto>> =
                    Response.success(emptyList())

                override suspend fun logHabit(request: HabitLogRequest): Response<HabitLogResponse> =
                    Response.success(HabitLogResponse(true, "Synced", null))

                override suspend fun getPlayerCard(): Response<NetworkPlayerCardDto> =
                    Response.success(NetworkPlayerCardDto("default_user", "Pro Tracker", 50, 50, 50, 50, 50, 50))
            }

            val repository = HabitRepository(habitDao, playerCardDao, syncQueueDao, apiServicePlaceholder)

            // 2. Dispatch data synchronization tasks
            repository.syncPendingLogs()
            repository.refreshHabits()
            repository.refreshPlayerCard()

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
