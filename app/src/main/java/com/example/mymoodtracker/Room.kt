package com.example.mymoodtracker

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.*


// MOOD Database ---------------------------------------------------------------
@Entity
data class DailyMood(
    @PrimaryKey val date: Long,
    @ColumnInfo(name = "value") val value: Int = 0
)

@Dao
interface DailyMoodDao {
    @Query("SELECT * FROM DailyMood")
    suspend fun getAll(): List<DailyMood>

    @Query("SELECT * FROM DailyMood ORDER BY date ASC LIMIT 1")
    suspend fun getFirstDay(): DailyMood?

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

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mood_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}


//convert date into long
fun getDayInt(date: Date): Long {
    val calendar = Calendar.getInstance().apply {
        time = date
    }

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    return year * 10000L + month * 100 + day
}

fun getDayString(date: Long): String {
    val year = date / 10000
    val month = (date % 10000) / 100
    val day = date % 100

    return "%04d/%02d/%02d".format(year, month, day)
}

suspend fun setUpDailyMood(db: AppDatabase) {
    val dao = db.dailyMoodDao()
    val today = getDayInt(Date())
    val firstDay = dao.getFirstDay()?.date ?: today
    val existingDates = dao.getAll().map { it.date }.toSet()

    // Create calendar starting from firstDay
    val calendar = Calendar.getInstance().apply {
        val year = (firstDay / 10000).toInt()
        val month = ((firstDay % 10000) / 100 - 1).toInt()
        val day = (firstDay % 100).toInt()

        set(year, month, day)
    }

    val moodsToInsert = mutableListOf<DailyMood>()

    while (true) {
        val date = getDayInt(calendar.time)

        if (date > today) break

        if (date !in existingDates) {
            moodsToInsert.add(DailyMood(date)) // value defaults to 0
        }

        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }

    if (moodsToInsert.isNotEmpty()) {
        dao.insertAll(moodsToInsert)
    }
}