package com.example.frontend_moodify.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.frontend_moodify.data.remote.response.profile.Profile
import kotlinx.coroutines.Dispatchers
import com.example.frontend_moodify.presentation.repository.ProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _profile = MutableLiveData<Profile?>()
    val profile: LiveData<Profile?> = _profile

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _updateStatus = MutableLiveData<Boolean>()
    val updateStatus: LiveData<Boolean> get() = _updateStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getProfile() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("ProfileViewModel", "Fetching profile data...")
                val response = repository.getProfile()
                _profile.postValue(response.result)
                Log.d("ProfileViewModel", "Profile data fetched: ${response.result}")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error fetching profile data: ${e.message}", e)
                _error.postValue(e.message ?: "Terjadi kesalahan")
            }
        }
    }
    fun updateProfile(name: String, gender: String, country: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.updateProfile(name, gender, country)
                if (response.status) {
                    _updateStatus.value = true
                } else {
                    _updateStatus.value = false
                    _error.value = "Gagal memperbarui profil"
                }
            } catch (e: Exception) {
                _updateStatus.value = false
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}