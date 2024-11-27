//package com.example.frontend_moodify.presentation.repository
//
//import com.example.frontend_moodify.data.remote.network.ArticleApiService
//import com.example.frontend_moodify.data.remote.response.news.Article
//import com.example.frontend_moodify.data.remote.response.news.ArticleResponse
//
//
//class ArticleRepository(private val apiService: ArticleApiService) {
////    suspend fun fetchArticles(token: String): List<Article> {
////        return apiService.getArticles("Bearer $token")
////    }
////    suspend fun getArticles(token: String): List<Article> {
////        return apiService.getArticles(token)
////    }
////    suspend fun getArticles(token: String): List<Article>? {
////        val response = apiService.getArticles(token)
////        return if (response.isSuccessful) {
////            response.body() // Mengembalikan daftar artikel jika berhasil
////        } else {
////            null // Mengembalikan null jika gagal
////        }
////    }
////    suspend fun fetchArticles(token: String) = apiService.getArticles(token)
//    suspend fun getArticles(token: String): List<Article> {
//        val response = apiService.getArticles("Bearer $token")
//        if (response.status) {
//            return response.data
//        } else {
//            throw Exception(response.message)
//        }
//    }
//
//}
