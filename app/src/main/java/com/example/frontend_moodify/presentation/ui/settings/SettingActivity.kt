package com.example.frontend_moodify.presentation.ui.settings

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.frontend_moodify.R
import com.example.frontend_moodify.data.remote.network.Injection
import com.example.frontend_moodify.databinding.ActivitySettingsBinding
import com.example.frontend_moodify.presentation.ui.auth.LoginActivity
import com.example.frontend_moodify.presentation.ui.profile.ProfileActivity
import com.example.frontend_moodify.utils.FirebaseService
import com.example.frontend_moodify.utils.SessionManager
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var darkModeSwitch: SwitchCompat
    private lateinit var dailyReminderSwitch: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        darkModeSwitch = findViewById(R.id.dark_mode_switch)
        dailyReminderSwitch = findViewById(R.id.notification_switch)

        val sharedPreferences = getSharedPreferences("user_settings", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        val isDailyReminderEnabled = sharedPreferences.getBoolean("daily_reminder", false)

        // Set initial states
        darkModeSwitch.isChecked = isDarkMode
        dailyReminderSwitch.isChecked = isDailyReminderEnabled
        updateDarkMode(isDarkMode)

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("dark_mode", isChecked)
            editor.apply()
            updateDarkMode(isChecked)
        }

        dailyReminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("daily_reminder", isChecked)
            editor.apply()

            if (isChecked) {
                activateDailyReminder()
            } else {
                deactivateDailyReminder()
            }
        }

        // Request notification permissions for API 33+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
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

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.profileLink.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun activateDailyReminder() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM Token", "Token: $token")
                sendTokenToServer(token)
            } else {
                Log.e("FCM Token", "Failed to get token")
            }
        }
    }

    private fun deactivateDailyReminder() {
        // Logic for disabling the reminder
        Toast.makeText(this, "Daily reminder deactivated", Toast.LENGTH_SHORT).show()
    }

    private fun sendTokenToServer(token: String) {
        val sessionManager = SessionManager(this)
        val apiService = Injection.provideNotificationApiService(sessionManager)
        val body = mapOf("fcmToken" to token)

        apiService.sendNotification(body).enqueue(object : Callback<Unit> {
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

    private fun updateDarkMode(isEnabled: Boolean) {
        if (isEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
