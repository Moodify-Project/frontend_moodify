package com.example.frontend_moodify.data.remote.network

import android.content.Context
import com.example.frontend_moodify.presentation.repository.ArticleRepository
import com.example.frontend_moodify.presentation.repository.AuthRepository
import com.example.frontend_moodify.presentation.repository.JournalRepository
import com.example.frontend_moodify.presentation.repository.NationRepository
import com.example.frontend_moodify.presentation.repository.ProfileRepository
import com.example.frontend_moodify.presentation.viewmodel.AuthViewModelFactory
import com.example.frontend_moodify.utils.AuthInterceptor
import com.example.frontend_moodify.utils.SessionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Injection {
    private const val BASE_URL = "http://35.219.12.145:8000/api/v1/"

    private fun provideAuthApiService(): AuthApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    fun provideAuthRepository(): AuthRepository {
        return AuthRepository(provideAuthApiService())
    }

//    private fun provideOkHttpClient(sessionManager: SessionManager): OkHttpClient {
//        return OkHttpClient.Builder()
//            .addInterceptor(AuthInterceptor(sessionManager))
//            .build()
//    }
    private fun provideAuthInterceptor(sessionManager: SessionManager): AuthInterceptor {
        val authRepository = provideAuthRepository()
        return AuthInterceptor(sessionManager, authRepository)
    }

    private fun provideOkHttpClient(sessionManager: SessionManager): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(provideAuthInterceptor(sessionManager))
            .build()
    }

    private fun provideRetrofit(sessionManager: SessionManager): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideOkHttpClient(sessionManager))
            .build()
    }

    fun provideArticleApiService(sessionManager: SessionManager): ArticleApiService {
        return provideRetrofit(sessionManager).create(ArticleApiService::class.java)
    }

    fun provideArticleRepository(sessionManager: SessionManager): ArticleRepository {
        return ArticleRepository(provideArticleApiService(sessionManager))
    }

    fun provideProfileApiService(sessionManager: SessionManager): ProfileApiService {
        return provideRetrofit(sessionManager).create(ProfileApiService::class.java)
    }

    fun provideProfileRepository(sessionManager: SessionManager): ProfileRepository {
        return ProfileRepository(provideProfileApiService(sessionManager))
    }

    fun provideJournalApiService(sessionManager: SessionManager): JournalApiService {
        return provideRetrofit(sessionManager).create(JournalApiService::class.java)
    }

    fun provideJournalRepository(sessionManager: SessionManager): JournalRepository {
        return JournalRepository(provideJournalApiService(sessionManager))
    }

    fun provideNationsRepository(): NationRepository {
        val apiService = NationApiClient.api
        return NationRepository(apiService)
    }

    fun provideNotificationApiService(sessionManager: SessionManager): NotificationApiService {
        return provideRetrofit(sessionManager).create(NotificationApiService::class.java)
    }
}