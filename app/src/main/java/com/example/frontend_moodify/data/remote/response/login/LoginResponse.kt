package com.example.frontend_moodify.data.remote.response.login

data class LoginResponse(
    val status: Boolean,
    val message: String,
    val accessToken: String,
    val refreshToken: String
)
