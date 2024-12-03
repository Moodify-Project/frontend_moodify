// NationViewModel.kt
package com.example.frontend_moodify.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_moodify.presentation.repository.NationRepository
import kotlinx.coroutines.launch

class NationViewModel(private val repository: NationRepository) : ViewModel() {
    private val _nations = MutableLiveData<List<String>>()
    val nations: LiveData<List<String>> = _nations

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchNations() {
        Log.d("NationsViewModel", "Fetching nations...")
        viewModelScope.launch {
            try {
                val result = repository.fetchNations()
                Log.d("NationsViewModel", "Fetched Nations: $result")
                _nations.value = result
            } catch (e: Exception) {
                Log.e("NationsViewModel", "Error fetching nations: ${e.message}")
                _error.value = e.message
            }
        }
    }
}

