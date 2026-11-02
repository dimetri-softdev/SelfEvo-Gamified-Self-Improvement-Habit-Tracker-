package com.example.selfevo.data.remote

import com.example.selfevo.BuildConfig
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit client for real backend communication with live cloud production REST API.
 */
object RetrofitClient {
    private const val BASE_URL = BuildConfig.SELF_EVO_API_BASE_URL

    @Volatile
    private var cachedToken: String? = null

    init {
        try {
            FirebaseAuth.getInstance().addIdTokenListener(FirebaseAuth.IdTokenListener { auth ->
                val user = auth.currentUser
                if (user != null) {
                    user.getIdToken(false).addOnSuccessListener { result: GetTokenResult ->
                        cachedToken = result.token
                    }
                } else {
                    cachedToken = null
                }
            })
        } catch (_: Exception) {
            // Firebase Auth initialization safeguard
        }
    }

    private fun getAuthToken(): String? {
        val user = FirebaseAuth.getInstance().currentUser ?: return null
        val token = cachedToken
        if (!token.isNullOrBlank()) {
            return token
        }
        return try {
            val task = user.getIdToken(false)
            val result = Tasks.await(task, 5, TimeUnit.SECONDS)
            result?.token?.also { cachedToken = it }
        } catch (_: Exception) {
            null
        }
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            val token = getAuthToken()
            if (!token.isNullOrBlank()) {
                requestBuilder.header("Authorization", "Bearer $token")
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
