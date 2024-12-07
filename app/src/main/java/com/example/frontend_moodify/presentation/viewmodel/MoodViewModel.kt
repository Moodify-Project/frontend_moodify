package com.example.frontend_moodify.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_moodify.data.remote.response.journal.Moods
import com.example.frontend_moodify.presentation.repository.JournalRepository
import kotlinx.coroutines.launch

class MoodViewModel(private val repository: JournalRepository) : ViewModel() {
    private val _moods = MutableLiveData<Moods>()
    val moods: LiveData<Moods> = _moods

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchMoods(date: String) {
        Log.d("MoodViewModel", "Fetching moods for date: $date")

        viewModelScope.launch {
            try {
                val response = repository.getWeeklyMoods(date)
                if (!response.error) {
                    Log.d("MoodViewModel", "Mood data fetched successfully: ${response.moods}")
                    _moods.value = response.moods
                } else {
                    Log.e("MoodViewModel", "Error in response: ${response.error}")
                    _errorMessage.value = "Terjadi kesalahan saat memuat data"
                }
            } catch (e: Exception) {
                Log.e("MoodViewModel", "Failed to fetch mood data: ${e.message}")
                _errorMessage.value = e.message ?: "Kesalahan tidak diketahui"
            }
        }
    }
}
