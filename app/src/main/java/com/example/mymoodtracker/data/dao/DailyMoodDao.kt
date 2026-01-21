package com.example.mymoodtracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mymoodtracker.data.model.DailyMood

@Dao
interface DailyMoodDao {
    @Query("SELECT * FROM DailyMood")
    suspend fun getAll(): List<DailyMood>

    @Query("SELECT * FROM DailyMood ORDER BY date ASC LIMIT 1")
    suspend fun getFirstDay(): DailyMood

    @Query("SELECT * FROM DailyMood WHERE date = :date")
    suspend fun getByDate(date: Long): DailyMood

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dailyMood: DailyMood)

    @Query("UPDATE DailyMood SET value = :value WHERE date = :date")
    suspend fun updateMood(date: Long, value: Int)

    @Query("UPDATE DailyMood SET content = :content WHERE date = :date")
    suspend fun updateContent(date: Long, content: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dailyMoods: List<DailyMood>)

    @Delete
    suspend fun delete(moodValue: DailyMood)
}
