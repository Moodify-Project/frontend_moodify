package com.example.frontend_moodify.data.remote.response.nation

data class NationResponse(
    val success: Boolean,
    val totalNation: Int,
    val result: List<String>
)
