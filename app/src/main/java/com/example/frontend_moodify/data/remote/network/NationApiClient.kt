package com.example.frontend_moodify.data.remote.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NationApiClient {
    private const val BASE_URL = "http://35.219.12.145:8000/"

//    val apiService: NationApiService by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(NationApiService::class.java)
//    }
    val api: NationApiService by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(NationApiService::class.java)
    }
}