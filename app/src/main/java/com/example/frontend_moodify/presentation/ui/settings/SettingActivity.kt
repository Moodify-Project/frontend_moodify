package com.example.frontend_moodify.presentation.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.frontend_moodify.R
import androidx.appcompat.app.AppCompatDelegate
import com.example.frontend_moodify.data.remote.network.ApiService
import com.example.frontend_moodify.data.remote.network.ScheduleRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SettingActivity : AppCompatActivity() {

    private lateinit var darkModeSwitch: SwitchCompat
    private lateinit var dailyReminderSwitch: SwitchCompat
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Setup UI
        darkModeSwitch = findViewById(R.id.dark_mode_switch)
        dailyReminderSwitch = findViewById(R.id.notification_switch)

        // Setup SharedPreferences
        sharedPreferences = getSharedPreferences("user_settings", MODE_PRIVATE)

        // Initialize Retrofit
        retrofit = Retrofit.Builder()
            .baseUrl("http://35.219.12.145/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        // Dark Mode: Load from SharedPreferences
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        darkModeSwitch.isChecked = isDarkMode
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // Listener untuk Dark Mode
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("dark_mode", isChecked)
            editor.apply()

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Daily Reminder: Listener untuk mengatur status
        dailyReminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = ScheduleRequest(isActive = isChecked)
                    val response = apiService.setNotificationSchedule(request)

                    // Handle response
                    if (response.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "Setting updated!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "Failed to update setting", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    // Handle error, such as network issues
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
