package com.example.frontend_moodify.utils

import com.auth0.android.jwt.JWT
import com.example.frontend_moodify.data.remote.network.AuthApiService
import com.example.frontend_moodify.presentation.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AuthInterceptor(private val sessionManager: SessionManager, private val authRepository: AuthRepository) : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val requestBuilder = chain.request().newBuilder()
//
//        sessionManager.getAccessToken()?.let { token ->
//            requestBuilder.addHeader("Authorization", "Bearer $token")
//        }
//
//        sessionManager.getRefreshTokenCookie()?.let { cookie ->
//            requestBuilder.addHeader("Cookie", cookie)
//        }
//
//        return chain.proceed(requestBuilder.build())
//    }
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = sessionManager.getAccessToken()
        val expiredDate = sessionManager.getExpiredDate()

        // Cek apakah token sudah kadaluarsa
        if (expiredDate != null && isTokenExpired(expiredDate)) {
            runBlocking {
                val result = authRepository.refreshToken(expiredDate)
                if (result.isSuccess) {
                    result.getOrNull()?.let {
                        sessionManager.saveAccessToken(it.accessToken!!)
                        sessionManager.saveExpiredDate(it.expiredDate!!)
                    }
                } else {
                    sessionManager.clearSession()
                    throw IOException("Token expired and refresh failed: ${result.exceptionOrNull()?.message}")
                }
            }
        }

        val requestBuilder = chain.request().newBuilder()
        sessionManager.getAccessToken()?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        sessionManager.getRefreshTokenCookie()?.let { cookie ->
            requestBuilder.addHeader("Cookie", cookie)
        }
        return chain.proceed(requestBuilder.build())
    }

    private fun isTokenExpired(expiredDate: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val expiredTime = dateFormat.parse(expiredDate)?.time ?: 0L
        val now = System.currentTimeMillis()
        return now > expiredTime
    }

}

