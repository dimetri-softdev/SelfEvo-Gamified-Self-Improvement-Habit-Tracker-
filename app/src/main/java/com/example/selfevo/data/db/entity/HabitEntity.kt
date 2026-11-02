package com.example.selfevo.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habits",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["userId", "isCompletedToday"])
    ]
)
data class HabitEntity(
    @PrimaryKey val id: String,
    val userId: String = "default_user",
    val title: String,
    val description: String,
    val attributeType: String, // e.g. PAC, SHO, PAS, SKL, DEF, PHY or Custom Abbreviation
    val secondaryAttributeType: String? = null, // Optional 2nd attribute for +1/+1 split
    val isCompletedToday: Boolean = false,
    val lastCompletedDate: String? = null, // yyyy-MM-dd
    val frequency: String = "Daily",
    val reminderTime: String = "07:00",
    val daysOfWeek: String = "Daily", // e.g. "MON,WED,FRI" or "Daily"
    val targetDuration: String = "30 mins", // e.g. "30 mins", "10 reps"
    val syncStatus: String = "SYNCED" // SYNCED, PENDING
)
