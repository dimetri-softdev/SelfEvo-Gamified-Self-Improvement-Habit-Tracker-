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
    val physical: Int = 50,
    val avatarUri: String? = null,
    val equippedTheme: String = "BRONZE",
    val slot1: String = "PAC",
    val slot2: String = "SHO",
    val slot3: String = "PAS",
    val slot4: String = "SKL",
    val slot5: String = "DEF",
    val slot6: String = "PHY"
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

    fun incrementStat(attributeType: String, amount: Int = 1): PlayerCard {
        return when (attributeType.uppercase()) {
            "PAC", "PACE" -> copy(pace = (pace + amount).coerceAtMost(99))
            "SHO", "SHOOTING" -> copy(shooting = (shooting + amount).coerceAtMost(99))
            "PAS", "PASSING" -> copy(passing = (passing + amount).coerceAtMost(99))
            "SKL", "SKILL", "DRIBBLING" -> copy(skill = (skill + amount).coerceAtMost(99))
            "DEF", "DEFENDING" -> copy(defending = (defending + amount).coerceAtMost(99))
            "PHY", "PHYSICAL" -> copy(physical = (physical + amount).coerceAtMost(99))
            else -> this
        }
    }
}
