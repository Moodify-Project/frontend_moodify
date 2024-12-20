package com.example.frontend_moodify.data.remote.network

import com.example.frontend_moodify.data.remote.response.login.LoginRequest
import com.example.frontend_moodify.data.remote.response.login.LoginResponse
import com.example.frontend_moodify.data.remote.response.login.RefreshTokenRequest
import com.example.frontend_moodify.data.remote.response.login.RefreshTokenResponse
import com.example.frontend_moodify.data.remote.response.register.RegisterRequest
import com.example.frontend_moodify.data.remote.response.register.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("auth/refresh_token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): RefreshTokenResponse
}