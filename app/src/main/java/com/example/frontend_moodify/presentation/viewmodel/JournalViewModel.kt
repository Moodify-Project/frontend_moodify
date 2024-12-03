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

    fun fetchJournalByDate(date: String) {
        viewModelScope.launch {
            try {
                val response = repository.getJournalByDate(date)
                if (response.status) {
                    _journalContent.postValue(response.journal.content)
                } else {
                    _journalContent.postValue("Journal is empty") // Tampilkan pesan ini jika tidak ada journal
                }
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    _journalContent.postValue("Journal is empty") // Menangani kesalahan 404
                } else {
                    _errorMessage.postValue("Kesalahan jaringan: ${e.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Kesalahan jaringan: ${e.message}")
            }
        }
    }
}