package com.example.frontend_moodify.presentation.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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

        animateElements()

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

    private fun animateElements() {
        val bannerImageTranslationX = ObjectAnimator.ofFloat(binding.bannerImage, "translationX", -30f, 30f)
        bannerImageTranslationX.duration = 2000
        bannerImageTranslationX.repeatCount = ObjectAnimator.INFINITE
        bannerImageTranslationX.repeatMode = ObjectAnimator.REVERSE
        bannerImageTranslationX.interpolator = android.view.animation.AccelerateDecelerateInterpolator()

        val titleAlpha = ObjectAnimator.ofFloat(binding.loginTitle, "alpha", 0f, 1f).setDuration(500)
        val titleTranslationY = ObjectAnimator.ofFloat(binding.loginTitle, "translationY", -50f, 0f).setDuration(500)

        val emailAlpha = ObjectAnimator.ofFloat(binding.emailInputLayout, "alpha", 0f, 1f).setDuration(500)
        val emailTranslationY = ObjectAnimator.ofFloat(binding.emailInputLayout, "translationY", 50f, 0f).setDuration(500)

        val passwordAlpha = ObjectAnimator.ofFloat(binding.passwordInputLayout, "alpha", 0f, 1f).setDuration(500)
        val passwordTranslationY = ObjectAnimator.ofFloat(binding.passwordInputLayout, "translationY", 50f, 0f).setDuration(500)

        val loginButtonAlpha = ObjectAnimator.ofFloat(binding.loginButton, "alpha", 0f, 1f).setDuration(500)
        val loginButtonTranslationY = ObjectAnimator.ofFloat(binding.loginButton, "translationY", 50f, 0f).setDuration(500)

        val forgotPasswordAlpha = ObjectAnimator.ofFloat(binding.forgotPassword, "alpha", 0f, 1f).setDuration(500)
        val forgotPasswordTranslationY = ObjectAnimator.ofFloat(binding.forgotPassword, "translationY", 50f, 0f).setDuration(500)

        val registerTextAlpha = ObjectAnimator.ofFloat(binding.registerText, "alpha", 0f, 1f).setDuration(500)
        val registerTextTranslationY = ObjectAnimator.ofFloat(binding.registerText, "translationY", 50f, 0f).setDuration(500)

        val registerLinkAlpha = ObjectAnimator.ofFloat(binding.registerLink, "alpha", 0f, 1f).setDuration(500)
        val registerLinkTranslationY = ObjectAnimator.ofFloat(binding.registerLink, "translationY", 50f, 0f).setDuration(500)

        val commonDelay = 1000L

        titleAlpha.startDelay = 200
        titleTranslationY.startDelay = 200
        emailAlpha.startDelay = 400
        emailTranslationY.startDelay = 400
        passwordAlpha.startDelay = 600
        passwordTranslationY.startDelay = 600
        loginButtonAlpha.startDelay = 800
        loginButtonTranslationY.startDelay = 800
        forgotPasswordAlpha.startDelay = commonDelay
        forgotPasswordTranslationY.startDelay = commonDelay

        registerTextAlpha.startDelay = commonDelay
        registerTextTranslationY.startDelay = commonDelay
        registerLinkAlpha.startDelay = commonDelay
        registerLinkTranslationY.startDelay = commonDelay

        val formAnimatorSet = AnimatorSet().apply {
            playTogether(
                titleAlpha, titleTranslationY,
                emailAlpha, emailTranslationY,
                passwordAlpha, passwordTranslationY,
                forgotPasswordAlpha, forgotPasswordTranslationY,
                loginButtonAlpha, loginButtonTranslationY
            )
        }

        val registerTextAndLinkAnimatorSet = AnimatorSet().apply {
            playTogether(registerTextAlpha, registerTextTranslationY, registerLinkAlpha, registerLinkTranslationY)
        }

        val finalAnimatorSet = AnimatorSet().apply {
            playSequentially(formAnimatorSet, registerTextAndLinkAnimatorSet)
        }

        finalAnimatorSet.start()
        bannerImageTranslationX.start()

    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    override fun onBackPressed() {
        if (isTaskRoot) {
            finishAffinity()
        } else {
            super.onBackPressed()
        }
    }
}
