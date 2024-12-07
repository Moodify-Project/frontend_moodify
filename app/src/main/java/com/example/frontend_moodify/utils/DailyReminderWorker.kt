package com.example.frontend_moodify.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.frontend_moodify.R
import com.example.frontend_moodify.data.remote.network.Injection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DailyReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val sessionManager = SessionManager(applicationContext)
        val apiService = Injection.provideNotificationApiService(sessionManager)
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getNotifications()
                if (!response.error) {
                    showNotification(response.notification)
                    Result.success()
                } else {
                    Result.retry()
                }
            } catch (e: Exception) {
                Result.retry()
            }
        }
    }

    private fun showNotification(message: String) {
        val channelId = "daily_reminder_channel"
        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Reminder",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Daily Reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        manager.notify(1, notification)
    }
    companion object {
        fun scheduleDailyReminder(context: Context) {
            val workRequest = androidx.work.PeriodicWorkRequestBuilder<DailyReminderWorker>(
                1, java.util.concurrent.TimeUnit.DAYS
            )
                .setInitialDelay(calculateInitialDelay(), java.util.concurrent.TimeUnit.MILLISECONDS)
                .addTag("daily_reminder")
                .build()

            androidx.work.WorkManager.getInstance(context).enqueue(workRequest)
        }

        fun cancelDailyReminder(context: Context) {
            androidx.work.WorkManager.getInstance(context).cancelAllWorkByTag("daily_reminder")
        }

        private fun calculateInitialDelay(): Long {
            val now = java.util.Calendar.getInstance()
            val target = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 16)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
            }

            if (now.after(target)) {
                target.add(java.util.Calendar.DAY_OF_MONTH, 1)
            }

            return target.timeInMillis - now.timeInMillis
        }
    }
}
