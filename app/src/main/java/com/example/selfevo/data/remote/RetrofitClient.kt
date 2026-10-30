package com.example.selfevo.data.remote

import com.example.selfevo.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

/**
 * Singleton Retrofit client for real backend communication with live cloud production REST API.
 */
object RetrofitClient {
    private const val BASE_URL = BuildConfig.SELF_EVO_API_BASE_URL

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val token = runBlocking { user.getIdToken(false).await().token }
                if (!token.isNullOrBlank()) {
                    requestBuilder.header("Authorization", "Bearer $token")
                }
            }
            chain.proceed(requestBuilder.build())
        }
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
