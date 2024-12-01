package com.example.frontend_moodify.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_moodify.data.remote.response.news.Article
import com.example.frontend_moodify.presentation.repository.ArticleRepository
import kotlinx.coroutines.launch

class ArticleViewModel(private val repository: ArticleRepository) : ViewModel() {
    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> get() = _articles

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchArticles() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.getArticles()
                if (response.isSuccessful) {
                    _articles.value = response.body()?.data
                } else {
                    _error.value = "Error ${response.code()}: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network Error: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

}
