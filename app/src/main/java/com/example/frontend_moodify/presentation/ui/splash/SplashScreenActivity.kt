package com.example.frontend_moodify.presentation.ui.splash

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.frontend_moodify.MainActivity
import com.example.frontend_moodify.R

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val icLogoSmile = findViewById<ImageView>(R.id.ic_logo_smile)
        val icLogoNetral = findViewById<ImageView>(R.id.ic_logo_netral)
        val icLogoSad = findViewById<ImageView>(R.id.ic_logo_sad)

        val floatInSmile = ObjectAnimator.ofFloat(icLogoSmile, "translationY", 200f, 0f).apply {
            duration = 1000
        }

        floatInSmile.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                icLogoSmile.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator) {
                // Animasi berikutnya setelah float in selesai
                showSequentialLogos(icLogoSmile, icLogoNetral, icLogoSad)
            }
        })

        floatInSmile.start()
    }

    private fun showSequentialLogos(
        icLogoSmile: ImageView,
        icLogoNetral: ImageView,
        icLogoSad: ImageView
    ) {
        val distanceBetweenLogos = 150f
        val closerDistanceForSad = 130f // Jarak lebih dekat untuk ic_logo_sad

        val moveSmileLeft =
            ObjectAnimator.ofFloat(icLogoSmile, "translationX", 0f, -distanceBetweenLogos).apply {
                duration = 1000
            }

        val showNetral = ObjectAnimator.ofFloat(icLogoNetral, "alpha", 0f, 1f).apply {
            duration = 1000
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    icLogoNetral.visibility = View.VISIBLE
                }
            })
        }

        val moveAndCenterNetral = AnimatorSet().apply {
            playTogether(moveSmileLeft, showNetral)
        }

        val moveSmileFurtherLeft = ObjectAnimator.ofFloat(
            icLogoSmile,
            "translationX",
            -distanceBetweenLogos,
            -2 * distanceBetweenLogos
        ).apply {
            duration = 1000
        }

        val moveNetralLeft =
            ObjectAnimator.ofFloat(icLogoNetral, "translationX", 0f, -distanceBetweenLogos).apply {
                duration = 1000
            }

        val showSad = ObjectAnimator.ofFloat(icLogoSad, "alpha", 0f, 1f).apply {
            duration = 1000
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    icLogoSad.visibility = View.VISIBLE
                }
            })
        }

        val moveAndCenterSad = AnimatorSet().apply {
            playTogether(moveSmileFurtherLeft, moveNetralLeft, showSad)
        }

        // Langkah terakhir: kembalikan ikon ke posisi tengah secara horizontal, tetapi dengan jarak lebih dekat
        val arrangeLogos = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(
                    icLogoSmile,
                    "translationX",
                    -distanceBetweenLogos
                ), // Smile di kiri
                ObjectAnimator.ofFloat(
                    icLogoNetral,
                    "translationX",
                    0f
                ),                  // Netral di tengah
                ObjectAnimator.ofFloat(
                    icLogoSad,
                    "translationX",
                    closerDistanceForSad
                )   // Sad lebih dekat ke Netral
            )
            duration = 1000
        }

        val finalAnimationSet = AnimatorSet().apply {
            playSequentially(moveAndCenterNetral, moveAndCenterSad, arrangeLogos)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // Pindah ke MainActivity
                    val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Tutup SplashScreenActivity
                }
            })
        }

        finalAnimationSet.start()
    }
}