package com.example.selfevo.data.repository

import com.example.selfevo.data.db.dao.CustomAttributeDao
import com.example.selfevo.data.db.dao.HabitDao
import com.example.selfevo.data.db.dao.PlayerCardDao
import com.example.selfevo.data.db.dao.SyncQueueDao
import com.example.selfevo.data.db.entity.CustomAttributeEntity
import com.example.selfevo.data.db.entity.HabitEntity
import com.example.selfevo.data.db.entity.SyncQueueEntity
import com.example.selfevo.data.model.PlayerCard
import com.example.selfevo.data.remote.SelfEvoApiService
import com.example.selfevo.data.remote.dto.CreateHabitRequest
import com.example.selfevo.data.remote.dto.HabitLogRequest
import com.example.selfevo.data.remote.dto.HabitLogResponse
import com.example.selfevo.data.remote.dto.NetworkHabitDto
import com.example.selfevo.data.remote.dto.NetworkPlayerCardDto
import com.example.selfevo.data.remote.dto.PlayerNameUpdateRequest
import com.example.selfevo.domain.usecase.CompleteHabitUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class HabitRepositoryTest {

    private lateinit var completeHabitUseCase: CompleteHabitUseCase
    private lateinit var habitRepository: HabitRepositoryImpl
    private lateinit var playerCardRepository: PlayerCardRepositoryImpl
    private lateinit var syncRepository: SyncRepositoryImpl

    private val habitDao = object : HabitDao {
        var habit: HabitEntity? = null
        var habits: List<HabitEntity> = emptyList()
        override suspend fun insertHabits(habits: List<HabitEntity>) { this.habits = habits }
        override suspend fun insertHabit(habit: HabitEntity) { this.habit = habit }
        override suspend fun updateHabit(habit: HabitEntity) { this.habit = habit }
        override suspend fun markCompletedToday(habitId: String, userId: String, today: String): Int {
            val currentHabit = getHabitById(habitId, userId) ?: return 0
            if (currentHabit.isCompletedToday) return 0
            habit = currentHabit.copy(isCompletedToday = true, lastCompletedDate = today)
            return 1
        }
        override suspend fun getHabitById(habitId: String, userId: String): HabitEntity? =
            habit?.takeIf { it.id == habitId && it.userId == userId }
        override suspend fun getAllHabits(userId: String): List<HabitEntity> = habits
        override suspend fun deleteHabitById(habitId: String, userId: String): Int {
            if (habit?.id == habitId && habit?.userId == userId) {
                habit = null
                return 1
            }
            return 0
        }
        override suspend fun deleteHabit(habitId: String): Int {
            if (habit?.id == habitId) {
                habit = null
                return 1
            }
            return 0
        }
        override suspend fun deleteAllHabits() {}
        override fun getAllHabitsFlow(userId: String): Flow<List<HabitEntity>> = throw NotImplementedError()
    }

    private val playerCardDao = object : PlayerCardDao {
        var card: PlayerCard? = null
        override suspend fun insertPlayerCard(playerCard: PlayerCard) { this.card = playerCard }
        override suspend fun getPlayerCard(userId: String): PlayerCard? = card
        override fun getPlayerCardFlow(userId: String): Flow<PlayerCard?> = flowOf(card)
    }

    private val customAttributeDao = object : CustomAttributeDao {
        var attributes = mutableListOf<CustomAttributeEntity>()
        override fun getCustomAttributesFlow(userId: String): Flow<List<CustomAttributeEntity>> = flowOf(attributes)
        override suspend fun getCustomAttributes(userId: String): List<CustomAttributeEntity> = attributes
        override suspend fun insertCustomAttribute(attribute: CustomAttributeEntity) { attributes.add(attribute) }
        override suspend fun incrementAttributeScore(abbr: String, amount: Int, userId: String) {
            val idx = attributes.indexOfFirst { it.abbreviation == abbr && it.userId == userId }
            if (idx >= 0) {
                val old = attributes[idx]
                attributes[idx] = old.copy(score = minOf(99, old.score + amount))
            }
        }
    }

    private val syncQueueDao = object : SyncQueueDao {
        override suspend fun addItemToQueue(item: SyncQueueEntity) {}
        override suspend fun getAllPendingItems(): List<SyncQueueEntity> = emptyList()
        override suspend fun deleteItemFromQueue(item: SyncQueueEntity) {}
        override suspend fun deleteById(id: Int) {}
    }

    private val apiService = object : SelfEvoApiService {
        override suspend fun getHabits(date: String): Response<List<NetworkHabitDto>> = throw NotImplementedError()
        override suspend fun createHabit(request: CreateHabitRequest): Response<NetworkHabitDto> = throw NotImplementedError()
        override suspend fun deleteHabit(id: String): Response<Unit> = Response.success(Unit)
        override suspend fun logHabit(request: HabitLogRequest): Response<HabitLogResponse> = Response.success(HabitLogResponse(true, "success", null))
        override suspend fun getPlayerCard(): Response<NetworkPlayerCardDto> = throw NotImplementedError()
        override suspend fun updatePlayerName(request: PlayerNameUpdateRequest): Response<NetworkPlayerCardDto> = throw NotImplementedError()
    }

    @Before
    fun setup() {
        syncRepository = SyncRepositoryImpl(syncQueueDao, habitDao, apiService)
        playerCardRepository = PlayerCardRepositoryImpl(playerCardDao, customAttributeDao, apiService, syncRepository)
        habitRepository = HabitRepositoryImpl(habitDao, customAttributeDao, syncQueueDao, apiService, syncRepository, playerCardRepository)
        completeHabitUseCase = CompleteHabitUseCase(habitRepository, playerCardRepository)
    }

    @Test
    fun `completeHabitUseCase increments pace stat when attribute type is PACE`() = runBlocking {
        val habitId = "h3"
        habitDao.habit = HabitEntity(id = habitId, userId = "user1", title = "Sprint", description = "Sprint training", attributeType = "PACE", isCompletedToday = false)
        playerCardDao.card = PlayerCard(id = "user1", playerName = "Test Player", pace = 50)

        val result = completeHabitUseCase(habitId, "user1")

        assertEquals(52, result.updatedCard?.pace)
        assertEquals(52, playerCardDao.card?.pace)
    }

    @Test
    fun `completeHabitUseCase increments shooting stat when attribute type is SHOOTING`() = runBlocking {
        val habitId = "h4"
        habitDao.habit = HabitEntity(id = habitId, userId = "user1", title = "Shooting", description = "Shooting practice", attributeType = "SHOOTING", isCompletedToday = false)
        playerCardDao.card = PlayerCard(id = "user1", playerName = "Test Player", shooting = 60)

        val result = completeHabitUseCase(habitId, "user1")

        assertEquals(62, result.updatedCard?.shooting)
        assertEquals(62, playerCardDao.card?.shooting)
    }

    @Test
    fun `completeHabitUseCase supports the short attribute codes used by the habit form`() = runBlocking {
        habitDao.habit = HabitEntity(id = "h5", userId = "user1", title = "Pace", description = "Run", attributeType = "PAC", isCompletedToday = false)
        playerCardDao.card = PlayerCard(id = "user1", playerName = "Test Player", pace = 50)

        val result = completeHabitUseCase("h5", "user1")

        assertEquals(52, result.updatedCard?.pace)
    }

    @Test
    fun `completeHabitUseCase cannot load another user's habit`() = runBlocking {
        habitDao.habit = HabitEntity(id = "h6", userId = "user1", title = "Pace", description = "Run", attributeType = "PACE", isCompletedToday = false)
        playerCardDao.card = PlayerCard(id = "user2", playerName = "Other User", pace = 50)

        val result = completeHabitUseCase("h6", "user2")

        assertEquals(50, result.updatedCard?.pace)
        assertEquals(false, result.isLevelUp)
        assertEquals(50, playerCardDao.card?.pace)
    }

    @Test
    fun `completeHabitUseCase triggers OVR increase when stats improve`() = runBlocking {
        val habitId = "h3"
        habitDao.habit = HabitEntity(id = habitId, userId = "user1", title = "Sprint", description = "Sprint training", attributeType = "PACE", isCompletedToday = false)
        playerCardDao.card = PlayerCard(
            id = "user1", playerName = "Test Player",
            pace = 89, shooting = 90, passing = 90, skill = 90, defending = 90, physical = 90
        )

        val result = completeHabitUseCase(habitId, "user1")

        assertEquals(90, result.updatedCard?.ovr)
        assertEquals("Walkout", result.updatedCard?.tier)
        assertEquals(true, result.isLevelUp)
    }

    @Test
    fun `deleteHabit removes habit from local db`() = runBlocking {
        val habitId = "h_delete"
        habitDao.habit = HabitEntity(id = habitId, userId = "user1", title = "To Delete", description = "Test", attributeType = "PACE")

        habitRepository.deleteHabit(habitId, "user1")

        assertEquals(null, habitDao.getHabitById(habitId, "user1"))
    }
}
