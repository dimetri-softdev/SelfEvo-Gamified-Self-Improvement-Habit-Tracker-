package com.example.selfevo.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_attributes")
data class CustomAttributeEntity(
    @PrimaryKey val id: String,
    val userId: String = "default_user",
    val title: String,
    val abbreviation: String,
    val colorHex: String = "#00E5FF",
    val score: Int = 0 // Custom attributes start at rating 0 per user specification
)
