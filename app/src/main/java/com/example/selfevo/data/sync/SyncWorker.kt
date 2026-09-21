package com.example.selfevo.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.selfevo.data.local.SelfEvoDatabase
import com.example.selfevo.data.remote.RetrofitClient
import com.example.selfevo.data.repository.HabitRepository

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

            // Use real Retrofit API Service
            val apiService = RetrofitClient.apiService

            val repository = HabitRepository(habitDao, playerCardDao, syncQueueDao, apiService)

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
