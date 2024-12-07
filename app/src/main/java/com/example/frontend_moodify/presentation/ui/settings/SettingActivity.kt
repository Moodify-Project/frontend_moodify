package com.example.frontend_moodify.presentation.ui.settings

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.WorkManager
import com.example.frontend_moodify.R
import com.example.frontend_moodify.databinding.ActivitySettingsBinding
import com.example.frontend_moodify.presentation.ui.auth.RegisterActivity
import com.example.frontend_moodify.presentation.ui.profile.ProfileActivity
import com.example.frontend_moodify.utils.DailyReminderWorker

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var darkModeSwitch: SwitchCompat
    private lateinit var dailyReminderSwitch: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup UI
        darkModeSwitch = findViewById(R.id.dark_mode_switch)
        dailyReminderSwitch = findViewById(R.id.notification_switch)

        // Dark Mode
        val sharedPreferences = getSharedPreferences("user_settings", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        darkModeSwitch.isChecked = isDarkMode
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

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

        // Daily Reminder
        val isDailyReminderEnabled = sharedPreferences.getBoolean("daily_reminder", true)
        dailyReminderSwitch.isChecked = isDailyReminderEnabled

        dailyReminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Check Notification Permission for Android 13+
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        enableDailyReminder(sharedPreferences)
                    } else {
                        requestNotificationPermission()
                    }
                } else {
                    enableDailyReminder(sharedPreferences)
                }
            } else {
                disableDailyReminder(sharedPreferences)
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.profileLink.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun enableDailyReminder(sharedPreferences: SharedPreferences) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("daily_reminder", true)
        editor.apply()
        DailyReminderWorker.scheduleDailyReminder(this)
        Toast.makeText(this, "Daily Reminder enabled!", Toast.LENGTH_SHORT).show()
    }

    private fun disableDailyReminder(sharedPreferences: SharedPreferences) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("daily_reminder", false)
        editor.apply()
        DailyReminderWorker.cancelDailyReminder(this)
        Toast.makeText(this, "Daily Reminder disabled!", Toast.LENGTH_SHORT).show()
    }

    private fun requestNotificationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            1001
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableDailyReminder(getSharedPreferences("user_settings", MODE_PRIVATE))
            } else {
                Toast.makeText(this, "Notification permission is required!", Toast.LENGTH_SHORT).show()
                dailyReminderSwitch.isChecked = false
            }
        }
    }
}
