package com.example.selfevo.data.repository

import android.util.Log
import com.example.selfevo.data.db.dao.CustomAttributeDao
import com.example.selfevo.data.db.dao.PlayerCardDao
import com.example.selfevo.data.model.PlayerCard
import com.example.selfevo.data.remote.SelfEvoApiService
import com.example.selfevo.data.remote.dto.PlayerNameUpdateRequest
import com.example.selfevo.domain.repository.PlayerCardRepository
import com.example.selfevo.domain.repository.SyncRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerCardRepositoryImpl @Inject constructor(
    private val playerCardDao: PlayerCardDao,
    private val customAttributeDao: CustomAttributeDao,
    private val apiService: SelfEvoApiService,
    private val syncRepository: SyncRepository
) : PlayerCardRepository {

    private val TAG = "SelfEvo_PlayerCardRepo"

    override fun getPlayerCardStream(userId: String): Flow<PlayerCard?> =
        playerCardDao.getPlayerCardFlow(userId).map { card ->
            card ?: PlayerCard(id = userId, playerName = "SelfEvo Player")
        }

    override suspend fun ensurePlayerCardExists(userId: String, name: String) = withContext(Dispatchers.IO) {
        val card = playerCardDao.getPlayerCard(userId)
        if (card == null) {
            playerCardDao.insertPlayerCard(PlayerCard(id = userId, playerName = name))
        }
    }

    override suspend fun refreshPlayerCard(userId: String): Boolean = withContext(Dispatchers.IO) {
        Log.d(TAG, "Refreshing Player Card for user: $userId")
        try {
            ensurePlayerCardExists(userId)
            val response = apiService.getPlayerCard()
            if (!response.isSuccessful) {
                Log.w(TAG, "Player card refresh failed with HTTP ${response.code()}")
                return@withContext false
            }
            val remoteDto = response.body() ?: return@withContext false
            val localCard = playerCardDao.getPlayerCard(userId) ?: PlayerCard(id = userId, playerName = remoteDto.playerName)
            val resolvedCard = localCard.copy(
                playerName = remoteDto.playerName.ifBlank { localCard.playerName },
                pace = maxOf(localCard.pace, remoteDto.pace),
                shooting = maxOf(localCard.shooting, remoteDto.shooting),
                passing = maxOf(localCard.passing, remoteDto.passing),
                skill = maxOf(localCard.skill, remoteDto.dribbling),
                defending = maxOf(localCard.defending, remoteDto.defending),
                physical = maxOf(localCard.physical, remoteDto.physical)
            )
            playerCardDao.insertPlayerCard(resolvedCard)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Player card refresh failed; keeping local data", e)
            ensurePlayerCardExists(userId)
            false
        }
    }

    override suspend fun updatePlayerName(userId: String, name: String) = withContext(Dispatchers.IO) {
        val currentCard = playerCardDao.getPlayerCard(userId) ?: PlayerCard(id = userId, playerName = name)
        val updatedCard = currentCard.copy(playerName = name)
        playerCardDao.insertPlayerCard(updatedCard)

        try {
            val response = apiService.updatePlayerName(PlayerNameUpdateRequest(name))
            if (!response.isSuccessful) syncRepository.queuePlayerNameUpdate(userId, name)
        } catch (e: Exception) {
            Log.w(TAG, "Player name queued for sync", e)
            syncRepository.queuePlayerNameUpdate(userId, name)
        }
    }

    override suspend fun updateAvatarUri(userId: String, avatarUri: String?) = withContext(Dispatchers.IO) {
        val currentCard = playerCardDao.getPlayerCard(userId) ?: PlayerCard(id = userId, playerName = "SelfEvo Player")
        playerCardDao.insertPlayerCard(currentCard.copy(avatarUri = avatarUri))
    }

    override suspend fun updateEquippedTheme(userId: String, theme: String) = withContext(Dispatchers.IO) {
        val currentCard = playerCardDao.getPlayerCard(userId) ?: PlayerCard(id = userId, playerName = "SelfEvo Player")
        playerCardDao.insertPlayerCard(currentCard.copy(equippedTheme = theme))
    }

    override suspend fun updateCardSlots(
        userId: String,
        slot1: String,
        slot2: String,
        slot3: String,
        slot4: String,
        slot5: String,
        slot6: String
    ) = withContext(Dispatchers.IO) {
        val currentCard = playerCardDao.getPlayerCard(userId) ?: PlayerCard(id = userId, playerName = "SelfEvo Player")
        playerCardDao.insertPlayerCard(
            currentCard.copy(
                slot1 = slot1,
                slot2 = slot2,
                slot3 = slot3,
                slot4 = slot4,
                slot5 = slot5,
                slot6 = slot6
            )
        )
    }

    override suspend fun incrementStat(userId: String, attributeType: String, amount: Int): PlayerCard? = withContext(Dispatchers.IO) {
        val activeCard = playerCardDao.getPlayerCard(userId) ?: PlayerCard(id = userId, playerName = "SelfEvo Player")
        val isDefaultStat = attributeType.uppercase() in listOf("PAC", "PACE", "SHO", "SHOOTING", "PAS", "PASSING", "SKL", "SKILL", "DRIBBLING", "DEF", "DEFENDING", "PHY", "PHYSICAL")
        if (isDefaultStat) {
            val updatedCard = activeCard.incrementStat(attributeType, amount)
            playerCardDao.insertPlayerCard(updatedCard)
            Log.i(TAG, "Default Stat Growth: $attributeType +$amount. New OVR: ${updatedCard.ovr}")
            updatedCard
        } else {
            customAttributeDao.incrementAttributeScore(attributeType.uppercase(), amount, userId)
            Log.i(TAG, "Custom Stat Growth: $attributeType +$amount.")
            activeCard
        }
    }
}
