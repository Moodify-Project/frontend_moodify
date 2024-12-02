package com.example.frontend_moodify

import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frontend_moodify.data.remote.network.Injection
import com.example.frontend_moodify.databinding.FragmentJournalBinding
import com.example.frontend_moodify.presentation.adapter.DateAdapter
import com.example.frontend_moodify.presentation.viewmodel.JournalViewModel
import com.example.frontend_moodify.presentation.viewmodel.JournalViewModelFactory
import com.example.frontend_moodify.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.*

class JournalFragment : Fragment() {
    private lateinit var binding: FragmentJournalBinding
    private lateinit var dateAdapter: DateAdapter
    private lateinit var viewModel: JournalViewModel
    private var allDates: List<Calendar> = emptyList()
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJournalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        val repository = Injection.provideJournalRepository(sessionManager)
        val factory = JournalViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[JournalViewModel::class.java]

        allDates = generateDates()
        dateAdapter = DateAdapter(allDates, isWeeklyMode = false) { selected ->
            selectedDate = selected
            updateDateText(selected)
        }

        binding.rvDateSelector.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDateSelector.adapter = dateAdapter

        val today = Calendar.getInstance()
        selectedDate = today
        updateDateText(today)

        scrollToToday()
        observeViewModel()

        binding.btnLeft.setOnClickListener { navigateDate(-1) }
        binding.btnRight.setOnClickListener { navigateDate(1) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.tvJournalText.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }
    }

    private fun observeViewModel() {
        viewModel.journalContent.observe(viewLifecycleOwner) { content ->
            binding.tvJournalText.text = content
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateDateText(date: Calendar) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        viewModel.fetchJournalByDate(dateFormat.format(date.time))
        binding.tvDate.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date.time)
    }

    private fun navigateDate(step: Int) {
        val currentIndex = allDates.indexOfFirst { isSameDay(it, selectedDate) }
        val newIndex = (currentIndex + step).coerceIn(0, allDates.size - 1)
        selectedDate = allDates[newIndex]
        dateAdapter.setSelectedDate(selectedDate)
        updateDateText(selectedDate)
        binding.rvDateSelector.scrollToPosition(newIndex)
    }

    private fun scrollToToday() {
        val todayIndex = allDates.indexOfFirst { isSameDay(it, Calendar.getInstance()) }
        binding.rvDateSelector.scrollToPosition(todayIndex)
        dateAdapter.setSelectedDate(Calendar.getInstance())
    }

    private fun generateDates(): List<Calendar> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -365)
        val dates = mutableListOf<Calendar>()

        for (i in 0 until 730) {
            dates.add(calendar.clone() as Calendar)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return dates
    }

    private fun isSameDay(date1: Calendar, date2: Calendar): Boolean {
        return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
                date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR)
    }
}
