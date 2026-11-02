package com.example.selfevo.domain.repository

interface SyncRepository {
    suspend fun syncPendingLogs(userId: String): Boolean
    suspend fun queueCompletion(userId: String, habitId: String, completedOn: String)
    suspend fun queueHabit(userId: String, habitId: String, title: String, description: String, attributeType: String, frequency: String, reminderTime: String)
    suspend fun queueDeleteHabit(userId: String, habitId: String)
    suspend fun queuePlayerNameUpdate(userId: String, name: String)
}
