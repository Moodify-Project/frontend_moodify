package com.example.frontend_moodify.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.auth0.android.jwt.JWT

class SessionManager(context: Context)  {
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

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}