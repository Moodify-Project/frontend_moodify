package com.example.frontend_moodify.presentation.ui.splash

import android.content.Intent
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.text.Layout
import androidx.appcompat.app.AppCompatActivity
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

        // Handle button clicks
        binding.letsGoButton.setOnClickListener {
            // Navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.registerButton.setOnClickListener {
            // Navigate to RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            binding.descriptionText.breakStrategy = LineBreaker.BREAK_STRATEGY_SIMPLE
            binding.descriptionText.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // API 26 to 32
            binding.descriptionText.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }
    }
}
