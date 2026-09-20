package com.example.selfevo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.selfevo.data.local.SelfEvoDatabase
import com.example.selfevo.data.local.entity.HabitEntity
import com.example.selfevo.data.model.PlayerCard
import com.example.selfevo.data.remote.SelfEvoApiService
import com.example.selfevo.data.remote.dto.AuthResponse
import com.example.selfevo.data.remote.dto.HabitLogRequest
import com.example.selfevo.data.remote.dto.HabitLogResponse
import com.example.selfevo.data.remote.dto.LoginRequest
import com.example.selfevo.data.remote.dto.NetworkHabitDto
import com.example.selfevo.data.remote.dto.NetworkPlayerCardDto
import com.example.selfevo.data.repository.HabitRepository
import com.example.selfevo.data.sync.SyncWorker
import com.example.selfevo.ui.dashboard.DashboardScreen
import com.example.selfevo.ui.dashboard.DashboardViewModel
import com.example.selfevo.ui.navigation.SelfEvoApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize local database DAOs
        val database = SelfEvoDatabase.getDatabase(applicationContext)
        val habitDao = database.habitDao()
        val playerCardDao = database.playerCardDao()
        val syncQueueDao = database.syncQueueDao()

        // 2. SelfEvo API Placeholder Client Integration
        val apiServicePlaceholder = object : SelfEvoApiService {
            override suspend fun login(request: LoginRequest): Response<AuthResponse> {
                return Response.success(AuthResponse("dummy-jwt-token", request.email, "Player"))
            }

            override suspend fun getHabits(): Response<List<NetworkHabitDto>> {
                return Response.success(emptyList())
            }

            override suspend fun logHabit(request: HabitLogRequest): Response<HabitLogResponse> {
                return Response.success(HabitLogResponse(true, "Logged successfully", null))
            }

            override suspend fun getPlayerCard(): Response<NetworkPlayerCardDto> {
                return Response.success(NetworkPlayerCardDto("default_user", "Pro Tracker", 50, 50, 50, 50, 50, 50))
            }

            override suspend fun syncPlayerCard(card: NetworkPlayerCardDto): Response<NetworkPlayerCardDto> {
                return Response.success(card)
            }
        }

        // 3. Construct unified data repository
        val repository = HabitRepository(habitDao, playerCardDao, syncQueueDao, apiServicePlaceholder)

        // Pre-populate dummy starter habits locally if the database is empty for easy testing
        CoroutineScope(Dispatchers.IO).launch {
            if (playerCardDao.getPlayerCard() == null) {
                playerCardDao.insertPlayerCard(PlayerCard(playerName = "Dimetri Peters"))
            }
            if (habitDao.getAllHabits().isEmpty()) {
                habitDao.insertHabits(
                    listOf(
                        HabitEntity("h1", "Morning Gym Workout", "Boost physical strength core stats", "PHYSICAL", false, "Daily", "07:00"),
                        HabitEntity("h2", "LeetCode Algorithmic Problem", "Increase tactical passing / skill level", "SKILL", false, "Daily", "18:00"),
                        HabitEntity("h3", "Sprint Interval Training", "Max out player pace speed limits", "PACE", false, "Daily", "08:00"),
                        HabitEntity("h4", "Precision Aim Practice", "Refine sharp focus and shooting accuracy", "SHOOTING", false, "Daily", "10:00")
                    )
                )
            }
        }

        // 4. Construct ViewModel via inline provider factory
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(repository) as T
            }
        }
        val viewModel = ViewModelProvider(this, viewModelFactory)[DashboardViewModel::class.java]

        // 5. Build Content View via Jetpack Compose
        // 5. Setup Constraint-Aware Periodic WorkManager Synchronization
        val syncConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicSyncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(syncConstraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "SelfEvoBackgroundSyncWork",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicSyncRequest
        )

        // 6. Build Content View via Jetpack Compose
        setContent {
            SelfEvoApp(viewModel = viewModel)
        }
    }
}
