package com.example.frontend_moodify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.frontend_moodify.presentation.repository.ArticleRepository

class BookmarkViewModelFactory(private val articleRepository: ArticleRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookmarkViewModel(articleRepository) as T
    }
}