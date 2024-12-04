package com.example.frontend_moodify.presentation.ui.splash

import android.content.Intent
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.text.Layout
import androidx.appcompat.app.AppCompatActivity
import com.example.frontend_moodify.MainActivity
import com.example.frontend_moodify.databinding.ActivityLandingPageBinding
import com.example.frontend_moodify.presentation.ui.auth.LoginActivity
import com.example.frontend_moodify.presentation.ui.auth.RegisterActivity
import com.example.frontend_moodify.utils.SessionManager

class LandingPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingPageBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        if (sessionManager.getAccessToken() != null) {
            navigateToMainActivity()
        }

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
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
