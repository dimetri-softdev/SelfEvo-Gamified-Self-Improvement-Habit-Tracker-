package com.example.selfevo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NetworkPlayerCardDto(
    @SerializedName("id") val id: String,
    @SerializedName("playerName") val playerName: String,
    @SerializedName("pace") val pace: Int,
    @SerializedName("shooting") val shooting: Int,
    @SerializedName("passing") val passing: Int,
    @SerializedName("dribbling") val dribbling: Int,
    @SerializedName("defending") val defending: Int,
    @SerializedName("physical") val physical: Int
)
