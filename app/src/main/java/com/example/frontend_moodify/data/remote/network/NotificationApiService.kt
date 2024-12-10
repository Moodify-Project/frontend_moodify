package com.example.frontend_moodify.data.remote.network

import com.example.frontend_moodify.data.remote.response.notification.NotificationResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NotificationApiService {
//    @GET("notifications")
//    suspend fun getNotifications(): NotificationResponse
    @POST("notifications")
    fun sendNotification(@Body body: Map<String, String>): Call<Unit>
}