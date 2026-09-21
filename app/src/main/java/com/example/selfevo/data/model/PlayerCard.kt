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

    fun incrementStat(attributeType: String, amount: Int = 2): PlayerCard {
        return when (attributeType.uppercase()) {
            "PACE" -> copy(pace = (pace + amount).coerceAtMost(99))
            "SHOOTING" -> copy(shooting = (shooting + amount).coerceAtMost(99))
            "PASSING" -> copy(passing = (passing + amount).coerceAtMost(99))
            "SKILL", "DRIBBLING" -> copy(skill = (skill + amount).coerceAtMost(99))
            "DEFENDING" -> copy(defending = (defending + amount).coerceAtMost(99))
            "PHYSICAL" -> copy(physical = (physical + amount).coerceAtMost(99))
            else -> this
        }
    }
}
