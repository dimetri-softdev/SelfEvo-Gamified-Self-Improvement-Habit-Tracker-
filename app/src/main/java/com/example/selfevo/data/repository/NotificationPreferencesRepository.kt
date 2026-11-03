package com.example.selfevo.data.repository

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

enum class SelfEvoNotificationTopic(val topicName: String) {
    DAILY_REMINDERS("selfevo_daily_reminders"),
    STREAK_ALERTS("selfevo_streak_alerts")
}

class NotificationPreferencesRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val messaging = FirebaseMessaging.getInstance()
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun isEnabled(topic: SelfEvoNotificationTopic): Boolean =
        preferences.getBoolean(topic.preferenceKey, false)

    suspend fun setEnabled(topic: SelfEvoNotificationTopic, enabled: Boolean) {
        if (enabled) {
            messaging.subscribeToTopic(topic.topicName).await()
        } else {
            messaging.unsubscribeFromTopic(topic.topicName).await()
        }

        preferences.edit().putBoolean(topic.preferenceKey, enabled).apply()
    }

    private val SelfEvoNotificationTopic.preferenceKey: String
        get() = "enabled_${topicName}"

    private companion object {
        const val PREFERENCES_NAME = "notification_preferences"
    }
}
