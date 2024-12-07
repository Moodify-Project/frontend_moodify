package com.example.frontend_moodify.data.remote.response.news

data class ArticleDetailResponse(
    val error: Boolean,
    val message: String,
    val result: Article
)
