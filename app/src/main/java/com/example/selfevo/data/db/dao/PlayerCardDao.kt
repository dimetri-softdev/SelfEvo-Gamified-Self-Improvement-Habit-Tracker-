package com.example.selfevo.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.selfevo.data.model.PlayerCard
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerCardDao {
    @Query("SELECT * FROM player_card WHERE id = :userId LIMIT 1")
    fun getPlayerCardFlow(userId: String = "default_user"): Flow<PlayerCard?>

    @Query("SELECT * FROM player_card WHERE id = :userId LIMIT 1")
    suspend fun getPlayerCard(userId: String = "default_user"): PlayerCard?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerCard(playerCard: PlayerCard)
}
