package com.example.frontend_moodify.presentation.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.frontend_moodify.R
import androidx.appcompat.app.AppCompatDelegate

class SettingActivity : AppCompatActivity() {

    private lateinit var darkModeSwitch: SwitchCompat
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Get reference to the dark mode switch
        darkModeSwitch = findViewById(R.id.dark_mode_switch)

        // Initialize SharedPreferences to store user preferences
        sharedPreferences = getSharedPreferences("user_settings", MODE_PRIVATE)

        // Set the current status of dark mode based on SharedPreferences
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        darkModeSwitch.isChecked = isDarkMode

        // Handle dark mode toggle
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            // Save the user's dark mode preference
            val editor = sharedPreferences.edit()
            editor.putBoolean("dark_mode", isChecked)
            editor.apply()
        }
    }
}
