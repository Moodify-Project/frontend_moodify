package com.example.frontend_moodify

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.core.app.ActivityCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var isActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


//        val sharedPreferences = getSharedPreferences("user_settings", MODE_PRIVATE)
//        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
//
//        if (isDarkMode) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        }

        val sessionManager = SessionManager(this)
        val isDarkMode = sessionManager.getThemePreference()

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        if (sessionManager.getAccessToken() == null) {
            sessionManager.clearSession()
            navigateToLogin()
        }

        // Inflate layout dan setContentView
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        if (isDarkMode) {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        }

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

        val bottomNavigationView: BottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf("android.permission.POST_NOTIFICATIONS"),
                    1002
                )
            }
        }
    }

//    private fun initializeFirebase() {
//        // Pastikan Firebase diinisialisasi sebelum digunakan
//        if (FirebaseApp.getApps(this).isEmpty()) {
//            FirebaseApp.initializeApp(this)
//            Log.d("FirebaseInit", "Firebase initialized successfully")
//        } else {
//            Log.d("FirebaseInit", "Firebase already initialized")
//        }
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

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

    private fun logout() {
        val sessionManager = SessionManager(this)
        sessionManager.clearSession()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onPause() {
        super.onPause()
        isActive = false
        // Hentikan proses atau layanan saat aktivitas tidak terlihat
        Log.d("MainActivity", "onPause: Proses dihentikan sementara.")
    }

    override fun onStop() {
        super.onStop()
        isActive = false
        // Bersihkan sumber daya atau proses yang berjalan
        Log.d("MainActivity", "onStop: Proses dihentikan sepenuhnya.")
    }

    override fun onResume() {
        super.onResume()
        isActive = true
        // Lanjutkan kembali proses saat aktivitas kembali terlihat
        Log.d("MainActivity", "onResume: Proses dilanjutkan.")
    }
}
