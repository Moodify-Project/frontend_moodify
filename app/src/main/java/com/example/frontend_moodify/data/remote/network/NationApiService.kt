package com.example.frontend_moodify.data.remote.network

import com.example.frontend_moodify.data.remote.response.nation.NationResponse
import retrofit2.Response
import retrofit2.http.GET

interface NationApiService {
    @GET("nations")
    suspend fun getNations(): Response<NationResponse>
}