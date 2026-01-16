import com.example.mymoodtracker.DailyMood

/**
 * SampleData for Jetpack Compose Tutorial
 */
object SampleData {
    // Sample conversation data
    val moodtrack: List<Int> = mutableListOf(5,5,5)

    val day1 = DailyMood(20260112, 5)
    val day2 = DailyMood(20260111, 3)
    val day3 = DailyMood(20260110, 4)
    val day4 = DailyMood(20260109, 2)
    val day5 = DailyMood(20260108, 5)
    val day6 = DailyMood(20260107, 5)
    val day7 = DailyMood(20260106, 3)
    val day8 = DailyMood(20260105, 4)


    val moodList: List<DailyMood> = mutableListOf(day8, day7, day6, day5, day4, day3, day2, day1)
}