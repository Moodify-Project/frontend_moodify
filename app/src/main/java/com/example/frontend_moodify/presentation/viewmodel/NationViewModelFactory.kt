// NationViewModelFactory.kt
package com.example.frontend_moodify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.frontend_moodify.presentation.repository.NationRepository

class NationViewModelFactory(private val repository: NationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

