package com.example.frontend_moodify.data.remote.network

import com.example.frontend_moodify.data.remote.response.nation.NationResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NationApiService {
//    @GET("nations")
//    suspend fun getNations(
//        @Query("offset") offset: Int,
//        @Query("limit") limit: Int = 10
//    ): NationResponse

    @GET("nations")
    suspend fun getNations(): NationResponse
}