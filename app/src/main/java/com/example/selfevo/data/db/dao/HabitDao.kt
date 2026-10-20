package com.example.selfevo.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.selfevo.data.db.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE userId = :userId")
    fun getAllHabitsFlow(userId: String): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE userId = :userId")
    suspend fun getAllHabits(userId: String): List<HabitEntity>

    @Query("SELECT * FROM habits WHERE id = :habitId AND userId = :userId")
    suspend fun getHabitById(habitId: String, userId: String): HabitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(habits: List<HabitEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("UPDATE habits SET isCompletedToday = 1, lastCompletedDate = :today WHERE id = :habitId AND userId = :userId AND isCompletedToday = 0")
    suspend fun markCompletedToday(habitId: String, userId: String, today: String): Int

    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits()
}
