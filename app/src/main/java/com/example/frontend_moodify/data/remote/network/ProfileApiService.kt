package com.example.frontend_moodify.data.remote.network

import com.example.frontend_moodify.data.remote.response.profile.ProfilePhotoResponse
import com.example.frontend_moodify.data.remote.response.profile.ProfileResponse
import com.example.frontend_moodify.data.remote.response.profile.UpdateProfileResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Part


interface ProfileApiService {
    @GET("profiles/me")
    suspend fun getProfile(): ProfileResponse

    @PUT("profiles/me")
    suspend fun updateProfile(@Body body: Map<String, String>): UpdateProfileResponse

    @Multipart
    @PATCH("profiles/photo")
    fun uploadProfilePhoto(
        @Part image: MultipartBody.Part
    ): Call<ResponseBody>
}