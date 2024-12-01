package com.example.frontend_moodify

import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frontend_moodify.databinding.FragmentMoodTrackBinding
import com.example.frontend_moodify.presentation.adapter.DateAdapter
import java.text.SimpleDateFormat
import java.util.*

class MoodTrackerFragment : Fragment() {
    private lateinit var binding: FragmentMoodTrackBinding
    private lateinit var dateAdapter: DateAdapter
    private var allDates: List<Calendar> = emptyList()
    private var selectedWeekStart: Calendar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoodTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allDates = generateDates()
        selectedWeekStart = getWeekStart(Calendar.getInstance()) // Default to current week

        dateAdapter = DateAdapter(allDates, isWeeklyMode = true) { selected ->
            selectedWeekStart = getWeekStart(selected)
            updateDateRangeText()
        }

        binding.rvDateSelector.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDateSelector.adapter = dateAdapter

        // Scroll to today's week and select
        scrollToToday()
        updateDateRangeText()

        binding.btnLeft.setOnClickListener { navigateWeek(-1) }
        binding.btnRight.setOnClickListener { navigateWeek(1) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.tvJournalText.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }

    }

    private fun navigateWeek(step: Int) {
        // Ensure selectedWeekStart is non-null before using it
        if (selectedWeekStart == null) {
            selectedWeekStart = Calendar.getInstance() // Default to current date if null
        }

        val currentIndex = allDates.indexOfFirst { isSameDay(it, selectedWeekStart!!) } // Non-nullable
        val newIndex = (currentIndex + (step * 7)).coerceIn(0, allDates.size - 1)

        // Update selectedWeekStart safely with non-null value
        selectedWeekStart = getWeekStart(allDates[newIndex])

        // Update date range text
        updateDateRangeText()

        // Ensure selectedWeekStart is non-null before passing it to the adapter
        selectedWeekStart?.let {
            dateAdapter.setSelectedDate(it)
        }

        binding.rvDateSelector.scrollToPosition(newIndex)
    }
    private fun updateDateRangeText() {
        val endOfWeek = selectedWeekStart?.clone() as Calendar
        endOfWeek.add(Calendar.DAY_OF_WEEK, 6)
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        binding.tvDate.text = "${dateFormat.format(selectedWeekStart?.time)} - ${dateFormat.format(endOfWeek.time)}"
    }

    private fun scrollToToday() {
        val todayIndex = allDates.indexOfFirst { isSameDay(it, Calendar.getInstance()) }
        binding.rvDateSelector.scrollToPosition(todayIndex)

        // Ensure selectedWeekStart is non-null before passing to the adapter
        selectedWeekStart = Calendar.getInstance()
        selectedWeekStart?.let {
            dateAdapter.setSelectedDate(it)
        }
    }

    private fun getWeekStart(date: Calendar): Calendar {
        val start = date.clone() as Calendar
        start.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return start
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
