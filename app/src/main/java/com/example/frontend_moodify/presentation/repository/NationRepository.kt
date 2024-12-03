package com.example.frontend_moodify.presentation.repository

import android.util.Log
import com.example.frontend_moodify.data.remote.network.NationApiService

class NationRepository(private val apiService: NationApiService) {
    suspend fun fetchNations(): List<String> {
        val response = apiService.getNations()
        Log.d("NationsRepository", "API Response Code: ${response.code()}")
        Log.d("NationsRepository", "API Response Body: ${response.body()}")
        if (response.isSuccessful) {
            return response.body()?.result ?: emptyList()
        } else {
            Log.e("NationsRepository", "Failed to fetch nations: ${response.message()}")
            throw Exception("Failed to fetch nations: ${response.message()}")
        }
    }
}
