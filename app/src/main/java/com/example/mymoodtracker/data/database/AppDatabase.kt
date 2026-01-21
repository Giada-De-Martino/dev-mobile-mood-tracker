package com.example.mymoodtracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mymoodtracker.data.dao.DailyMoodDao
import com.example.mymoodtracker.data.model.DailyMood

@Database(entities = [DailyMood::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dailyMoodDao(): DailyMoodDao
}
