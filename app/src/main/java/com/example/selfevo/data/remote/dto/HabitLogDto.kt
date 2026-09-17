package com.example.selfevo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HabitLogRequest(
    @SerializedName("habitId") val habitId: String
)

data class HabitLogResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("updatedCard") val updatedCard: NetworkPlayerCardDto? = null
)
