package com.example.frontend_moodify.data.remote.network

import com.example.frontend_moodify.data.remote.response.journal.JournalResponse
import com.example.frontend_moodify.data.remote.response.journal.JournalUpdateRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface JournalApiService {
    @GET("journals")
    suspend fun getJournalByDate(@Query("date") date: String): JournalResponse

    @POST("journals")
    suspend fun createJournal(@Body body: Map<String, String>): JournalResponse

    @PUT("journals")
    suspend fun updateJournal(
        @Query("date") date: String,
        @Body body: JournalUpdateRequest
    ): JournalResponse
}
