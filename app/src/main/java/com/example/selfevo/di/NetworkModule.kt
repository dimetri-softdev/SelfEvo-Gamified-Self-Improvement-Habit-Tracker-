package com.example.selfevo.di

import com.example.selfevo.data.remote.RetrofitClient
import com.example.selfevo.data.remote.SelfEvoApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSelfEvoApiService(): SelfEvoApiService {
        return RetrofitClient.apiService
    }
}
