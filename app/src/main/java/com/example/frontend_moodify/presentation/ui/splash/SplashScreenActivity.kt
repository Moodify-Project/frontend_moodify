package com.example.frontend_moodify.presentation.ui.splash

import android.animation.ObjectAnimator
import android.animation.AnimatorListenerAdapter
import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.frontend_moodify.R
import com.example.frontend_moodify.presentation.ui.splash.LandingPageActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val logo = findViewById<ImageView>(R.id.logo)

        val moveAnimator = ObjectAnimator.ofFloat(logo, "translationY", -500f, 0f)
        moveAnimator.duration = 800
        moveAnimator.interpolator = AccelerateDecelerateInterpolator()
        moveAnimator.start()

        val fadeOutAnimator = ObjectAnimator.ofFloat(logo, "alpha", 1f, 0f)
        fadeOutAnimator.duration = 300

        moveAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                fadeOutAnimator.start()

                fadeOutAnimator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        val intent = Intent(this@SplashScreenActivity, LandingPageActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                })
            }
        })
    }
}
