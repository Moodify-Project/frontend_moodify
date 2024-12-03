package com.example.frontend_moodify.presentation.repository

import com.example.frontend_moodify.data.remote.network.JournalApiService

class JournalRepository(private val apiService: JournalApiService) {
    suspend fun getJournalByDate(date: String) = apiService.getJournalByDate(date)
}
