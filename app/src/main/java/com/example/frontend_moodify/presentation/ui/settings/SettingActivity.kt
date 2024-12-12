package com.example.frontend_moodify.presentation.ui.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.frontend_moodify.R
import com.example.frontend_moodify.data.remote.network.Injection
import com.example.frontend_moodify.databinding.ActivitySettingsBinding
import com.example.frontend_moodify.presentation.ui.profile.ProfileActivity
import com.example.frontend_moodify.utils.SessionManager
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            sessionManager = SessionManager(this)
            FirebaseApp.initializeApp(this)

            // Set tema awal berdasarkan preferensi pengguna
            val isDarkMode = sessionManager.getThemePreference()
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )

            binding = ActivitySettingsBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Inisialisasi switches
            val darkModeSwitch: SwitchCompat = binding.darkModeSwitch
            val dailyReminderSwitch: SwitchCompat = binding.notificationSwitch

            // Set state awal dari preferensi
            darkModeSwitch.isChecked = isDarkMode
            dailyReminderSwitch.isChecked = sessionManager.getDailyReminderPreference()

            // Handle dark mode toggle
            darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (sessionManager.getThemePreference() != isChecked) {
                    sessionManager.saveThemePreference(isChecked)
                    updateDarkMode(isChecked)
                }
            }

            // Handle daily reminder toggle
            dailyReminderSwitch.setOnCheckedChangeListener { _, isChecked ->
                sessionManager.saveDailyReminderPreference(isChecked)
                handleDailyReminder(isChecked)
            }

            // Request notifikasi untuk Android 13+
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

            // Tautan ke profil
            binding.profileLink.setOnClickListener {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        }catch (e: Exception) {
            Log.e("SettingActivity", "Error: ${e.message}", e)
        }
    }

    private fun handleDailyReminder(isEnabled: Boolean) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                val action = if (isEnabled) 1 else 0
                sendTokenToServer(token, action)
            } else {
                Log.e("FCM Token", "Failed to get token")
            }
        }
    }

    private fun sendTokenToServer(token: String, action: Int) {
        val apiService = Injection.provideNotificationApiService(sessionManager)
        val body = mapOf("fcmToken" to token)

        apiService.sendNotification(action, body).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(call: retrofit2.Call<Unit>, response: retrofit2.Response<Unit>) {
                if (response.isSuccessful) {
                    Log.d("FCM Token", "Token successfully sent to server")
                } else {
                    Log.e("FCM Token", "Failed to send token: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                Log.e("FCM Token", "Error sending token to server", t)
            }
        })
    }

    private fun updateDarkMode(isEnabled: Boolean) {
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        val newMode = if (isEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO

        if (currentMode != newMode) {
            AppCompatDelegate.setDefaultNightMode(newMode)
            // Hindari rekreasi otomatis dengan memberikan animasi transisi
            window.setWindowAnimations(android.R.style.Animation_Translucent)
        }
    }
}
