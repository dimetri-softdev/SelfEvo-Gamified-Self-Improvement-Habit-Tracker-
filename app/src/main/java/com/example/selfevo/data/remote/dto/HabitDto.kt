package com.example.selfevo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NetworkHabitDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("attributeType") val attributeType: String,
    @SerializedName("isCompletedToday") val isCompletedToday: Boolean,
    @SerializedName("frequency") val frequency: String,
    @SerializedName("reminderTime") val reminderTime: String
)
