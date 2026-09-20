package com.example.selfevo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_card")
data class PlayerCard(
    @PrimaryKey val id: String = "default_user",
    val playerName: String,
    val pace: Int = 50,
    val shooting: Int = 50,
    val passing: Int = 50,
    val skill: Int = 50,
    val defending: Int = 50,
    val physical: Int = 50
) {
    val ovr: Int
        get() = (pace + shooting + passing + skill + defending + physical) / 6

    val tier: String
        get() = when {
            ovr >= 85 -> "Walkout"
            ovr >= 75 -> "Gold"
            ovr >= 65 -> "Silver"
            else -> "Bronze"
        }
}
