package com.example.frontend_moodify.data.remote.network

import com.example.frontend_moodify.data.remote.response.bookmark.BookmarkRequest
import com.example.frontend_moodify.data.remote.response.bookmark.BookmarkResponse
import com.example.frontend_moodify.data.remote.response.bookmark.BookmarkedArticlesResponse
import com.example.frontend_moodify.data.remote.response.news.ArticleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ArticleApiService {

    @GET("articles")
    suspend fun getArticles(): Response<ArticleResponse>

    @POST("articles/bookmark")
    suspend fun bookmarkArticle(
        @Body request: BookmarkRequest
    ): Response<BookmarkResponse>

    @DELETE("articles/{articleId}/bookmark")
    suspend fun removeBookmark(
        @Path("articleId") articleId: String
    ): Response<Unit>

    @GET("articles/bookmarks/me")
    suspend fun getBookmarkedArticles(): BookmarkedArticlesResponse

}
