package com.example.selfevo.data.remote

import com.example.selfevo.data.remote.dto.AuthResponse
import com.example.selfevo.data.remote.dto.HabitLogRequest
import com.example.selfevo.data.remote.dto.HabitLogResponse
import com.example.selfevo.data.remote.dto.LoginRequest
import com.example.selfevo.data.remote.dto.NetworkHabitDto
import com.example.selfevo.data.remote.dto.NetworkPlayerCardDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SelfEvoApiService {

    @POST("/api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @GET("/api/habits")
    suspend fun getHabits(): Response<List<NetworkHabitDto>>

    @POST("/api/habits/log")
    suspend fun logHabit(
        @Body request: HabitLogRequest
    ): Response<HabitLogResponse>

    @GET("/api/sync/card")
    suspend fun getPlayerCard(): Response<NetworkPlayerCardDto>

    @POST("/api/sync/card")
    suspend fun syncPlayerCard(
        @Body card: NetworkPlayerCardDto
    ): Response<NetworkPlayerCardDto>
}
