package com.example.selfevo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.selfevo.data.sync.SyncWorker
import com.example.selfevo.ui.navigation.SelfEvoApp
import com.example.selfevo.util.lang.LocaleHelper
import com.example.selfevo.util.theme.ThemeManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Settings
        ThemeManager.init(this)
        LocaleHelper.applyLocale(this)

        // Setup Constraint-Aware Periodic WorkManager Synchronization
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

        // Build Content View via Jetpack Compose & Hilt DI
        setContent {
            SelfEvoApp()
        }
    }
}
