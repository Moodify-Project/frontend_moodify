package com.example.frontend_moodify.data.remote.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NationApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://35.219.12.145:8000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: NationApiService = retrofit.create(NationApiService::class.java)
}