package com.example.frontend_moodify.utils

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Tambahkan Authorization Header
        sessionManager.getAccessToken()?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        // Tambahkan Cookie Header
        sessionManager.getRefreshTokenCookie()?.let { cookie ->
            requestBuilder.addHeader("Cookie", cookie)
        }

        return chain.proceed(requestBuilder.build())
    }
}

fun provideOkHttpClient(sessionManager: SessionManager): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(sessionManager))
        .build()
}