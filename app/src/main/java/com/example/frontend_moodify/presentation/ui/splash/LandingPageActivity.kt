package com.example.frontend_moodify.presentation.ui.splash

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.frontend_moodify.databinding.ActivityLandingPageBinding
import com.example.frontend_moodify.presentation.ui.auth.LoginActivity
import com.example.frontend_moodify.presentation.ui.auth.RegisterActivity

class LandingPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate layout using View Binding
        binding = ActivityLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animateLogo()
        animateWelcomeText()
        animateBanner()
        animateDescriptionText()
        animateButtons()

        // Handle button clicks
        binding.letsGoButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            binding.descriptionText.breakStrategy = LineBreaker.BREAK_STRATEGY_SIMPLE
            binding.descriptionText.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.descriptionText.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }
    }

    private fun animateLogo() {
        val fadeInLogo = ObjectAnimator.ofFloat(binding.logoImage, "alpha", 0f, 1f)
        fadeInLogo.duration = 1000
        fadeInLogo.start()
    }

    private fun animateWelcomeText() {
        val fadeInText = ObjectAnimator.ofFloat(binding.welcomeText, "alpha", 0f, 1f)
        fadeInText.duration = 1000
        fadeInText.start()
    }

    private fun animateBanner() {
        val slideInBanner = ObjectAnimator.ofFloat(binding.bannerImage, "translationY", -500f, 0f)
        slideInBanner.duration = 1000
        slideInBanner.start()
    }

    private fun animateDescriptionText() {
        val fadeInText = ObjectAnimator.ofFloat(binding.descriptionText, "alpha", 0f, 1f)
        fadeInText.duration = 1000
        fadeInText.start()
    }

    private fun animateButtons() {
        val slideInButtons = ObjectAnimator.ofFloat(binding.buttonContainer, "translationY", 300f, 0f)
        slideInButtons.duration = 1000 // Durasi animasi 1 detik
        slideInButtons.start()
    }
}
