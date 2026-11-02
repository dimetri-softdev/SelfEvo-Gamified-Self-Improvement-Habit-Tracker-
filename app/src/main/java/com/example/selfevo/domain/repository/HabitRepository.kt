package com.example.selfevo.domain.repository

import com.example.selfevo.data.db.entity.CustomAttributeEntity
import com.example.selfevo.data.db.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getHabitsStream(userId: String): Flow<List<HabitEntity>>
    fun getHabitsForTodayStream(userId: String): Flow<List<HabitEntity>>
    fun getCustomAttributesStream(userId: String): Flow<List<CustomAttributeEntity>>
    suspend fun refreshHabits(userId: String): Boolean
    suspend fun markHabitCompleted(habitId: String, userId: String): HabitEntity?
    suspend fun addHabit(
        userId: String,
        title: String,
        description: String,
        attributeType: String,
        secondaryAttributeType: String? = null,
        frequency: String,
        reminderTime: String,
        daysOfWeek: String = "Daily",
        targetDuration: String = "30 mins"
    )
    suspend fun updateHabit(
        habitId: String,
        userId: String,
        title: String,
        description: String = "Custom Habit",
        attributeType: String,
        secondaryAttributeType: String? = null,
        frequency: String,
        reminderTime: String,
        daysOfWeek: String = "Daily",
        targetDuration: String = "30 mins"
    )
    suspend fun deleteHabit(habitId: String, userId: String)
    suspend fun addCustomAttribute(
        userId: String,
        title: String,
        abbreviation: String,
        colorHex: String
    )
}
