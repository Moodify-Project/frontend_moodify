package com.example.frontend_moodify.presentation.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.frontend_moodify.MainActivity
import com.example.frontend_moodify.R
import com.example.frontend_moodify.data.remote.network.Injection
import com.example.frontend_moodify.databinding.ActivityRegisterBinding
import com.example.frontend_moodify.presentation.ui.splash.LandingPageActivity
import com.example.frontend_moodify.presentation.viewmodel.AuthViewModel
import com.example.frontend_moodify.presentation.viewmodel.AuthViewModelFactory
import com.example.frontend_moodify.utils.SessionManager

class RegisterActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(Injection.provideAuthRepository())
    }
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animateElements()

        binding.backButton.setOnClickListener {
            startActivity(Intent(this, LandingPageActivity::class.java))
        }

        binding.loginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.registerButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.length < 8) {
                Toast.makeText(this, "Please enter valid details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.register(name, email, password)
        }

//        authViewModel.error.observe(this) { errorMessage ->
//            errorMessage?.let {
//                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
//            }
//        }
        authViewModel.registerResult.observe(this, Observer { result ->
            result.fold(
                onSuccess = { message ->
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                onFailure = { exception ->
                    Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                }
            )
        })

        authViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
    override fun onBackPressed() {
        if (isTaskRoot) {
            finishAffinity()
        } else {
            super.onBackPressed()
        }
    }

    private fun animateElements() {

        val bannerImageTranslationX = ObjectAnimator.ofFloat(binding.bannerImage, "translationX", -30f, 30f)
        bannerImageTranslationX.duration = 2000 // Durasi lebih lambat untuk animasi gerak banner
        bannerImageTranslationX.repeatCount = ObjectAnimator.INFINITE
        bannerImageTranslationX.repeatMode = ObjectAnimator.REVERSE
        bannerImageTranslationX.interpolator = android.view.animation.AccelerateDecelerateInterpolator()

        val titleAlpha = ObjectAnimator.ofFloat(binding.registerTitle, "alpha", 0f, 1f).setDuration(500)
        val titleTranslationY = ObjectAnimator.ofFloat(binding.registerTitle, "translationY", -50f, 0f).setDuration(500)

        val nameAlpha = ObjectAnimator.ofFloat(binding.nameInputLayout, "alpha", 0f, 1f).setDuration(500)
        val nameTranslationY = ObjectAnimator.ofFloat(binding.nameInputLayout, "translationY", 50f, 0f).setDuration(500)

        val emailAlpha = ObjectAnimator.ofFloat(binding.emailInputLayout, "alpha", 0f, 1f).setDuration(500)
        val emailTranslationY = ObjectAnimator.ofFloat(binding.emailInputLayout, "translationY", 50f, 0f).setDuration(500)

        val passwordAlpha = ObjectAnimator.ofFloat(binding.passwordInputLayout, "alpha", 0f, 1f).setDuration(500)
        val passwordTranslationY = ObjectAnimator.ofFloat(binding.passwordInputLayout, "translationY", 50f, 0f).setDuration(500)

        val registerButtonAlpha = ObjectAnimator.ofFloat(binding.registerButton, "alpha", 0f, 1f).setDuration(500)
        val registerButtonTranslationY = ObjectAnimator.ofFloat(binding.registerButton, "translationY", 50f, 0f).setDuration(500)

        val agreementTextAlpha = ObjectAnimator.ofFloat(binding.agreementText, "alpha", 0f, 1f).setDuration(500)
        val agreementTextTranslationY = ObjectAnimator.ofFloat(binding.agreementText, "translationY", 50f, 0f).setDuration(500)

        val loginPromptAlpha = ObjectAnimator.ofFloat(binding.loginPrompt, "alpha", 0f, 1f).setDuration(500)
        val loginPromptTranslationY = ObjectAnimator.ofFloat(binding.loginPrompt, "translationY", 50f, 0f).setDuration(500)

        val loginLinkAlpha = ObjectAnimator.ofFloat(binding.loginLink, "alpha", 0f, 1f).setDuration(500)
        val loginLinkTranslationY = ObjectAnimator.ofFloat(binding.loginLink, "translationY", 50f, 0f).setDuration(500)

        val commonDelay = 1000L

        titleAlpha.startDelay = 200
        titleTranslationY.startDelay = 200
        nameAlpha.startDelay = 400
        nameTranslationY.startDelay = 400
        emailAlpha.startDelay = 600
        emailTranslationY.startDelay = 600
        passwordAlpha.startDelay = 800
        passwordTranslationY.startDelay = 800
        registerButtonAlpha.startDelay = 1000
        registerButtonTranslationY.startDelay = 1000
        agreementTextAlpha.startDelay = 1200
        agreementTextTranslationY.startDelay = 1200
        loginPromptAlpha.startDelay = 1400
        loginPromptTranslationY.startDelay = 1400
        loginLinkAlpha.startDelay = 1600
        loginLinkTranslationY.startDelay = 1600

        val formAnimatorSet = AnimatorSet().apply {
            playTogether(
                titleAlpha, titleTranslationY,
                nameAlpha, nameTranslationY,
                emailAlpha, emailTranslationY,
                passwordAlpha, passwordTranslationY,
                registerButtonAlpha, registerButtonTranslationY,
                agreementTextAlpha, agreementTextTranslationY
            )
        }

        val loginTextAnimatorSet = AnimatorSet().apply {
            playTogether(loginPromptAlpha, loginPromptTranslationY, loginLinkAlpha, loginLinkTranslationY)
        }

        val finalAnimatorSet = AnimatorSet().apply {
            playSequentially(formAnimatorSet, loginTextAnimatorSet)
        }

        finalAnimatorSet.start()


        bannerImageTranslationX.start()
    }
}