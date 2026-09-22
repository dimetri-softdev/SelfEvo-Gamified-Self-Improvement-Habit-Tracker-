package com.example.selfevo.data.repository

import com.example.selfevo.data.local.dao.HabitDao
import com.example.selfevo.data.local.dao.PlayerCardDao
import com.example.selfevo.data.local.dao.SyncQueueDao
import com.example.selfevo.data.local.entity.HabitEntity
import com.example.selfevo.data.local.entity.SyncQueueEntity
import com.example.selfevo.data.model.PlayerCard
import com.example.selfevo.data.remote.SelfEvoApiService
import com.example.selfevo.data.remote.dto.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class HabitRepositoryTest {

    private lateinit var repository: HabitRepository

    private val habitDao = object : HabitDao {
        var habit: HabitEntity? = null
        val habits = mutableListOf<HabitEntity>()
        override suspend fun insertHabits(habits: List<HabitEntity>) { this.habits.addAll(habits) }
        override suspend fun insertHabit(habit: HabitEntity) { this.habits.add(habit) }
        override suspend fun updateHabit(habit: HabitEntity) {
            this.habit = habit
            val index = habits.indexOfFirst { it.id == habit.id }
            if (index != -1) habits[index] = habit
        }
        override suspend fun getHabitById(habitId: String): HabitEntity? = habit
        override suspend fun getAllHabits(): List<HabitEntity> = habits
        override suspend fun deleteAllHabits() { habits.clear() }
        override fun getAllHabitsFlow(): Flow<List<HabitEntity>> = throw NotImplementedError()
    }

    private val playerCardDao = object : PlayerCardDao {
        var card: PlayerCard? = null
        override suspend fun insertPlayerCard(playerCard: PlayerCard) { this.card = playerCard }
        override suspend fun getPlayerCard(userId: String): PlayerCard? = card
        override fun getPlayerCardFlow(userId: String): Flow<PlayerCard?> = throw NotImplementedError()
    }

    private val syncQueueDao = object : SyncQueueDao {
        val queue = mutableListOf<SyncQueueEntity>()
        override suspend fun addItemToQueue(item: SyncQueueEntity) { queue.add(item) }
        override suspend fun getAllPendingItems(): List<SyncQueueEntity> = queue
        override suspend fun deleteItemFromQueue(item: SyncQueueEntity) { queue.remove(item) }
        override suspend fun deleteById(id: Int) { queue.removeIf { it.id == id } }
    }

    private val apiService = object : SelfEvoApiService {
        var shouldFail = false
        var lastSyncedCard: NetworkPlayerCardDto? = null

        override suspend fun login(request: LoginRequest): Response<AuthResponse> = throw NotImplementedError()
        override suspend fun getHabits(): Response<List<NetworkHabitDto>> = throw NotImplementedError()
        override suspend fun logHabit(request: HabitLogRequest): Response<HabitLogResponse> {
            return if (shouldFail) {
                Response.error(500, "error".toResponseBody(null))
            } else {
                Response.success(HabitLogResponse(true, "success", null))
            }
        }
        override suspend fun getPlayerCard(): Response<NetworkPlayerCardDto> = throw NotImplementedError()
        override suspend fun syncPlayerCard(card: NetworkPlayerCardDto): Response<NetworkPlayerCardDto> {
            lastSyncedCard = card
            return Response.success(card)
        }
    }

    @Before
    fun setup() {
        repository = HabitRepository(habitDao, playerCardDao, syncQueueDao, apiService)
        apiService.shouldFail = false
        apiService.lastSyncedCard = null
        syncQueueDao.queue.clear()
        habitDao.habits.clear()
        habitDao.habit = null
        playerCardDao.card = null
    }

    @Test
    fun `completeHabit increments pace stat when attribute type is PACE`() = runBlocking {
        // Arrange
        val habitId = "h3"
        habitDao.habit = HabitEntity(habitId, "Sprint", "Sprint training", "PACE", false, null)
        playerCardDao.card = PlayerCard(id = "user1", playerName = "Test Player", pace = 50)

        // Act
        val resultCard = repository.completeHabit(habitId)

        // Assert
        assertEquals(52, resultCard?.pace)
        assertEquals(52, playerCardDao.card?.pace)
    }

    @Test
    fun `completeHabit adds to sync queue when API call fails`() = runBlocking {
        // Arrange
        val habitId = "h3"
        habitDao.habit = HabitEntity(habitId, "Sprint", "Sprint training", "PACE", false, null)
        playerCardDao.card = PlayerCard(id = "user1", playerName = "Test Player", pace = 50)
        apiService.shouldFail = true

        // Act
        repository.completeHabit(habitId)

        // Assert
        assertEquals(1, syncQueueDao.queue.size)
        assertEquals(habitId, syncQueueDao.queue[0].habitId)
    }

    @Test
    fun `refreshPlayerCard resolves stats correctly taking max of local and remote`() = runBlocking {
        // Arrange
        playerCardDao.card = PlayerCard(id = "user1", playerName = "Test", pace = 60, shooting = 40)
        val remoteDto = NetworkPlayerCardDto(id = "user1", playerName = "Test", pace = 50, shooting = 70, passing = 50, dribbling = 50, defending = 50, physical = 50)

        val apiServiceWithCard = object : SelfEvoApiService by apiService {
            override suspend fun getPlayerCard(): Response<NetworkPlayerCardDto> = Response.success(remoteDto)
        }
        val repo = HabitRepository(habitDao, playerCardDao, syncQueueDao, apiServiceWithCard)

        // Act
        repo.refreshPlayerCard("user1")

        // Assert
        val finalCard = playerCardDao.card
        assertEquals(60, finalCard?.pace) // Local was higher
        assertEquals(70, finalCard?.shooting) // Remote was higher
    }

    @Test
    fun `refreshPlayerCard pushes local stats to remote if local is higher`() = runBlocking {
        // Arrange
        playerCardDao.card = PlayerCard(id = "user1", playerName = "Test", pace = 80)
        val remoteDto = NetworkPlayerCardDto(id = "user1", playerName = "Test", pace = 70, shooting = 50, passing = 50, dribbling = 50, defending = 50, physical = 50)

        val apiServiceWithCard = object : SelfEvoApiService by apiService {
            override suspend fun getPlayerCard(): Response<NetworkPlayerCardDto> = Response.success(remoteDto)
            override suspend fun syncPlayerCard(card: NetworkPlayerCardDto): Response<NetworkPlayerCardDto> {
                apiService.lastSyncedCard = card
                return Response.success(card)
            }
        }
        val repo = HabitRepository(habitDao, playerCardDao, syncQueueDao, apiServiceWithCard)

        // Act
        repo.refreshPlayerCard("user1")

        // Assert
        assertEquals(80, apiService.lastSyncedCard?.pace)
    }

    @Test
    fun `completeHabit triggers OVR increase when stats improve`() = runBlocking {
        // Arrange
        val habitId = "h3"
        habitDao.habit = HabitEntity(habitId, "Sprint", "Sprint training", "PACE", false, null)
        playerCardDao.card = PlayerCard(
            id = "user1", playerName = "Test Player",
            pace = 89, shooting = 90, passing = 90, skill = 90, defending = 90, physical = 90,
        )

        // Act
        val resultCard = repository.completeHabit(habitId)

        // Assert
        assertEquals(90, resultCard?.ovr)
        assertEquals("Walkout", resultCard?.tier)
    }

    @Test
    fun `completeHabit returns current card without changes if already completed today`() = runBlocking {
        // Arrange
        val habitId = "h3"
        habitDao.habit = HabitEntity(habitId, "Sprint", "Sprint training", "PACE", true, "2023-01-01")
        val initialCard = PlayerCard(id = "user1", playerName = "Test Player", pace = 50)
        playerCardDao.card = initialCard

        // Act
        val resultCard = repository.completeHabit(habitId)

        // Assert
        assertEquals(50, resultCard?.pace)
        assertEquals(initialCard, resultCard)
    }
}
