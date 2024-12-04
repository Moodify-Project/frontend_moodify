package com.example.frontend_moodify.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_moodify.data.remote.network.ArticleApiService
import com.example.frontend_moodify.data.remote.response.bookmark.BookmarkRequest
import kotlinx.coroutines.launch

class DetailViewModel(private val apiService: ArticleApiService) : ViewModel() {
    private val _isBookmarked = MutableLiveData<Boolean>()
    val isBookmarked: LiveData<Boolean> = _isBookmarked

    fun setBookmarkStatus(isBookmarked: Boolean) {
        _isBookmarked.value = isBookmarked
    }

    fun fetchBookmarkStatus(articleId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getBookmarkedArticles()
                if (response.status) {
                    val isBookmarked = response.articles.any { it.id == articleId }
                    _isBookmarked.postValue(isBookmarked)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleBookmark(articleId: String) {
        if (_isBookmarked.value == true) {
            removeBookmark(articleId)
        } else {
            addBookmark(articleId)
        }
    }

    fun addBookmark(articleId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.bookmarkArticle(BookmarkRequest(articleId))
                if (response.isSuccessful) {
                    _isBookmarked.value = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removeBookmark(articleId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.removeBookmark(articleId)
                if (response.isSuccessful) {
                    _isBookmarked.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
