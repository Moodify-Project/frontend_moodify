package com.example.frontend_moodify.data.remote.response.journal

data class JournalResponse(
    val status: Boolean,
    val message: String,
    val journal: Journal
)
