package com.example.frontend_moodify.data.remote.network

import com.example.frontend_moodify.data.remote.response.notification.NotificationResponse
import retrofit2.http.GET

interface NotificationApiService {
    @GET("notifications")
    suspend fun getNotifications(): NotificationResponse
}