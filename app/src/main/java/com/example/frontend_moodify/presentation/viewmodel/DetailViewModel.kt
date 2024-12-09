package com.example.frontend_moodify.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_moodify.data.remote.network.ArticleApiService
import com.example.frontend_moodify.data.remote.response.bookmark.BookmarkRequest
import com.example.frontend_moodify.data.remote.response.news.Article
import kotlinx.coroutines.launch

class DetailViewModel(private val apiService: ArticleApiService) : ViewModel() {

    private val _articleDetail = MutableLiveData<Article>()
    val articleDetail: LiveData<Article> get() = _articleDetail

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isBookmarked = MutableLiveData<Boolean>()
    val isBookmarked: LiveData<Boolean> = _isBookmarked

    fun setBookmarkStatus(isBookmarked: Boolean) {
        _isBookmarked.value = isBookmarked
    }

    fun fetchArticleDetail(articleId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.getArticleDetail(articleId)
                if (response.isSuccessful) {
                    response.body()?.result?.let { article ->
                        _articleDetail.postValue(article)
                        Log.d("DetailViewModel", "Article Detail fetched successfully: $article")
                    }
                } else {
                    val errorMessage = "Error: ${response.message()}"
                    _error.postValue(errorMessage)
                    Log.e("DetailViewModel", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Network Error: ${e.message}"
                _error.postValue(errorMessage)
                Log.e("DetailViewModel", errorMessage)
            } finally {
                _loading.value = false
            }
        }
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
