package com.example.selfevo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProfileImageDto(
    @SerializedName("path") val path: String? = null,
    @SerializedName("url") val url: String? = null
)
