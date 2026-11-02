package com.example.selfevo.di

import android.content.Context
import com.example.selfevo.data.db.SelfEvoDatabase
import com.example.selfevo.data.db.dao.CustomAttributeDao
import com.example.selfevo.data.db.dao.HabitDao
import com.example.selfevo.data.db.dao.PlayerCardDao
import com.example.selfevo.data.db.dao.SyncQueueDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SelfEvoDatabase {
        return SelfEvoDatabase.getDatabase(context)
    }

    @Provides
    fun provideHabitDao(database: SelfEvoDatabase): HabitDao {
        return database.habitDao()
    }

    @Provides
    fun providePlayerCardDao(database: SelfEvoDatabase): PlayerCardDao {
        return database.playerCardDao()
    }

    @Provides
    fun provideSyncQueueDao(database: SelfEvoDatabase): SyncQueueDao {
        return database.syncQueueDao()
    }

    @Provides
    fun provideCustomAttributeDao(database: SelfEvoDatabase): CustomAttributeDao {
        return database.customAttributeDao()
    }
}
