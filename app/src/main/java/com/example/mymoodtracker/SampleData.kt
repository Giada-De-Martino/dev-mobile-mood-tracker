import com.example.mymoodtracker.DailyMood

/**
 * SampleData for Jetpack Compose Tutorial
 */
object SampleData {
    // Sample conversation data
    val startday = DailyMood(20260107, 5)


    val moodList: List<DailyMood> = mutableListOf(startday)
}