package com.example.mymoodtracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val CHANNEL_ID = "default_channel"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")

        // Data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            // Check if this is a weather check trigger
            if (remoteMessage.data["type"] == "weather_check") {
                checkWeatherAndNotify()
            }
        }

        // Notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.title ?: "New Notification", it.body ?: "You have a new message")
        }
    }

    private fun checkWeatherAndNotify() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Replace with user's actual coordinates - you could get this from GPS or SharedPreferences
                val latitude = 43.6109 // Montpellier
                val longitude = 3.8763

                val weatherCode = getCurrentWeather(latitude, longitude)

                if (weatherCode != null && isSunny(weatherCode)) {
                    withContext(Dispatchers.Main) {
                        sendNotification(
                            "It's a Beautiful Day! ☀️",
                            "The sun is shining! It's going to be a good day!"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking weather: ${e.message}")
            }
        }
    }

    private suspend fun getCurrentWeather(latitude: Double, longitude: Double): Int? = withContext(Dispatchers.IO) {
        try {
            val url = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=weather_code"
            val response = URL(url).readText()
            val json = JSONObject(response)
            val current = json.getJSONObject("current")
            current.getInt("weather_code")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching weather: ${e.message}")
            null
        }
    }

    private fun isSunny(weatherCode: Int): Boolean {
        // Open-Meteo weather codes:
        // 0 = Clear sky
        // 1 = Mainly clear
        // 2 = Partly cloudy
        return weatherCode in 0..1
    }

    private fun sendNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Weather Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    /** FOR TESTING */
    fun testNotification() {
        sendNotification(
            "It's a Beautiful Day! ☀️",
            "The sun is shining! It's going to be a good day!"
        )
    }
}