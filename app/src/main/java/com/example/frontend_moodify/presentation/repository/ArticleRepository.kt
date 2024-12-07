package com.example.frontend_moodify.presentation.repository

import com.example.frontend_moodify.data.remote.network.ArticleApiService
import com.example.frontend_moodify.data.remote.response.bookmark.BookmarkedArticlesResponse
import com.example.frontend_moodify.data.remote.response.news.Article
import com.example.frontend_moodify.data.remote.response.news.ArticleResponse
import retrofit2.Response

class ArticleRepository(private val apiService: ArticleApiService) {
    suspend fun getArticles(): Response<ArticleResponse> {
        return apiService.getArticles()
    }

//    suspend fun getArticleDetail(articleId: String): Response<Article> {
//        return apiService.getArticleDetail(articleId)
//    }

    suspend fun getBookmarkedArticles(): BookmarkedArticlesResponse {
        return apiService.getBookmarkedArticles()
    }
}
