package com.example.frontend_moodify.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.frontend_moodify.MainActivity
import com.example.frontend_moodify.R
import com.example.frontend_moodify.data.remote.network.Injection
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM Token", "New token received: $token")
//        sendTokenToServer(token)
        val sharedPreferences = getSharedPreferences("user_settings", MODE_PRIVATE)
        val isDailyReminderEnabled = sharedPreferences.getBoolean("daily_reminder", true)
        val action = if (isDailyReminderEnabled) 1 else 0
        sendTokenToServer(token, action)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM Message", "Message received: ${remoteMessage.data}")

//        remoteMessage.notification?.let {
//            showNotification(it.title ?: "Notification", it.body ?: "No message body")
//        }
        val title = remoteMessage.notification?.title ?: "Notification"
        val body = remoteMessage.notification?.body ?: "No message body"

        if (body != "User already created journal") {
            showNotification(title, body)
        } else {
            Log.d("FirebaseService", "Notifikasi tidak ditampilkan: $body")
        }
    }

    private fun sendTokenToServer(token: String, action: Int) {
        val sessionManager = SessionManager(applicationContext)
        val apiService = Injection.provideNotificationApiService(sessionManager)
        val body = mapOf("fcmToken" to token)

        apiService.sendNotification(action, body).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    Log.d("FCM Token", "Token successfully sent to server")
                } else {
                    Log.e("FCM Token", "Failed to send token: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.e("FCM Token", "Error sending token to server", t)
            }
        })
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "daily_reminder_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Reminder Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(0, notification)
    }
}
