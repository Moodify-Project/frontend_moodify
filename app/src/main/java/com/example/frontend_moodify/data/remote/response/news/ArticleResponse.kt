package com.example.frontend_moodify.data.remote.response.news

data class ArticleResponse(
    val status: Boolean,
    val message: String,
//    val index: Int,
//    val totalArticle: Int,
    val data: List<Article>
)
