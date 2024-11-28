package com.example.frontend_moodify.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_moodify.data.remote.network.ArticleApiService
import com.example.frontend_moodify.data.remote.response.bookmark.BookmarkRequest
import kotlinx.coroutines.launch

class DetailViewModel(private val apiService: ArticleApiService) : ViewModel() {

    private val _isBookmarked = MutableLiveData<Boolean>()
    val isBookmarked: LiveData<Boolean> get() = _isBookmarked

    fun toggleBookmark(articleId: String) {
        viewModelScope.launch {
            try {
                if (_isBookmarked.value == true) {
//                    val response = apiService.removeBookmark(articleId)
//                    if (response.isSuccessful) {
//                        _isBookmarked.postValue(false)
//                    }
                } else {
                    val response = apiService.bookmarkArticle(BookmarkRequest(articleId))
                    if (response.isSuccessful) {
                        _isBookmarked.postValue(true)
                    }
                }
            } catch (e: Exception) {
                Log.e("BookmarkError", e.message.toString())
            }
        }
    }

    fun setBookmarkStatus(isBookmarked: Boolean) {
        _isBookmarked.postValue(isBookmarked)
    }
}