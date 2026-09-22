package com.example.selfevo.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.selfevo.data.db.dao.HabitDao
import com.example.selfevo.data.db.dao.PlayerCardDao
import com.example.selfevo.data.db.dao.SyncQueueDao
import com.example.selfevo.data.db.entity.HabitEntity
import com.example.selfevo.data.db.entity.SyncQueueEntity
import com.example.selfevo.data.model.PlayerCard

@Database(
    entities = [HabitEntity::class, SyncQueueEntity::class, PlayerCard::class],
    version = 4,
    exportSchema = false
)
abstract class SelfEvoDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun playerCardDao(): PlayerCardDao
    abstract fun syncQueueDao(): SyncQueueDao

    companion object {
        @Volatile
        private var INSTANCE: SelfEvoDatabase? = null

        fun getDatabase(context: Context): SelfEvoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SelfEvoDatabase::class.java,
                    "selfevo_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
