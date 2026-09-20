package com.example.selfevo.data.repository

import com.example.selfevo.data.local.dao.HabitDao
import com.example.selfevo.data.local.dao.PlayerCardDao
import com.example.selfevo.data.local.dao.SyncQueueDao
import com.example.selfevo.data.local.entity.HabitEntity
import com.example.selfevo.data.local.entity.SyncQueueEntity
import com.example.selfevo.data.model.PlayerCard
import com.example.selfevo.data.remote.SelfEvoApiService
import com.example.selfevo.data.remote.dto.AuthResponse
import com.example.selfevo.data.remote.dto.HabitLogRequest
import com.example.selfevo.data.remote.dto.HabitLogResponse
import com.example.selfevo.data.remote.dto.LoginRequest
import com.example.selfevo.data.remote.dto.NetworkHabitDto
import com.example.selfevo.data.remote.dto.NetworkPlayerCardDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class HabitRepositoryTest {

    private lateinit var repository: HabitRepository
    
    private val habitDao = object : HabitDao {
        var habit: HabitEntity? = null
        override suspend fun insertHabits(habits: List<HabitEntity>) {}
        override suspend fun insertHabit(habit: HabitEntity) {}
        override suspend fun updateHabit(habit: HabitEntity) { this.habit = habit }
        override suspend fun getHabitById(habitId: String): HabitEntity? = habit
        override suspend fun getAllHabits(): List<HabitEntity> = emptyList()
        override suspend fun deleteAllHabits() {}
        override fun getAllHabitsFlow(): Flow<List<HabitEntity>> = throw NotImplementedError()
    }

    private val playerCardDao = object : PlayerCardDao {
        var card: PlayerCard? = null
        override suspend fun insertPlayerCard(playerCard: PlayerCard) { this.card = playerCard }
        override suspend fun getPlayerCard(userId: String): PlayerCard? = card
        override fun getPlayerCardFlow(userId: String): Flow<PlayerCard?> = throw NotImplementedError()
    }

    private val syncQueueDao = object : SyncQueueDao {
        override suspend fun addItemToQueue(item: SyncQueueEntity) {}
        override suspend fun getAllPendingItems(): List<SyncQueueEntity> = emptyList()
        override suspend fun deleteItemFromQueue(item: SyncQueueEntity) {}
        override suspend fun deleteById(id: Int) {}
    }

    private val apiService = object : SelfEvoApiService {
        override suspend fun login(request: LoginRequest): Response<AuthResponse> = throw NotImplementedError()
        override suspend fun getHabits(): Response<List<NetworkHabitDto>> = throw NotImplementedError()
        override suspend fun logHabit(request: HabitLogRequest): Response<HabitLogResponse> = Response.success(HabitLogResponse(true, "success", null))
        override suspend fun getPlayerCard(): Response<NetworkPlayerCardDto> = throw NotImplementedError()
        override suspend fun syncPlayerCard(card: NetworkPlayerCardDto): Response<NetworkPlayerCardDto> = throw NotImplementedError()
    }

    @Before
    fun setup() {
        repository = HabitRepository(habitDao, playerCardDao, syncQueueDao, apiService)
    }

    @Test
    fun `completeHabit increments pace stat when attribute type is PACE`() = runBlocking {
        // Arrange
        val habitId = "h3"
        habitDao.habit = HabitEntity(habitId, "Sprint", "Sprint training", "PACE", false)
        playerCardDao.card = PlayerCard(id = "user1", playerName = "Test Player", pace = 50)
        
        // Act
        val resultCard = repository.completeHabit(habitId)

        // Assert
        assertEquals(51, resultCard?.pace)
        assertEquals(51, playerCardDao.card?.pace)
    }

    @Test
    fun `completeHabit increments shooting stat when attribute type is SHOOTING`() = runBlocking {
        // Arrange
        val habitId = "h4"
        habitDao.habit = HabitEntity(habitId, "Shooting", "Shooting practice", "SHOOTING", false)
        playerCardDao.card = PlayerCard(id = "user1", playerName = "Test Player", shooting = 60)
        
        // Act
        val resultCard = repository.completeHabit(habitId)

        // Assert
        assertEquals(61, resultCard?.shooting)
        assertEquals(61, playerCardDao.card?.shooting)
    }

    @Test
    fun `completeHabit triggers OVR increase when stats improve`() = runBlocking {
        // Arrange
        val habitId = "h3"
        habitDao.habit = HabitEntity(habitId, "Sprint", "Sprint training", "PACE", false)
        // All stats at 89, one more increase should bump OVR if it was rounding down or on the edge
        playerCardDao.card = PlayerCard(
            id = "user1", playerName = "Test Player",
            pace = 89, shooting = 90, passing = 90, skill = 90, defending = 90, physical = 90
        )
        // Initial OVR: (89+90+90+90+90+90)/6 = 539/6 = 89.83 -> 89
        
        // Act
        val resultCard = repository.completeHabit(habitId)

        // Assert
        // New OVR: (90+90+90+90+90+90)/6 = 90
        assertEquals(90, resultCard?.ovr)
        assertEquals("Walkout", resultCard?.tier)
    }
}
