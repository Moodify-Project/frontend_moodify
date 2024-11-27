package com.example.frontend_moodify.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SessionManager(context: Context)  {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveAccessToken(token: String) {
        prefs.edit().putString("ACCESS_TOKEN", token).apply()
    }

    fun getAccessToken(): String? {
        val token = prefs.getString("ACCESS_TOKEN", null)
        Log.d("SessionManager", "Token retrieved: $token")
        return token
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}