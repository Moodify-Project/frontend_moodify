package com.example.frontend_moodify.data.remote.network

import android.content.Context
//import com.example.frontend_moodify.presentation.repository.ArticleRepository
import com.example.frontend_moodify.presentation.repository.AuthRepository
import com.example.frontend_moodify.utils.AuthInterceptor
import com.example.frontend_moodify.utils.SessionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Injection {
    private const val BASE_URL = "http://35.219.12.145:8000/api/v1/"

//    private fun provideApiService(): ArticleApiService {
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ArticleApiService::class.java)
//    }
//
//    fun provideArticleRepository(): ArticleRepository {
//        return ArticleRepository(provideApiService())
//    }

//    fun provideApiService(): ArticleApiService {
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ArticleApiService::class.java)
//    }
//
//    // Fungsi untuk menyediakan repository
//    fun provideArticleRepository(): ArticleRepository {
//        return ArticleRepository(provideApiService())
//    }

    private fun provideAuthApiService(): AuthApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
    private fun provideRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun provideAuthRepository(): AuthRepository {
        return AuthRepository(provideAuthApiService())
    }

    private fun provideOkHttpClient(context: Context): OkHttpClient {
        val sessionManager = SessionManager(context)
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .build()
    }

//    fun provideArticleRepository(context: Context): ArticleRepository {
//        return ArticleRepository(provideRetrofit(context).create(ArticleApiService::class.java))
//    }
}