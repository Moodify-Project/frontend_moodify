package com.example.frontend_moodify.presentation.repository

import com.example.frontend_moodify.data.remote.network.JournalApiService
import com.example.frontend_moodify.data.remote.response.journal.JournalContent
import com.example.frontend_moodify.data.remote.response.journal.JournalResponse
import com.example.frontend_moodify.data.remote.response.journal.JournalUpdateRequest

class JournalRepository(private val apiService: JournalApiService) {
    suspend fun getJournalByDate(date: String) = apiService.getJournalByDate(date)

    suspend fun createJournal(content: String) = apiService.createJournal(
        mapOf("journalContent" to content)
    )

    suspend fun updateJournal(date: String, content: String): JournalResponse {
        return apiService.updateJournal(date, JournalUpdateRequest(JournalContent(content)))
    }
}
