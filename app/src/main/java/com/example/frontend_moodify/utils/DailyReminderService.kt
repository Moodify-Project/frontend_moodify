package com.example.frontend_moodify.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.frontend_moodify.R
import com.example.frontend_moodify.data.remote.network.NotificationApiService
import com.example.frontend_moodify.data.remote.response.notification.NotificationResponse
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class DailyReminderService : android.app.Service() {

    companion object {
        private const val CHANNEL_ID = "daily_reminder_channel"
        private const val CHANNEL_NAME = "Daily Reminder Notifications"
        private const val NOTIFICATION_ID = 1
        private const val BASE_URL = "http://35.219.12.145:8000/api/v1/"

        fun startService(context: Context) {
            val intent = Intent(context, DailyReminderService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, DailyReminderService::class.java)
            context.stopService(intent)
        }
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        coroutineScope.launch {
            checkTimeAndFetchNotification()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private suspend fun checkTimeAndFetchNotification() {
        val currentTime = Calendar.getInstance(TimeZone.getTimeZone("Asia/Makassar"))
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)

        if (currentHour >= 16) {
            try {
                val notificationApiService = createNotificationApiService()
                val response = notificationApiService.getNotifications()

                if (!response.error) {
                    sendDailyReminderNotification(response.notification)
                }
            } catch (e: Exception) {
                Log.e("DailyReminderService", "Failed to fetch notification: ${e.message}")
            }
        }
    }

    private fun createNotificationApiService(): NotificationApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(NotificationApiService::class.java)
    }

    private fun sendDailyReminderNotification(message: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Daily Reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
