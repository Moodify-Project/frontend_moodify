package com.example.frontend_moodify.presentation.repository

import com.example.frontend_moodify.data.remote.network.AuthApiService
import com.example.frontend_moodify.data.remote.response.login.LoginRequest
import com.example.frontend_moodify.data.remote.response.login.LoginResponse
import com.example.frontend_moodify.data.remote.response.login.RefreshTokenRequest
import com.example.frontend_moodify.data.remote.response.login.RefreshTokenResponse
import com.example.frontend_moodify.data.remote.response.login.TokenResponse
import com.example.frontend_moodify.data.remote.response.register.RegisterRequest
import com.example.frontend_moodify.data.remote.response.register.RegisterResponse
import retrofit2.Response
import java.io.IOException

class AuthRepository(private val apiService: AuthApiService) {
    suspend fun login(request: LoginRequest): LoginResponse {
        return apiService.login(request)
    }
    suspend fun register(name: String, email: String, password: String): RegisterResponse? {
        return try {
            val response = apiService.register(RegisterRequest(name, email, password))
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            throw IOException("Registration failed: ${e.message}")
        }
    }
    suspend fun refreshToken(expiredDate: String): Result<RefreshTokenResponse> {
        return try {
            val response = apiService.refreshToken(RefreshTokenRequest(expiredDate))
            if (response.error) {
                Result.failure(Exception(response.message))
            } else {
                Result.success(response)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
