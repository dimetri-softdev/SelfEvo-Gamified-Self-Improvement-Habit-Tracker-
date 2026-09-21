package com.example.selfevo.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton Retrofit client for real backend communication with ASP.NET Core production URL.
 */
object RetrofitClient {
    // Replace this with the actual production ASP.NET Core backend URL
    private const val BASE_URL = "https://selfevo-api-production.azurewebsites.net/" 

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val apiService: SelfEvoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
            .create(SelfEvoApiService::class.java)
    }
}
