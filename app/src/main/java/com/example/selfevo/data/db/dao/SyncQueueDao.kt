package com.example.selfevo.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.selfevo.data.db.entity.SyncQueueEntity

@Dao
interface SyncQueueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItemToQueue(item: SyncQueueEntity)

    @Query("SELECT * FROM sync_queue")
    suspend fun getAllPendingItems(): List<SyncQueueEntity>

    @Delete
    suspend fun deleteItemFromQueue(item: SyncQueueEntity)

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun deleteById(id: Int)
}
