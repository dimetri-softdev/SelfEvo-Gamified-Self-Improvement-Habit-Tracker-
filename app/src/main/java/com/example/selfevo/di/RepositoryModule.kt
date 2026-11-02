package com.example.selfevo.di

import com.example.selfevo.data.auth.AuthRepositoryImpl
import com.example.selfevo.data.repository.HabitRepositoryImpl
import com.example.selfevo.data.repository.PlayerCardRepositoryImpl
import com.example.selfevo.data.repository.SyncRepositoryImpl
import com.example.selfevo.domain.repository.AuthRepository
import com.example.selfevo.domain.repository.HabitRepository
import com.example.selfevo.domain.repository.PlayerCardRepository
import com.example.selfevo.domain.repository.SyncRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHabitRepository(
        impl: HabitRepositoryImpl
    ): HabitRepository

    @Binds
    @Singleton
    abstract fun bindPlayerCardRepository(
        impl: PlayerCardRepositoryImpl
    ): PlayerCardRepository

    @Binds
    @Singleton
    abstract fun bindSyncRepository(
        impl: SyncRepositoryImpl
    ): SyncRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}
