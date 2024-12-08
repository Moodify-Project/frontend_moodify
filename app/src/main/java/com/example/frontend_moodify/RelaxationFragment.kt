package com.example.frontend_moodify

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.frontend_moodify.databinding.FragmentRelaxationBinding
import com.google.android.material.snackbar.Snackbar
import kotlin.concurrent.timer

class RelaxationFragment : Fragment() {
    private var _binding: FragmentRelaxationBinding? = null
    private val binding get() = _binding!!

    private var isRunning = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var mediaPlayer: MediaPlayer
    private var currentStep = 0
    private var remainingTime = 0

    private val steps = listOf(
        Step("Inhale deeply and slowly", 6),
        Step("Hold your breath", 4),
        Step("Exhale slowly", 8)
    )

    private var songPlayer: MediaPlayer? = null
    private val updateSongProgressHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRelaxationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Animasi fade-in untuk tvTitle dan elemen lainnya ketika pertama kali tampilan fragment dimuat
        animateTextAppearance(binding.tvTitle)
        animateTextAppearance(binding.tvTimer)
        animateTextAppearance(binding.tvDurationStart)
        animateTextAppearance(binding.tvDurationEnd)

        mediaPlayer = MediaPlayer()

        setupSongPlayer()

        binding.btnPlay.setOnClickListener {
            if (isRunning) {
                stopRelaxation()
            } else {
                startRelaxation()
            }
        }

        binding.imgInfo.setOnClickListener { view ->
            val snackbar = Snackbar.make(
                view,
                "GUIDELINE RELAXATION FEATURE \n 1. Inhale deeply, slowly, and mindfully (6s).\n2. Gently hold your breath (4s).\n 3. Exhale slowly, release all tension (8s).",
                Snackbar.LENGTH_INDEFINITE
            )
            snackbar.setAction("Tutup") { snackbar.dismiss() }
            snackbar.setAnchorView(R.id.imgInfo)

            val snackbarView = snackbar.view
            snackbarView.setPadding(16, 16, 16, 16)
            snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).apply {
                maxLines = 10
                textSize = 14f
            }

            // Fade in snackbar
            snackbarView.alpha = 0f
            snackbarView.animate().alpha(1f).setDuration(500).start()

            snackbar.show()

            Handler(Looper.getMainLooper()).postDelayed({
                snackbar.dismiss()
            }, 15000)
        }
    }

    private fun animateTextAppearance(view: View) {
        view.alpha = 0f  // Mulai dengan transparansi 0
        view.animate().alpha(1f).setDuration(1000).start() // Fade-in dalam 1 detik
    }

    private fun setupSongPlayer() {
        songPlayer = MediaPlayer.create(requireContext(), R.raw.song).apply {
            setOnPreparedListener {
                binding.seekBarSong.max = duration
                binding.tvDurationEnd.text = formatTime(duration)
            }
            setOnCompletionListener {
                start()
            }
        }

        binding.seekBarSong.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    songPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun startRelaxation() {
        isRunning = true
        binding.btnPlay.setImageResource(R.drawable.ic_pause)
        binding.tvTitle.text = "Bersiap..."
        animatePlayPauseButton(true)
        startCountdownWithAnimation(3)

        songPlayer?.start()
        updateSongProgressHandler.post(updateSongProgressRunnable)
    }

    private fun startCountdownWithAnimation(start: Int) {
        var countdown = start
        val countdownAnimator = ValueAnimator.ofInt(countdown, 0)
        countdownAnimator.duration = 3000 // 3 seconds for countdown
        countdownAnimator.addUpdateListener { valueAnimator ->
            binding.tvTimer.text = valueAnimator.animatedValue.toString()
        }
        countdownAnimator.start()

        handler.postDelayed({
            currentStep = 0
            runStepWithAnimation()
        }, 3000)
    }

    private fun stopRelaxation() {
        isRunning = false
        handler.removeCallbacksAndMessages(null)
        binding.btnPlay.setImageResource(R.drawable.ic_play)
        animatePlayPauseButton(false)
        binding.tvTitle.text = "Relaxasi Berhenti"
        binding.tvTimer.text = "0"

        songPlayer?.pause()
        updateSongProgressHandler.removeCallbacksAndMessages(null)
    }

    private fun runStepWithAnimation() {
        val step = steps[currentStep]
        binding.tvTitle.text = step.name
        binding.tvTitle.alpha = 0f // Start with transparent text
        binding.tvTitle.animate().alpha(1f).setDuration(500).start() // Fade-in text

        remainingTime = step.duration
        updateTimer()
    }

    private fun updateTimer() {
        if (remainingTime > 0) {
            binding.tvTimer.text = remainingTime.toString()
            remainingTime--
            handler.postDelayed({ updateTimer() }, 1000)
        } else {
            playStepSound()
            handler.postDelayed({
                currentStep = (currentStep + 1) % steps.size
                runStepWithAnimation()
            }, 1000)
        }
    }

    private fun playStepSound() {
        try {
            if (::mediaPlayer.isInitialized) {
                mediaPlayer.release()
            }

            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.step_sound)
            mediaPlayer.setOnCompletionListener {
                mediaPlayer.release()
            }
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val updateSongProgressRunnable = object : Runnable {
        override fun run() {
            songPlayer?.let {
                binding.seekBarSong.progress = it.currentPosition
                binding.tvDurationStart.text = formatTime(it.currentPosition)
                updateSongProgressHandler.postDelayed(this, 1000)
            }
        }
    }

    private fun animatePlayPauseButton(isPlaying: Boolean) {
        val animator = ObjectAnimator.ofFloat(binding.btnPlay, "rotation", if (isPlaying) 180f else 0f)
        animator.duration = 300 // Rotate button smoothly
        animator.start()
    }

    private fun formatTime(milliseconds: Int): String {
        val minutes = milliseconds / 1000 / 60
        val seconds = milliseconds / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopRelaxation()
        songPlayer?.release()
        _binding = null
    }

    data class Step(val name: String, val duration: Int)
}
