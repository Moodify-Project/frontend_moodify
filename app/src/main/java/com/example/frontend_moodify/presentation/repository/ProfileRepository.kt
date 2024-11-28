package com.example.frontend_moodify.presentation.repository

import com.example.frontend_moodify.data.remote.network.ProfileApiService
import com.example.frontend_moodify.data.remote.response.profile.ProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileRepository(private val profileApiService: ProfileApiService) {
    fun getProfile(callback: (ProfileResponse?) -> Unit) {
        profileApiService.getProfile().enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}

