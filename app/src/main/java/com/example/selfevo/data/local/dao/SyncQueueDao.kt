package com.example.selfevo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.selfevo.data.local.entity.SyncQueueEntity

@Dao
interface SyncQueueDao {
    @Query("SELECT * FROM sync_queue ORDER BY timestamp ASC")
    suspend fun getAllPendingItems(): List<SyncQueueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItemToQueue(item: SyncQueueEntity)

    @Delete
    suspend fun deleteItemFromQueue(item: SyncQueueEntity)

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun deleteById(id: Int)
}
