package com.example.frontend_moodify.data.remote.response.login

data class RefreshTokenResponse(
    val error: Boolean,
    val message: String,
    val accessToken: String?,
    val expiredDate: String?
)
