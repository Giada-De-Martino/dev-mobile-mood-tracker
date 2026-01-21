package com.example.mymoodtracker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.example.mymoodtracker.data.database.AppDatabase
import com.example.mymoodtracker.ui.theme.MyMoodTrackerTheme
import com.example.mymoodtracker.utils.NavigationBarFeature
import com.example.mymoodtracker.utils.scheduleDailyReminder
import com.example.mymoodtracker.utils.sendTestNotificationNow

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        testLocalNotification()

        val database = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "daily_mood_database",
            ).fallbackToDestructiveMigration(false).build()

        setContent {
            MyMoodTrackerTheme {
                notification()
                NavigationBarFeature(database)
            }
        }
    }

    /** FIREBASE NOTIFICATIONS */
    private fun testLocalNotification() {
        val channelId = "default_channel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Weather Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("It's a Beautiful Day! ☀️")
            .setContentText("The sun is shining! It's going to be a good day!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1, notification)
    }

    /** WORKER NOTIFICATIONS */
    @Composable
    fun notification() {
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            scheduleDailyReminder(context, "Mood Tracker", "Don't forget to record your mood today 🌤️", 9, 0)
            scheduleDailyReminder(context, "Diary Reminder", "Take a moment to write your diary entry ✍️", 9, 0)
            sendTestNotificationNow(context)
        }
    }
    
}
