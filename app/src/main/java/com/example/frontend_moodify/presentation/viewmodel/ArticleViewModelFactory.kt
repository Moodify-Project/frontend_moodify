//package com.example.frontend_moodify.presentation.viewmodel
//
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.example.frontend_moodify.presentation.repository.ArticleRepository
//
////class ArticleViewModelFactory(private val articleRepository: ArticleRepository) : ViewModelProvider.Factory {
////    override fun <T : ViewModel> create(modelClass: Class<T>): T {
////        if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
////            return ArticleViewModel(articleRepository) as T
////        }
////        throw IllegalArgumentException("Unknown ViewModel class")
////    }
////}
//
//class ArticleViewModelFactory(private val repository: ArticleRepository) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
//            return ArticleViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
//
//
//
//
//
//
