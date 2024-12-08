package com.example.frontend_moodify

import android.animation.ObjectAnimator
import android.content.res.Configuration
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
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
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
        setRecyclerViewLayoutManager()
        binding.rvDateSelector.adapter = dateAdapter

        val today = Calendar.getInstance()
        selectedDate = today
        updateDateText(today)

        scrollToToday()
        observeViewModel()

        binding.btnLeft.setOnClickListener { navigateDate(-1) }
        binding.btnRight.setOnClickListener { navigateDate(1) }

        binding.tvDate.setOnClickListener {
            showDatePicker()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.tvJournalText.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }

        // Start Animations
        animateHeader()
        animateRecyclerView()
        animateJournalContent()
    }

    private fun animateHeader() {
        // Animation for tvDate and buttons (slide-in from sides)
        val dateAnimator = ObjectAnimator.ofFloat(binding.tvDate, "translationY", 50f, 0f)
        dateAnimator.duration = 600
        dateAnimator.start()

        val leftButtonAnimator = ObjectAnimator.ofFloat(binding.btnLeft, "translationX", -200f, 0f)
        val rightButtonAnimator = ObjectAnimator.ofFloat(binding.btnRight, "translationX", 200f, 0f)
        leftButtonAnimator.duration = 500
        rightButtonAnimator.duration = 500
        leftButtonAnimator.start()
        rightButtonAnimator.start()
    }

    private fun animateRecyclerView() {
        // Fade-in animation for RecyclerView
        val recyclerViewAnimator = ObjectAnimator.ofFloat(binding.rvDateSelector, "alpha", 0f, 1f)
        recyclerViewAnimator.duration = 500
        recyclerViewAnimator.start()
    }

    private fun animateJournalContent() {
        // Slide-up and fade-in animation for journal content
        val journalContentAnimator = ObjectAnimator.ofFloat(binding.svJournalContent, "translationY", 200f, 0f)
        journalContentAnimator.duration = 600
        journalContentAnimator.start()

        // Fade-in animation for journal text
        val textAnimator = ObjectAnimator.ofFloat(binding.tvJournalText, "alpha", 0f, 1f)
        textAnimator.duration = 600
        textAnimator.start()
    }

    private fun setRecyclerViewLayoutManager() {
        val orientation = resources.configuration.orientation
        val layoutManager = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        } else {
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        binding.rvDateSelector.layoutManager = layoutManager
    }

    private fun observeViewModel() {
        viewModel.journalContent.observe(viewLifecycleOwner) { content ->
            binding?.let { safeBinding ->
                if (content.isEmpty() || content == "Journal is empty") {
                    safeBinding.emptyStateLayout?.root?.visibility = View.VISIBLE
                    safeBinding.svJournalContent.visibility = View.GONE
                } else {
                    safeBinding.emptyStateLayout?.root?.visibility = View.GONE
                    safeBinding.svJournalContent.visibility = View.VISIBLE
                    safeBinding.tvJournalText.text = content
                }
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePicker() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(selectedDate.timeInMillis)
                .setCalendarConstraints(
                    CalendarConstraints.Builder()
                        .setStart(allDates.first().timeInMillis)
                        .setEnd(allDates.last().timeInMillis)
                        .build()
                )
                .build()

        datePicker.addOnPositiveButtonClickListener { dateInMillis ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = dateInMillis
            selectedDate = calendar
            updateDateText(selectedDate)
            scrollToSelectedDate()
        }

        datePicker.show(requireActivity().supportFragmentManager, "DatePicker")
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

    private fun scrollToSelectedDate() {
        val selectedIndex = allDates.indexOfFirst { isSameDay(it, selectedDate) }
        binding.rvDateSelector.scrollToPosition(selectedIndex)
        dateAdapter.setSelectedDate(selectedDate)
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
