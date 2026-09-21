package com.example.selfevo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NetworkPlayerCardDto(
    @SerializedName("id") val id: String,
    @SerializedName("playerName") val playerName: String,
    @SerializedName("pac") val pace: Int,
    @SerializedName("sho") val shooting: Int,
    @SerializedName("pas") val passing: Int,
    @SerializedName("skl") val dribbling: Int,
    @SerializedName("def") val defending: Int,
    @SerializedName("phy") val physical: Int
)
