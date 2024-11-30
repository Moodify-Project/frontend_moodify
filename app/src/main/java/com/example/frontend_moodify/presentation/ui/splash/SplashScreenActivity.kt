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

        // Animasi untuk pergerakan logo (bergerak ke atas)
        val moveAnimator = ObjectAnimator.ofFloat(logo, "translationY", -500f, 0f)
        moveAnimator.duration = 800  // Durasi animasi gerak logo
        moveAnimator.interpolator = AccelerateDecelerateInterpolator()  // Ease In Out
        moveAnimator.start()

        // Animasi untuk memudar logo
        val fadeOutAnimator = ObjectAnimator.ofFloat(logo, "alpha", 1f, 0f)
        fadeOutAnimator.duration = 300  // Durasi animasi memudar logo

        // Menambahkan listener untuk animasi pergerakan logo
        moveAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                // Mulai animasi fade out setelah pergerakan selesai
                fadeOutAnimator.start()

                // Setelah fade out selesai, pindah ke activity berikutnya
                fadeOutAnimator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        // Setelah animasi selesai, pindah ke LandingPageActivity
                        val intent = Intent(this@SplashScreenActivity, LandingPageActivity::class.java)
                        startActivity(intent)
                        finish()  // Pastikan activity splash selesai agar tidak bisa kembali
                    }
                })
            }
        })
    }
}
