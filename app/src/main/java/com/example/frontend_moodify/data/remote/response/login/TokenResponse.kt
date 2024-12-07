package com.example.frontend_moodify.data.remote.response.login

data class TokenResponse(
//    val accessToken: String,
//    val refreshToken: String
    val error: Boolean,
    val message: String,
    val accessToken: String?,
    val expiredDate: String?
)
