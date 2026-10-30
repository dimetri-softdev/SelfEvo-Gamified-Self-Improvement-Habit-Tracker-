package com.example.selfevo.data.remote

import com.example.selfevo.data.remote.dto.CreateHabitRequest
import com.example.selfevo.data.remote.dto.HabitLogRequest
import com.example.selfevo.data.remote.dto.HabitLogResponse
import com.example.selfevo.data.remote.dto.NetworkHabitDto
import com.example.selfevo.data.remote.dto.NetworkPlayerCardDto
import com.example.selfevo.data.remote.dto.PlayerNameUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SelfEvoApiService {
    @GET("/api/habits")
    suspend fun getHabits(
        @Query("date") date: String
    ): Response<List<NetworkHabitDto>>

    @POST("/api/habits")
    suspend fun createHabit(
        @Body request: CreateHabitRequest
    ): Response<NetworkHabitDto>

    @POST("/api/habits/log")
    suspend fun logHabit(
        @Body request: HabitLogRequest
    ): Response<HabitLogResponse>

    @GET("/api/sync/card")
    suspend fun getPlayerCard(): Response<NetworkPlayerCardDto>

    @PATCH("/api/sync/card")
    suspend fun updatePlayerName(
        @Body request: PlayerNameUpdateRequest
    ): Response<NetworkPlayerCardDto>
}
