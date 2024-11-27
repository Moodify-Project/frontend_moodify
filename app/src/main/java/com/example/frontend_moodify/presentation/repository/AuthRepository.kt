package com.example.frontend_moodify.presentation.repository

import com.example.frontend_moodify.data.remote.network.AuthApiService
import com.example.frontend_moodify.data.remote.response.login.LoginRequest
import com.example.frontend_moodify.data.remote.response.login.LoginResponse
import com.example.frontend_moodify.data.remote.response.register.RegisterRequest
import com.example.frontend_moodify.data.remote.response.register.RegisterResponse
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
                null // Jika server mengembalikan status gagal
            }
        } catch (e: Exception) {
            throw IOException("Registration failed: ${e.message}")
        }
    }
}
