package com.example.frontend_moodify.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.frontend_moodify.MainActivity
import com.example.frontend_moodify.data.remote.network.Injection
import com.example.frontend_moodify.databinding.ActivityLoginBinding
import com.example.frontend_moodify.presentation.repository.AuthRepository
import com.example.frontend_moodify.presentation.ui.splash.LandingPageActivity
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
            result.onSuccess { response ->
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                sessionManager.saveAccessToken(response.accessToken)
                sessionManager.saveRefreshToken(response.refreshToken)
                navigateToHome()
            }.onFailure {
                Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(email, password)
        }
        binding.backButton.setOnClickListener {
            val intent = Intent(this, LandingPageActivity::class.java)
            startActivity(intent)
        }
        binding.registerLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }
    override fun onBackPressed() {
        if (isTaskRoot) {
            finishAffinity()
        } else {
            super.onBackPressed()
        }
    }
    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
