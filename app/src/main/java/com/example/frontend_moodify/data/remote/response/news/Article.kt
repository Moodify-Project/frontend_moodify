package com.example.frontend_moodify.data.remote.response.news

data class Article(
    val id: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val content: String,
    val countBookmarked: Int = 0
)
