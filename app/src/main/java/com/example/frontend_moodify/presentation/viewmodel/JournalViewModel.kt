package com.example.frontend_moodify.presentation.viewmodel

import androidx.lifecycle.*
import com.example.frontend_moodify.presentation.repository.JournalRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class JournalViewModel(private val repository: JournalRepository) : ViewModel() {
    private val _journalContent = MutableLiveData<String>()
    val journalContent: LiveData<String> get() = _journalContent

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    fun fetchJournalByDate(date: String) {
        viewModelScope.launch {
            try {
                val response = repository.getJournalByDate(date)
                if (response.status) {
                    _journalContent.postValue(response.journal.content)
                } else {
                    _journalContent.postValue("Journal is empty")
                }
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    _journalContent.postValue("Journal is empty")
                } else {
                    _errorMessage.postValue("Kesalahan jaringan: ${e.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Kesalahan jaringan: ${e.message}")
            }
        }
    }

    fun saveOrUpdateJournal(date: String, content: String) {
        viewModelScope.launch {
            try {
                val response = if (_journalContent.value == "Journal is empty") {
                     repository.createJournal(content).also {
                         _journalContent.value = content // Perbarui konten di ViewModel
                     }
//                    _errorMessage.value = response.message
                } else {
                    repository.updateJournal(date, content).also {
                        _journalContent.value = content // Pastikan konten terbaru disimpan
                    }
//                    _errorMessage.value = response.message
                }
                _successMessage.value = response.message
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
}
