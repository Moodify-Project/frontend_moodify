package com.example.frontend_moodify.presentation.adapter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_moodify.data.remote.network.NationApiClient
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ProfileViewModel : ViewModel() {

    private val _nations = MutableLiveData<List<String>>()
    val nations: LiveData<List<String>> get() = _nations

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val allNations = mutableListOf<String>()
    private var offset = 0
    private val limit = 10

    fun fetchNations() {
        viewModelScope.launch {
            try {
                // Simulasi fetch API menggunakan Retrofit
                val response = NationApiClient.api.getNations()
                if (response.success) {
                    allNations.clear()
                    allNations.addAll(response.result)
                    loadMoreNations()
                } else {
                    _error.postValue("Failed to load nations")
                }
            } catch (e: HttpException) {
                _error.postValue("Network error")
            } catch (e: IOException) {
                _error.postValue("IO error: ${e.message}")
            }
        }
    }

    fun loadMoreNations() {
        val nextOffset = offset + limit
        if (offset < allNations.size) {
            _nations.postValue(allNations.subList(offset, nextOffset.coerceAtMost(allNations.size)))
            offset = nextOffset
        }
    }
}