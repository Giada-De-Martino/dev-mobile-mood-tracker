package com.example.mymoodtracker

import java.util.Date

/**
 * SampleData for Jetpack Compose Tutorial
 */
object SampleData {
    // Sample conversation data

    val today = getDayInt(Date())
    val day1 = DailyMood(today - 5, 2, content = "Test content day 1")
    val day2 = DailyMood(today - 4, 4, content = "Going to the gym, Nice!")
    val day3 = DailyMood(today - 3, 0)
    val day4 = DailyMood(today - 2, 3, content = "Big Feelings")
    val day5 = DailyMood(today - 1, 1, content = "Feeling down")

    val moodList: List<DailyMood> = mutableListOf(day1, day2, day3, day4, day5)
}