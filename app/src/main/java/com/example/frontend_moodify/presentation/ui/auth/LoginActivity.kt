package com.example.frontend_moodify.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.frontend_moodify.MainActivity
import com.example.frontend_moodify.R
import com.example.frontend_moodify.data.remote.network.Injection
import com.example.frontend_moodify.databinding.ActivityLoginBinding
import com.example.frontend_moodify.presentation.viewmodel.AuthViewModel
import com.example.frontend_moodify.presentation.viewmodel.AuthViewModelFactory
import com.example.frontend_moodify.utils.SessionManager

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        if (sessionManager.getAccessToken() != null) {
            navigateToHome()
        }
        val repository = Injection.provideAuthRepository()
        viewModel = ViewModelProvider(this, AuthViewModelFactory(repository))[AuthViewModel::class.java]

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.loginResult.observe(this) { result ->
            result.onSuccess { token ->
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                navigateToHome()
                sessionManager.saveAccessToken(token)
            }.onFailure {
                Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            viewModel.login(email, password)
        }
    }
    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
