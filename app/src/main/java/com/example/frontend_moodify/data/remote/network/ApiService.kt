package com.example.frontend_moodify.data.remote.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class ScheduleRequest(val isActive: Boolean)

interface ApiService {
    @POST("/api/v1/notification")
    suspend fun setNotificationSchedule(@Body request: ScheduleRequest): Response<Unit>
}
