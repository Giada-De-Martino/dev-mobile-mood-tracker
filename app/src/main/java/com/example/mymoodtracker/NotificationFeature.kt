package com.example.mymoodtracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import java.util.concurrent.TimeUnit

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val title = inputData.getString("title") ?: return Result.success()
        val message = inputData.getString("message") ?: return Result.success()

        sendNotification(title, message)
        return Result.success()
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "mood_reminder_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Mood & Diary Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            (System.currentTimeMillis() % 100000).toInt(),
            notification
        )
    }
}

fun scheduleDailyMoodReminder(context: Context) {
    val data = Data.Builder()
        .putString("title", "Mood Tracker")
        .putString("message", "Don't forget to record your mood today 🌤️")
        .build()

    val request = PeriodicWorkRequestBuilder<NotificationWorker>(
    15, TimeUnit.MINUTES
    )
        .setInputData(data)
        .addTag("daily_mood_reminder")
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "daily_mood_reminder",
        ExistingPeriodicWorkPolicy.UPDATE,
        request
    )
}

fun scheduleDailyDiaryReminder(context: Context) {
    val data = Data.Builder()
        .putString("title", "Diary Reminder")
        .putString("message", "Take a moment to write your diary entry ✍️")
        .build()

    val request = PeriodicWorkRequestBuilder<NotificationWorker>(
        15, TimeUnit.MINUTES
    )
        .setInputData(data)
        .addTag("daily_diary_reminder")
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "daily_diary_reminder",
        ExistingPeriodicWorkPolicy.UPDATE,
        request
    )
}

fun sendTestNotificationNow(context: Context) {
    val data = Data.Builder()
        .putString("title", "Test Notification")
        .putString("message", "This notification was sent instantly ✅")
        .build()

    val request = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInputData(data)
        .build()

    WorkManager.getInstance(context).enqueue(request)
}