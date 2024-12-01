package com.example.frontend_moodify.data.remote.response.bookmark

import com.example.frontend_moodify.data.remote.response.news.Article

data class BookmarkedArticlesResponse(
    val status: Boolean,
    val message: String,
    val articles: List<Article>
)
