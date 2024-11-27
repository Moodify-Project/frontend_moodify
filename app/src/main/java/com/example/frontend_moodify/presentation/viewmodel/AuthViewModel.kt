package com.example.frontend_moodify.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend_moodify.data.remote.response.login.LoginRequest
import com.example.frontend_moodify.data.remote.response.register.RegisterRequest
import com.example.frontend_moodify.data.remote.response.register.RegisterResponse
import com.example.frontend_moodify.presentation.repository.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _loginResult = MutableLiveData<Result<String>>()
    val loginResult: LiveData<Result<String>> get() = _loginResult

//    private val _registerResult = MutableLiveData<Result<RegisterResponse>>()
//    val registerResult: LiveData<Result<RegisterResponse>> = _registerResult

    private val _registerResult = MutableLiveData<Result<String>>()
    val registerResult: LiveData<Result<String>> get() = _registerResult

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = authRepository.login(LoginRequest(email, password))
                _loginResult.value = Result.success(response.accessToken)
            } catch (e: IOException) {
                _loginResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = authRepository.register(name, email, password)
                if (response != null) {
                    _registerResult.value = Result.success("Account created successfully!")
                } else {
                    _registerResult.value = Result.failure(Exception("Registration failed"))
                }
            } catch (e: Exception) {
                _registerResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
