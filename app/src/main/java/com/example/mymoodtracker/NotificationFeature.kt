package com.example.mymoodtracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.*
import java.util.concurrent.TimeUnit
import java.util.Calendar

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

        val channel = NotificationChannel(
            channelId,
            "Mood & Diary Reminders",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

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

fun scheduleDailyReminder(context: Context, title: String, message: String, hour: Int, minute: Int) {
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (before(now)) add(Calendar.DAY_OF_MONTH, 1)
    }

    val delay = target.timeInMillis - now.timeInMillis

    val data = Data.Builder()
        .putString("title", title)
        .putString("message", message)
        .build()

    val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(false)
        .setRequiresDeviceIdle(false)
        .build()

    val request = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(data)
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        title,
        ExistingPeriodicWorkPolicy.REPLACE,
        request
    )
}

fun scheduleReminderOneTime(context: Context, title: String, message: String, delay: Long) {
    val data = Data.Builder()
        .putString("title", title)
        .putString("message", message)
        .build()

    val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(false)
        .setRequiresDeviceIdle(false)
        .build()

    val request = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(delay, TimeUnit.MINUTES)
        .setInputData(data)
        .setConstraints(constraints)
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