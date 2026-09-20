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
        // Initialize WorkManager manually since default initializer was removed in manifest
        WorkManager.initialize(this, workManagerConfiguration)
    }
}
