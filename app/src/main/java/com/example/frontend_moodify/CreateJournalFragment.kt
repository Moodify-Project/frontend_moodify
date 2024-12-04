package com.example.frontend_moodify

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        // Observasi LiveData dari ViewModel
//        journalViewModel.journalContent.observe(viewLifecycleOwner) { content ->
//            binding.inputField.setText(content)
//        }
        journalViewModel.journalContent.observe(viewLifecycleOwner) { content ->
            if (content == "Journal is empty") {
                // Kosongkan input_field dan set hint
                binding.inputField.setText("")
                binding.inputField.hint = "Write your journal here..."
            } else {
                // Tampilkan konten Journal
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
            } else {
                Toast.makeText(requireContext(), "Content cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch journal untuk tanggal hari ini
        journalViewModel.fetchJournalByDate(formattedDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
