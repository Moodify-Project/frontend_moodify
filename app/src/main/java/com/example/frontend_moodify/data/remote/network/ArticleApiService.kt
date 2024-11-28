package com.example.frontend_moodify.data.remote.network

import com.example.frontend_moodify.data.remote.response.bookmark.BookmarkRequest
import com.example.frontend_moodify.data.remote.response.bookmark.BookmarkResponse
import com.example.frontend_moodify.data.remote.response.news.ArticleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ArticleApiService {

    @GET("articles")
    suspend fun getArticles(): Response<ArticleResponse>

    @POST("api/v1/articles/bookmark")
    suspend fun bookmarkArticle(
        @Body request: BookmarkRequest
    ): Response<BookmarkResponse>
}
