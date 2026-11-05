package com.example.selfevo.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.selfevo.MainActivity
import com.example.selfevo.R
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class HabitReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "Habit Reminder Worker started.")
        sendHabitReminderNotification()
        return Result.success()
    }

    private fun sendHabitReminderNotification() {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "selfevo_habit_reminders"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Habit Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Daily habit reminder notifications"
        }
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(applicationContext.getString(R.string.habit_reminder_title))
            .setContentText(applicationContext.getString(R.string.habit_reminder_message))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val TAG = "HabitReminderWorker"
        private const val WORK_NAME = "habit_reminder_work"
        private const val NOTIFICATION_ID = 1001

        fun schedule(context: Context, time: String) {
            require(SCHEDULE_TIME_PATTERN.matches(time)) { "Reminder time must use HH:mm format." }
            val scheduledTime = LocalTime.parse(time)
            val now = LocalTime.now()
            val delayMillis = Duration.between(now, scheduledTime).toMillis()
                .let { if (it <= 0) it + TimeUnit.DAYS.toMillis(1) else it }
            val workRequest = PeriodicWorkRequestBuilder<HabitReminderWorker>(
                24, TimeUnit.HOURS
            )
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
            preferences(context).edit()
                .putBoolean(KEY_ENABLED, true)
                .putString(KEY_TIME, time)
                .apply()
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            preferences(context).edit().putBoolean(KEY_ENABLED, false).apply()
        }

        fun isScheduled(context: Context): Boolean =
            preferences(context).getBoolean(KEY_ENABLED, false)

        fun getScheduledTime(context: Context): String =
            preferences(context).getString(KEY_TIME, DEFAULT_TIME) ?: DEFAULT_TIME

        fun isValidTime(time: String): Boolean = SCHEDULE_TIME_PATTERN.matches(time)

        private fun preferences(context: Context) =
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        private const val PREFERENCES_NAME = "habit_reminder_preferences"
        private const val KEY_ENABLED = "enabled"
        private const val KEY_TIME = "scheduled_time"
        private const val DEFAULT_TIME = "07:30"
        private val SCHEDULE_TIME_PATTERN = Regex("^(?:[01]\\d|2[0-3]):[0-5]\\d$")
    }
}
