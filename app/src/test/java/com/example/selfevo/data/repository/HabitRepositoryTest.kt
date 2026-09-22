package com.example.selfevo.data.repository

import com.example.selfevo.data.db.dao.HabitDao
import com.example.selfevo.data.db.dao.PlayerCardDao
import com.example.selfevo.data.db.dao.SyncQueueDao
import com.example.selfevo.data.db.entity.HabitEntity
import com.example.selfevo.data.db.entity.SyncQueueEntity
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
        var habits: List<HabitEntity> = emptyList()
        override suspend fun insertHabits(habits: List<HabitEntity>) { this.habits = habits }
        override suspend fun insertHabit(habit: HabitEntity) { this.habit = habit }
        override suspend fun updateHabit(habit: HabitEntity) { this.habit = habit }
        override suspend fun getHabitById(habitId: String): HabitEntity? = habit
        override suspend fun getAllHabits(userId: String): List<HabitEntity> = habits
        override suspend fun deleteAllHabits() {}
        override fun getAllHabitsFlow(userId: String): Flow<List<HabitEntity>> = throw NotImplementedError()
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
        habitDao.habit = HabitEntity(habitId, "user1", "Sprint", "Sprint training", "PACE", false, null)
        playerCardDao.card = PlayerCard(id = "user1", playerName = "Test Player", pace = 50)

        // Act
        val resultCard = repository.completeHabit(habitId, "user1")

        // Assert
        assertEquals(52, resultCard?.pace)
        assertEquals(52, playerCardDao.card?.pace)
    }

    @Test
    fun `completeHabit increments shooting stat when attribute type is SHOOTING`() = runBlocking {
        // Arrange
        val habitId = "h4"
        habitDao.habit = HabitEntity(habitId, "user1", "Shooting", "Shooting practice", "SHOOTING", false, null)
        playerCardDao.card = PlayerCard(id = "user1", playerName = "Test Player", shooting = 60)

        // Act
        val resultCard = repository.completeHabit(habitId, "user1")

        // Assert
        assertEquals(62, resultCard?.shooting)
        assertEquals(62, playerCardDao.card?.shooting)
    }

    @Test
    fun `completeHabit triggers OVR increase when stats improve`() = runBlocking {
        // Arrange
        val habitId = "h3"
        habitDao.habit = HabitEntity(habitId, "user1", "Sprint", "Sprint training", "PACE", false, null)
        playerCardDao.card = PlayerCard(
            id = "user1", playerName = "Test Player",
            pace = 89, shooting = 90, passing = 90, skill = 90, defending = 90, physical = 90
        )

        // Act
        val resultCard = repository.completeHabit(habitId, "user1")

        // Assert
        assertEquals(90, resultCard?.ovr)
        assertEquals("Walkout", resultCard?.tier)
    }
}
