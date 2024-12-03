package com.example.frontend_moodify.presentation.repository

import android.util.Log
import com.example.frontend_moodify.data.remote.network.ProfileApiService
import com.example.frontend_moodify.data.remote.response.profile.ProfileResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileRepository(private val apiService: ProfileApiService) {

    suspend fun getProfile() = apiService.getProfile()

    suspend fun updateProfile(name: String, gender: String, country: String) =
        apiService.updateProfile(mapOf("name" to name, "gender" to gender, "country" to country))

    fun uploadProfileImage(file: File): Call<ResponseBody> {
        val mimeType = getMimeType(file) ?: "image/jpeg"
        val mediaType = mimeType.toMediaTypeOrNull()
        val requestBody = file.asRequestBody(mediaType)
        val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestBody)

        Log.d("ProfileRepository", "MIME type: $mimeType")
        Log.d("ProfileRepository", "File name: ${file.name}")

        return apiService.uploadProfilePhoto(multipartBody)
    }

    private fun getMimeType(file: File): String? {
        val extension = file.extension.lowercase()
        return when (extension) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            else -> null
        }
    }
}

