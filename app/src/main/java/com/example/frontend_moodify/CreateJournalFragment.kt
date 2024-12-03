package com.example.frontend_moodify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.frontend_moodify.databinding.FragmentCreateJournalBinding
import java.text.SimpleDateFormat
import java.util.*

class CreateJournalFragment : Fragment() {
    private var _binding: FragmentCreateJournalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateJournalBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Format tanggal dan waktu saat ini
        val currentDateTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDateTime)

        // Set text pada text_date
        binding.textDate.text = formattedDate
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}