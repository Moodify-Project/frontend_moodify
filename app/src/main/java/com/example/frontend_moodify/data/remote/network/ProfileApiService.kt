package com.example.frontend_moodify.data.remote.network

import com.example.frontend_moodify.data.remote.response.profile.ProfileResponse
import retrofit2.Call
import retrofit2.http.GET


interface ProfileApiService {
    @GET("profiles/me")
    fun getProfile(): Call<ProfileResponse>
}