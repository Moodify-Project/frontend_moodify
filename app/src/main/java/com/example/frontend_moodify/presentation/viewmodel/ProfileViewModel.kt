//package com.example.frontend_moodify.presentation.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.frontend_moodify.data.remote.response.profile.Profile
//import com.example.frontend_moodify.presentation.repository.ProfileRepository
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {
//    private val _profileState = MutableStateFlow<Profile?>(null)
//    val profileState: StateFlow<Profile?> = _profileState
//
//    fun fetchProfile() {
//        viewModelScope.launch {
//            try {
//                val response = repository.getProfile()
//                if (response.status) {
//                    _profileState.value = response.result
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//}