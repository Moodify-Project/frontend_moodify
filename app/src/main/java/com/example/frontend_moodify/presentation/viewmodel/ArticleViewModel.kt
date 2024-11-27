//package com.example.frontend_moodify.presentation.viewmodel
//
//import android.util.Log
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.liveData
//import androidx.lifecycle.viewModelScope
//import com.example.frontend_moodify.data.remote.network.ArticleApiService
//import com.example.frontend_moodify.data.remote.response.news.Article
//import com.example.frontend_moodify.presentation.repository.ArticleRepository
//import com.example.frontend_moodify.utils.SessionManager
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import retrofit2.HttpException
//
////class ArticleViewModel(private val repository: ArticleRepository) : ViewModel() {
////
////    private val _articles = MutableStateFlow<List<Article>>(emptyList())
////    val articles: StateFlow<List<Article>> = _articles
////
////    private val _loading = MutableStateFlow(false)
////    val loading: StateFlow<Boolean> = _loading
////
////    private val _error = MutableStateFlow<String?>(null)
////    val error: StateFlow<String?> = _error
////
////    fun fetchArticles(token: String) {
////        viewModelScope.launch {
////
////            try {
////                val response = repository.getArticles("Bearer $token")
////                _articles.emit(response)
////            } catch (e: HttpException) {
////                if (e.code() == 401) {
////                    _error.emit("Unauthorized: Please login again.")
////                } else {
////                    _error.emit("Error: ${e.message}")
////                }
////            }
//////            _loading.value = true
//////            _error.value = null
//////            try {
//////                val response = repository.fetchArticles(token)
//////                if (response.status) {
//////                    _articles.value = response.data // Memastikan data diterima dan disimpan di _articles
//////                } else {
//////                    _error.value = "Failed to fetch articles"
//////                }
//////            } catch (e: Exception) {
//////                _error.value = e.message // Tangani error lebih baik
//////            } finally {
//////                _loading.value = false
//////            }
////        }
////    }
////}
//
////class ArticleViewModel(private val repository: ArticleRepository) : ViewModel() {
////    private val _articles = MutableLiveData<List<Article>>()
////    val articles: LiveData<List<Article>> get() = _articles
////
////    private val _isLoading = MutableLiveData<Boolean>()
////    val isLoading: LiveData<Boolean> get() = _isLoading
////
////    private val _errorMessage = MutableLiveData<String>()
////    val errorMessage: LiveData<String> get() = _errorMessage
////
////    fun fetchArticles(token: String) {
////        viewModelScope.launch {
////            _isLoading.value = true
////            try {
////                val response = repository.fetchArticles(token)
////                if (response.status) {
////                    _articles.value = response.data
////                } else {
////                    _errorMessage.value = response.message
////                }
////            } catch (e: Exception) {
////                _errorMessage.value = e.message
////            } finally {
////                _isLoading.value = false
////            }
////        }
////    }
////}
//
//class ArticleViewModel(private val repository: ArticleRepository) : ViewModel() {
//    private val _articles = MutableLiveData<List<Article>>()
//    val articles: LiveData<List<Article>> get() = _articles
//
//    private val _isLoading = MutableLiveData<Boolean>()
//    val isLoading: LiveData<Boolean> get() = _isLoading
//
//    fun fetchArticles(token: String) {
//        _isLoading.value = true
//        viewModelScope.launch {
//            try {
//                val articles = repository.getArticles(token)
//                _articles.value = articles
//            } catch (e: Exception) {
//                Log.e("ArticleViewModel", "Error fetching articles: ${e.message}")
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
//}