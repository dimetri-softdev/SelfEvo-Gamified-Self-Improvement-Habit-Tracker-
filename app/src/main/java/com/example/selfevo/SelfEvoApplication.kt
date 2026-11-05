package com.example.selfevo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import com.example.selfevo.R

@HiltAndroidApp
class SelfEvoApplication : Application(), Configuration.Provider {

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Initialize WorkManager manually since default initializer was removed in manifest
        WorkManager.initialize(this, workManagerConfiguration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    getString(R.string.push_notification_channel),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
    }

    private companion object {
        const val NOTIFICATION_CHANNEL_ID = "selfevo_notifications"
    }
}
