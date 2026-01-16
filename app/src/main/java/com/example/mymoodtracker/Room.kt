package com.example.mymoodtracker

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import java.util.*


// MOOD Database ---------------------------------------------------------------
@Entity
data class DailyMood(
    @PrimaryKey val date: Long,
    @ColumnInfo(name = "value") val value: Int
)

@Dao
interface DailyMoodDao {
    @Query("SELECT * FROM DailyMood")
    suspend fun getAll(): List<DailyMood>

    @Query("SELECT * FROM DailyMood WHERE date = :moodDate")
    suspend fun loadAllByIds(moodDate: Long): List<DailyMood>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dailyMood: DailyMood)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dailyMoods: List<DailyMood>)

    @Delete
    suspend fun delete(moodValue: DailyMood)
}

// DIARY database --------------------------------------------------------------
@Entity
data class DiaryEntry(
    @PrimaryKey val date: Long,          // e.g., 20260113
    @ColumnInfo(name = "content") val content: String
)

@Dao
interface DiaryEntryDao {
    @Query("SELECT * FROM DiaryEntry WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: Long): DiaryEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: DiaryEntry)

    @Query("SELECT * FROM DiaryEntry ORDER BY date DESC")
    suspend fun getAll(): List<DiaryEntry>
}

// All Databases --------------------------------------------------------------
@Database(entities = [DailyMood::class, DiaryEntry::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dailyMoodDao(): DailyMoodDao
    abstract fun diaryEntryDao(): DiaryEntryDao
}


//convert date into long
fun getTodayInt(): Long {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1  // 0-based
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    return year * 10000L + month * 100 + day      // e.g., 20260113
}

fun getDayString(date: Long): String {
    val year = date / 10000
    val month = (date % 10000) / 100
    val day = date % 100

    return "%04d/%02d/%02d".format(year, month, day)
}

suspend fun AppDatabase.setUpDailyMood() {
    val dao = dailyMoodDao()
    val today = getTodayInt()
    val exists = dao.getAll().any { it.date == today }
    if (!exists) {
        dao.insert(DailyMood(today, 0))
    }
}