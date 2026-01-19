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

fun scheduleReminder(context: Context, title: String, message: String, delay: Long) {
    val data = Data.Builder()
        .putString("title", title)
        .putString("message", message)
        .build()

    val request = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(delay, TimeUnit.MINUTES)
        .setInputData(data)
        .build()

    WorkManager.getInstance(context).enqueue(request)
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