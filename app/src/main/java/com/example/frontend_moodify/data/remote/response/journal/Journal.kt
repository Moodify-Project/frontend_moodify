package com.example.frontend_moodify.data.remote.response.journal

data class Journal(
    val emailAuthor: String,
    val journalId: String,
    val content: String,
    val createdAt: String,
    val updatedAt: String,
    val isPredicted: Boolean
)
