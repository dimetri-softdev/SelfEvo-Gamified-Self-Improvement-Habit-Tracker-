package com.example.selfevo.data.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.selfevo.data.db.SelfEvoDatabase
import com.example.selfevo.data.remote.RetrofitClient
import com.example.selfevo.data.repository.HabitRepositoryImpl
import com.example.selfevo.data.repository.PlayerCardRepositoryImpl
import com.example.selfevo.data.repository.SyncRepositoryImpl
import com.google.firebase.auth.FirebaseAuth

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val TAG = "SelfEvo_SyncWorker"

    override suspend fun doWork(): Result {
        Log.d(TAG, "Background Sync Job Started.")

        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
                Log.w(TAG, "Sync aborted: No active user session found.")
                return Result.success()
            }

            val database = SelfEvoDatabase.getDatabase(applicationContext)
            val habitDao = database.habitDao()
            val playerCardDao = database.playerCardDao()
            val syncQueueDao = database.syncQueueDao()
            val customAttributeDao = database.customAttributeDao()
            val apiService = RetrofitClient.apiService

            val syncRepo = SyncRepositoryImpl(syncQueueDao, habitDao, apiService)
            val playerCardRepo = PlayerCardRepositoryImpl(playerCardDao, customAttributeDao, apiService, syncRepo)
            val habitRepo = HabitRepositoryImpl(habitDao, customAttributeDao, syncQueueDao, apiService, syncRepo, playerCardRepo)

            Log.d(TAG, "Pushing pending local changes to cloud...")
            if (!syncRepo.syncPendingLogs(userId)) {
                return Result.retry()
            }

            Log.d(TAG, "Pulling latest data from cloud...")
            if (!habitRepo.refreshHabits(userId) || !playerCardRepo.refreshPlayerCard(userId)) {
                return Result.retry()
            }

            Log.i(TAG, "Background Sync Job Completed Successfully.")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Background Sync Error: ${e.message}. Scheduling retry.")
            Result.retry()
        }
    }
}
