import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.mymoodtracker.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import com.example.mymoodtracker.AppDatabase
import com.example.mymoodtracker.getDayInt
import java.util.Date

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val db = AppDatabase.getInstance(context) // your Room DB

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val today = getDayInt(Date()) // yyyyMMdd as Int
        val moods = db.dailyMoodDao().getAll()
        val todayMood = moods.find { it.date == today }

        if (todayMood == null) {
            sendNotification(
                "Mood Tracker",
                "You haven't recorded your mood today!"
            )
        } else {
            sendNotification(
                "Testing",
                "This is a test notification"
            )
        }
        Result.success()
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "mood_reminder_channel"

        val channel = NotificationChannel(channelId, "Mood Reminders", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // replace with your app icon
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1, notification)
    }
}

@Composable
fun SetupDailyMoodReminder() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val workManager = WorkManager.getInstance(context)
        val dailyWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(10, TimeUnit.SECONDS)
            .addTag("daily_mood_reminder")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "daily_mood_reminder",
            ExistingPeriodicWorkPolicy.REPLACE,
            dailyWorkRequest
        )
    }
}
