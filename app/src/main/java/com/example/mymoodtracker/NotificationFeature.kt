package com.example.mymoodtracker

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.util.Date

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val db = AppDatabase.getInstance(context)

    override suspend fun doWork(): Result {
        val today = getDayInt(Date())
        val todayMood = db.dailyMoodDao().getAll().find { it.date == today }

        if (todayMood == null || todayMood.value == 0) {
            sendNotification("Mood Tracker", "You haven't recorded your mood today!")
        }

        if (todayMood == null || todayMood.content.isEmpty()) {
            sendNotification("Mood Tracker", "You haven't written a diary entry today!")
        }

        // TEST
        sendNotification("Testing", "This is a test notification")
        return Result.success()
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "mood_reminder_channel"
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
    }
}
