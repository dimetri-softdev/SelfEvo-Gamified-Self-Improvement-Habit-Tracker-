package com.example.selfevo.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.selfevo.data.db.entity.CustomAttributeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomAttributeDao {
    @Query("SELECT * FROM custom_attributes WHERE userId = :userId")
    fun getCustomAttributesFlow(userId: String = "default_user"): Flow<List<CustomAttributeEntity>>

    @Query("SELECT * FROM custom_attributes WHERE userId = :userId")
    suspend fun getCustomAttributes(userId: String = "default_user"): List<CustomAttributeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomAttribute(attribute: CustomAttributeEntity)

    @Query("UPDATE custom_attributes SET score = MIN(99, score + :amount) WHERE abbreviation = :abbr AND userId = :userId")
    suspend fun incrementAttributeScore(abbr: String, amount: Int, userId: String = "default_user")
}
