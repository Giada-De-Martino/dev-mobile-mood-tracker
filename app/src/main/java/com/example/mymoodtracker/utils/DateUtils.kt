package com.example.mymoodtracker.utils

import com.example.mymoodtracker.data.database.AppDatabase
import com.example.mymoodtracker.data.model.DailyMood
import java.util.*

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
    
    val allMoods = dao.getAll()
    if (allMoods.isEmpty()) {
        dao.insert(DailyMood(today))
        return
    }
    
    val firstDay = allMoods.minByOrNull { it.date }?.date ?: today
    val existingDates = allMoods.map { it.date }.toSet()

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
            moodsToInsert.add(DailyMood(date))
        }

        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }

    if (moodsToInsert.isNotEmpty()) {
        dao.insertAll(moodsToInsert)
    }
}
