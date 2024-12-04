package com.example.frontend_moodify.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_moodify.data.remote.response.news.Article
import com.example.frontend_moodify.presentation.repository.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookmarkViewModel(private val repository: ArticleRepository) : ViewModel() {
//    private val _bookmarkedArticles = MutableStateFlow<List<Article>>(emptyList())
//    val bookmarkedArticles: StateFlow<List<Article>> = _bookmarkedArticles
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading: StateFlow<Boolean> = _isLoading
//
//    private val _errorMessage = MutableStateFlow<String?>(null)
//    val errorMessage: StateFlow<String?> = _errorMessage


    private val _bookmarkedArticles = MutableLiveData<List<Article>>()
    val bookmarkedArticles: LiveData<List<Article>> = _bookmarkedArticles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage


//    fun fetchBookmarkedArticles() {
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                val response = repository.getBookmarkedArticles()
//                if (response.status) {
//                    _bookmarkedArticles.value = response.articles
//                } else {
//                    _errorMessage.value = response.message
//                }
//            } catch (e: Exception) {
//                _errorMessage.value = e.message
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
    fun fetchBookmarkedArticles() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getBookmarkedArticles()
                if (response.status) {
                    _bookmarkedArticles.postValue(response.articles)
                } else {
                    _errorMessage.postValue(response.message)
                }
            } catch (e: Exception) {
                _errorMessage.postValue(e.message)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}