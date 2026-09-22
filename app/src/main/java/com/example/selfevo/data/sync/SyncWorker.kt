package com.example.selfevo.data.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.selfevo.data.db.SelfEvoDatabase
import com.example.selfevo.data.remote.RetrofitClient
import com.example.selfevo.data.repository.HabitRepository
import com.google.firebase.auth.FirebaseAuth

/**
 * SyncWorker is the automated background engine for data consistency.
 * It is managed by Jetpack WorkManager and handles offline-to-online data propagation.
 */
class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val TAG = "SelfEvo_SyncWorker"

    override suspend fun doWork(): Result {
        Log.d(TAG, "Background Sync Job Started.")

        return try {
            // Identify the current user to scoped background data operations
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
                Log.w(TAG, "Sync aborted: No active user session found.")
                return Result.success()
            }

            // 1. Initialize data layer handles inside the worker context
            val database = SelfEvoDatabase.getDatabase(applicationContext)
            val habitDao = database.habitDao()
            val playerCardDao = database.playerCardDao()
            val syncQueueDao = database.syncQueueDao()

            // Connect to real Retrofit production client
            val apiService = RetrofitClient.apiService
            val repository = HabitRepository(habitDao, playerCardDao, syncQueueDao, apiService)

            // 2. Perform synchronized heavy lifting operations
            Log.d(TAG, "Pushing pending local changes to cloud...")
            repository.syncPendingLogs()

            Log.d(TAG, "Pulling latest data from cloud...")
            repository.refreshHabits(userId)
            repository.refreshPlayerCard(userId)

            Log.i(TAG, "Background Sync Job Completed Successfully.")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Background Sync Error: ${e.message}. Scheduling retry.")
            Result.retry()
        }
    }
}
