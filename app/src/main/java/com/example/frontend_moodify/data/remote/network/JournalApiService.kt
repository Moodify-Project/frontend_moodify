package com.example.frontend_moodify.data.remote.network

import com.example.frontend_moodify.data.remote.response.journal.JournalResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface JournalApiService {
    @GET("journals")
    suspend fun getJournalByDate(@Query("date") date: String): JournalResponse
}
