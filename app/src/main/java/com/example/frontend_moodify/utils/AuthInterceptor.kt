package com.example.frontend_moodify.utils

import com.auth0.android.jwt.JWT
import com.example.frontend_moodify.data.remote.network.AuthApiService
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        sessionManager.getAccessToken()?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

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
