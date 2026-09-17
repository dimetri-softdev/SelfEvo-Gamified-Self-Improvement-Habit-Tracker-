package com.example.selfevo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val attributeType: String, // PACE, SHOOTING, PASSING, DRIBBLING, DEFENDING, PHYSICAL
    val isCompletedToday: Boolean = false,
    val syncStatus: String = "SYNCED" // SYNCED, PENDING
)
