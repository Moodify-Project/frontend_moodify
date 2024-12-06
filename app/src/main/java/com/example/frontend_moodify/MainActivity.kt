package com.example.frontend_moodify

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.frontend_moodify.databinding.ActivityMainBinding
import com.example.frontend_moodify.presentation.ui.auth.LoginActivity
import com.example.frontend_moodify.presentation.ui.bookmark.BookmarkActivity
import com.example.frontend_moodify.presentation.ui.profile.ProfileActivity
import com.example.frontend_moodify.presentation.ui.settings.SettingActivity
import com.example.frontend_moodify.utils.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("user_settings", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)

        // Set dark mode based on user preference
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // Inflate layout and setContentView
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar setup
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Adjust toolbar colors based on dark mode
        if (isDarkMode) {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        }

        // Set up NavController for navigation between fragments
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_relaxation,
                R.id.navigation_create,
                R.id.navigation_mood,
                R.id.navigation_journal
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Setup Bottom Navigation
        val bottomNavigationView: BottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED

        // Adjust background color of BottomNavigationView based on dark mode
        if (isDarkMode) {
            bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
        } else {
            bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }

        // Adjust color of the overflow menu icon based on dark mode
        val menuIcon = toolbar.overflowIcon
        if (isDarkMode) {
            menuIcon?.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
        } else {
            menuIcon?.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
        }
    }

    // Inflate the options menu in the toolbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Handle selected menu items
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_bookmarks -> {
                val intent = Intent(this, BookmarkActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Logout function that clears session and redirects to login
    private fun logout() {
        val sessionManager = SessionManager(this)
        sessionManager.clearSession()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // Handle navigation up actions
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
