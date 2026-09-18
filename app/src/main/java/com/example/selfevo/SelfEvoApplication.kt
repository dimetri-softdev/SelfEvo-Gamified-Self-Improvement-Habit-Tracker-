package com.example.selfevo

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager

class SelfEvoApplication : Application(), Configuration.Provider {

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Explicitly initialize WorkManager on startup to eliminate runtime initialization race conditions
        WorkManager.initialize(this, workManagerConfiguration)
    }
}
