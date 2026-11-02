package com.example.selfevo.domain.repository

import com.example.selfevo.data.model.PlayerCard
import kotlinx.coroutines.flow.Flow

interface PlayerCardRepository {
    fun getPlayerCardStream(userId: String): Flow<PlayerCard?>
    suspend fun ensurePlayerCardExists(userId: String, name: String = "SelfEvo Player")
    suspend fun refreshPlayerCard(userId: String): Boolean
    suspend fun updatePlayerName(userId: String, name: String)
    suspend fun updateAvatarUri(userId: String, avatarUri: String?)
    suspend fun updateEquippedTheme(userId: String, theme: String)
    suspend fun updateCardSlots(userId: String, slot1: String, slot2: String, slot3: String, slot4: String, slot5: String, slot6: String)
    suspend fun incrementStat(userId: String, attributeType: String, amount: Int = 1): PlayerCard?
}
