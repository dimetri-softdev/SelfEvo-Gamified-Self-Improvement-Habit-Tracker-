package com.example.selfevo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.selfevo.data.local.SelfEvoDatabase
import com.example.selfevo.data.remote.RetrofitClient
import com.example.selfevo.data.repository.HabitRepository
import com.example.selfevo.data.sync.SyncWorker
import com.example.selfevo.ui.dashboard.DashboardViewModel
import com.example.selfevo.ui.navigation.SelfEvoApp
import com.example.selfevo.util.lang.LocaleHelper
import com.example.selfevo.util.theme.ThemeManager
import com.example.selfevo.data.auth.AuthRepository
import java.util.concurrent.TimeUnit
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Settings
        ThemeManager.init(this)
        LocaleHelper.applyLocale(this)

        // 1. Initialize local database DAOs
        val database = SelfEvoDatabase.getDatabase(applicationContext)
        val habitDao = database.habitDao()
        val playerCardDao = database.playerCardDao()
        val syncQueueDao = database.syncQueueDao()

        // 2. Real SelfEvo API Client Integration
        val apiService = RetrofitClient.apiService
        val authRepository = AuthRepository()

        // 3. Construct unified data repository
        val repository = HabitRepository(habitDao, playerCardDao, syncQueueDao, apiService)

        // 4. Construct ViewModel via inline provider factory
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(repository, authRepository) as T
            }
        }
        val viewModel = ViewModelProvider(this, viewModelFactory)[DashboardViewModel::class.java]

        // 5. Setup Constraint-Aware Periodic WorkManager Synchronization
        val syncConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicSyncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(syncConstraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "SelfEvoBackgroundSyncWork",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicSyncRequest
        )

        // 6. Build Content View via Jetpack Compose
        setContent {
            SelfEvoApp(viewModel = viewModel, authRepository = authRepository)
        }
    }
}
