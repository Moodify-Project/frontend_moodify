package com.example.frontend_moodify

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.frontend_moodify.data.remote.network.Injection
import com.example.frontend_moodify.databinding.FragmentCreateJournalBinding
import com.example.frontend_moodify.presentation.viewmodel.JournalViewModel
import com.example.frontend_moodify.presentation.viewmodel.JournalViewModelFactory
import com.example.frontend_moodify.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.*

class CreateJournalFragment : Fragment() {
    private var _binding: FragmentCreateJournalBinding? = null
    private val binding get() = _binding!!

    private val journalViewModel: JournalViewModel by viewModels {
        val sessionManager = SessionManager(requireContext())
        val repository = Injection.provideJournalRepository(sessionManager)
        JournalViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateJournalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Format tanggal hari ini
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)

        // Set tanggal pada text_date
        val displayDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        binding.textDate.text = displayDateFormat.format(currentDate)

        // Fade-in animasi untuk text_date
        ObjectAnimator.ofFloat(binding.textDate, "alpha", 0f, 1f).apply {
            duration = 800
            start()
        }

        // Animasi untuk inputField dan textDone
        val inputFieldAnimator = ObjectAnimator.ofFloat(binding.inputField, "translationY", 300f, 0f)
        inputFieldAnimator.duration = 600

        val doneButtonAnimator = ObjectAnimator.ofFloat(binding.textDone, "translationY", 300f, 0f)
        doneButtonAnimator.duration = 600

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(inputFieldAnimator, doneButtonAnimator)
        animatorSet.start()

        // Observasi LiveData dari ViewModel
        journalViewModel.journalContent.observe(viewLifecycleOwner) { content ->
            if (content == "Journal is empty") {
                binding.inputField.setText("")
                binding.inputField.hint = "Write your journal here..."
            } else {
                binding.inputField.setText(content)
            }
        }

        journalViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                Log.e("CreateJournalFragment", "Error: $error")
            }
        }

        journalViewModel.successMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                Log.d("CreateJournalFragment", "Success: $message")
            }
        }

        binding.textDone.setOnClickListener {
            val content = binding.inputField.text.toString()
            if (content.isNotEmpty()) {
                journalViewModel.saveOrUpdateJournal(formattedDate, content)

                // Animasi tombol Done saat diklik
                binding.textDone.animate().apply {
                    duration = 300
                    rotation(360f)
                    scaleX(1.2f)
                    scaleY(1.2f)
                    withEndAction {
                        binding.textDone.animate().scaleX(1f).scaleY(1f).duration = 200
                    }
                }.start()
            } else {
                Toast.makeText(requireContext(), "Content cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch jurnal untuk tanggal hari ini
        journalViewModel.fetchJournalByDate(formattedDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
