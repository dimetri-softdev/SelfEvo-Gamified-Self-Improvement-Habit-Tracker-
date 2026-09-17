package com.example.selfevo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val habitId: String,
    val operation: String, // LOG_COMPLETION
    val timestamp: Long = System.currentTimeMillis()
)
