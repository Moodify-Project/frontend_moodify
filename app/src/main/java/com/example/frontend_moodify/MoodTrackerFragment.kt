package com.example.frontend_moodify

import android.content.res.Configuration
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frontend_moodify.data.remote.network.Injection
import com.example.frontend_moodify.databinding.FragmentMoodTrackBinding
import com.example.frontend_moodify.presentation.adapter.DateAdapter
import com.example.frontend_moodify.presentation.viewmodel.MoodViewModel
import com.example.frontend_moodify.presentation.viewmodel.MoodViewModelFactory
import com.example.frontend_moodify.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.*

class MoodTrackerFragment : Fragment() {

    private var _binding: FragmentMoodTrackBinding? = null
    private val binding get() = _binding!!

    private lateinit var dateAdapter: DateAdapter
    private var allDates: List<Calendar> = emptyList()
    private var selectedDate: Calendar = Calendar.getInstance()
    private lateinit var viewModel: MoodViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ViewModel
        val sessionManager = SessionManager(requireContext())
        val repository = Injection.provideJournalRepository(sessionManager)
        val factory = MoodViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MoodViewModel::class.java]

        // Generate daftar tanggal untuk dua tahun terakhir
        allDates = generateDates()

        // Inisialisasi adapter dan RecyclerView
        dateAdapter = DateAdapter(allDates, isWeeklyMode = false) { selected ->
            selectedDate = selected
            updateMoodData()
        }
        setRecyclerViewLayoutManager()
        binding.rvDateSelector.adapter = dateAdapter

        // Scroll ke hari ini
        scrollToToday()

        // Observe data dari ViewModel
        observeViewModel()

        // Tombol navigasi hari sebelumnya dan berikutnya
        binding.btnLeft.setOnClickListener { navigateDay(-1) }
        binding.btnRight.setOnClickListener { navigateDay(1) }

        // Justifikasi teks jika Android mendukung
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.tvJournalText.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }
    }

    private fun observeViewModel() {
        // Observasi data mood
        viewModel.moods.observe(viewLifecycleOwner) { moods ->
            val total = moods.anger + moods.enthusiasm + moods.happiness + moods.sadness + moods.worry
            if (total > 0) {
                binding.happyPercentage.text = "${(moods.happiness * 100) / total}%"
                binding.sadPercentage.text = "${(moods.sadness * 100) / total}%"
                binding.angryPercentage.text = "${(moods.anger * 100) / total}%"
                binding.enthusiasticPercentage.text = "${(moods.enthusiasm * 100) / total}%"
                binding.worryPercentage.text = "${(moods.worry * 100) / total}%"
            } else {
                binding.happyPercentage.text = "0%"
                binding.sadPercentage.text = "0%"
                binding.angryPercentage.text = "0%"
                binding.enthusiasticPercentage.text = "0%"
                binding.worryPercentage.text = "0%"
            }
        }

        // Observasi error
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            binding.tvError?.visibility = if (error.isNotEmpty()) View.VISIBLE else View.GONE
            binding.tvError?.text = error
        }

        // Observasi loading state
//        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
//            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//        }
    }

    private fun updateMoodData() {
        // Format tanggal
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDateString = dateFormat.format(selectedDate.time)

        // Tampilkan tanggal di UI
        binding.tvDate.text = dateFormat.format(selectedDate.time)

        // Fetch data mood berdasarkan tanggal
        viewModel.fetchMoods(selectedDateString)
    }

    private fun navigateDay(step: Int) {
        val currentIndex = allDates.indexOfFirst { isSameDay(it, selectedDate) }
        val newIndex = (currentIndex + step).coerceIn(0, allDates.size - 1)

        selectedDate = allDates[newIndex]
        dateAdapter.setSelectedDate(selectedDate)

        binding.rvDateSelector.scrollToPosition(newIndex)
        updateMoodData()
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

    private fun scrollToToday() {
        val todayIndex = allDates.indexOfFirst { isSameDay(it, Calendar.getInstance()) }
        binding.rvDateSelector.scrollToPosition(todayIndex)
        selectedDate = Calendar.getInstance()
        dateAdapter.setSelectedDate(selectedDate)
        updateMoodData()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
