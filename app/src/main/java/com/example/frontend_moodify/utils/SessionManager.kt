package com.example.frontend_moodify.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.auth0.android.jwt.JWT

class SessionManager(private val context: Context)  {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveAccessToken(token: String) {
        prefs.edit().putString("ACCESS_TOKEN", token).apply()
        Log.d("SessionManager", "Access Token Saved: $token")
    }

    fun getAccessToken(): String? {
        val token = prefs.getString("ACCESS_TOKEN", null)
        Log.d("SessionManager", "Token retrieved: $token")
        return token
    }

    fun saveRefreshToken(token: String) {
        prefs.edit().putString("REFRESH_TOKEN", token).apply()
        Log.d("SessionManager", "Refresh Token Saved: $token")
    }

    fun getRefreshToken(): String? {
        val token = prefs.getString("REFRESH_TOKEN", null)
        Log.d("SessionManager", "Token refresh: $token")
        return token
    }

    fun getRefreshTokenCookie(): String? {
        val refreshToken = getRefreshToken()
        return if (refreshToken != null) "refreshToken=$refreshToken" else null
    }

    fun saveExpiredDate(expiredDate: String) {
        prefs.edit().putString("EXPIRED_DATE", expiredDate).apply()
    }

    fun getExpiredDate(): String? {
        return prefs.getString("EXPIRED_DATE", null)
    }

    fun saveThemePreference(isDarkMode: Boolean) {
        prefs.edit().putBoolean("dark_mode", isDarkMode).apply()
    }

    fun getThemePreference(): Boolean {
        return prefs.getBoolean("dark_mode", false) // Default: light mode
    }

    fun saveDailyReminderPreference(isEnabled: Boolean) {
        prefs.edit().putBoolean("daily_reminder", isEnabled).apply()
    }

    fun getDailyReminderPreference(): Boolean {
        return prefs.getBoolean("daily_reminder", false) // Default: reminder off
    }

    fun clearSession() {
        prefs.edit().clear().apply()
        clearAppCacheAndFiles()
        Log.d("SessionManager", "Session cleared successfully.")
    }

    fun getContext(): Context {
        return context
    }

    private fun clearAppCacheAndFiles() {
        try {
            val cacheDir = context.cacheDir
            cacheDir?.deleteRecursively()

            val filesDir = context.filesDir
            filesDir?.deleteRecursively()

            Log.d("SessionManager", "Cache and files cleared successfully.")
        } catch (e: Exception) {
            Log.e("SessionManager", "Error clearing cache/files: ${e.message}")
        }
    }
}
