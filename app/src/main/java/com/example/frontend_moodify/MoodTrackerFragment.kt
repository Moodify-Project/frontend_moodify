package com.example.frontend_moodify

import android.content.res.Configuration
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.util.Log
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
    private var selectedWeekStart: Calendar? = null
//    private var selectedDate: Calendar = Calendar.getInstance()
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
        selectedWeekStart = getWeekStart(Calendar.getInstance())

        // Inisialisasi adapter dan RecyclerView
        dateAdapter = DateAdapter(allDates, isWeeklyMode = false) { selected ->
            selectedWeekStart = getWeekStart(selected)
            updateDateRangeText()
            updateMoodData(selected)
        }
        setRecyclerViewLayoutManager()
        binding.rvDateSelector.adapter = dateAdapter

        // Scroll ke hari ini
        scrollToToday()
        updateDateRangeText()

        // Observe data dari ViewModel
        observeViewModel()


        // Tombol navigasi hari sebelumnya dan berikutnya
        binding.btnLeft.setOnClickListener { navigateWeek(-1) }
        binding.btnRight.setOnClickListener { navigateWeek(1) }

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
                val happinessPercentage = (moods.happiness * 100) / total
                val sadnessPercentage = (moods.sadness * 100) / total
                val angerPercentage = (moods.anger * 100) / total
                val enthusiasmPercentage = (moods.enthusiasm * 100) / total
                val worryPercentage = (moods.worry * 100) / total

                // Tampilkan persentase
                binding.happyPercentage.text = String.format("%.2f%%", happinessPercentage)
                binding.sadPercentage.text = String.format("%.2f%%", sadnessPercentage)
                binding.angryPercentage.text = String.format("%.2f%%", angerPercentage)
                binding.enthusiasticPercentage.text = String.format("%.2f%%", enthusiasmPercentage)
                binding.worryPercentage.text = String.format("%.2f%%", worryPercentage)

                val conclusion = generateMoodConclusion(
                    happinessPercentage, sadnessPercentage, angerPercentage, enthusiasmPercentage, worryPercentage
                )
                Log.d("MoodTrackerFragment", "conclusion: $conclusion")
                binding.tvJournalText.text = conclusion
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

    private fun generateMoodConclusion(
        happiness: Float,
        sadness: Float,
        anger: Float,
        enthusiasm: Float,
        worry: Float
    ): String {
        fun getLevel(value: Float, ranges: List<Pair<Int, Int>>): String {
            val intValue = value.toInt() // Konversi Float ke Int
            return when {
                intValue in ranges[0].first..ranges[0].second -> "Sangat Rendah"
                intValue in ranges[1].first..ranges[1].second -> "Rendah"
                intValue in ranges[2].first..ranges[2].second -> "Sedang"
                intValue in ranges[3].first..ranges[3].second -> "Tinggi"
                else -> "Sangat Tinggi"
            }
        }

        val happinessLevel = getLevel(happiness, listOf(0 to 20, 21 to 40, 41 to 60, 61 to 80, 81 to 100))
        val sadnessLevel = getLevel(sadness, listOf(0 to 10, 11 to 30, 31 to 50, 51 to 70, 71 to 100))
        val angerLevel = getLevel(anger, listOf(0 to 5, 6 to 20, 21 to 40, 41 to 70, 71 to 100))
        val enthusiasmLevel = getLevel(enthusiasm, listOf(0 to 20, 21 to 40, 41 to 60, 61 to 80, 81 to 100))
        val worryLevel = getLevel(worry, listOf(0 to 10, 11 to 30, 31 to 50, 51 to 70, 71 to 100))

        return """
        Berdasarkan data yang telah dikumpulkan selama seminggu:
        - Kebahagiaan Anda berada di level: $happinessLevel
        - Kesedihan Anda berada di level: $sadnessLevel
        - Kemarahan Anda berada di level: $angerLevel
        - Antusiasme Anda berada di level: $enthusiasmLevel
        - Kekhawatiran Anda berada di level: $worryLevel

        Kesimpulan: Anda dapat memanfaatkan data ini untuk meningkatkan mood positif dan mengelola emosi negatif dengan lebih baik.
    """.trimIndent()
    }


    private fun updateMoodData(selectedDate: Calendar) {
        // Format tanggal
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDateString = dateFormat.format(selectedDate.time)

        Log.d("MoodTrackerFragment", "Selected Date: $selectedDateString")

        // Tampilkan tanggal di UI
//        binding.tvDate.text = dateFormat.format(selectedDate.time)

        // Fetch data mood berdasarkan tanggal
        viewModel.fetchMoods(selectedDateString)
    }

//    private fun navigateDay(step: Int) {
//        val currentIndex = allDates.indexOfFirst { isSameDay(it, selectedDate) }
//        val newIndex = (currentIndex + step).coerceIn(0, allDates.size - 1)
//
//        selectedDate = allDates[newIndex]
//        dateAdapter.setSelectedDate(selectedDate)
//
//        binding.rvDateSelector.scrollToPosition(newIndex)
//        updateMoodData()
//    }
    private fun navigateWeek(step: Int) {
        if (selectedWeekStart == null) {
            selectedWeekStart = Calendar.getInstance()
        }

        val currentIndex = allDates.indexOfFirst { isSameDay(it, selectedWeekStart!!) }
        val newIndex = (currentIndex + (step * 7)).coerceIn(0, allDates.size - 1)

        selectedWeekStart = getWeekStart(allDates[newIndex])
        updateDateRangeText()

        selectedWeekStart?.let {
            dateAdapter.setSelectedDate(it)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            Log.d("MoodTrackerFragment", "Navigated to Week Start: ${dateFormat.format(it.time)}")
        }

        binding.rvDateSelector.scrollToPosition(newIndex)
    }
    // Perbarui rentang tanggal di UI
    private fun updateDateRangeText() {
        val endOfWeek = selectedWeekStart?.clone() as Calendar
        endOfWeek.add(Calendar.DAY_OF_WEEK, 6)
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        binding.tvDate.text = "${dateFormat.format(selectedWeekStart?.time)} - ${dateFormat.format(endOfWeek.time)}"
    }
    private fun getWeekStart(date: Calendar): Calendar {
        val start = date.clone() as Calendar
        start.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        if (start.after(date)) {
            // Jika awal minggu lebih besar dari tanggal saat ini,
            // artinya tanggal saat ini termasuk minggu sebelumnya.
            start.add(Calendar.DAY_OF_MONTH, -7)
        }
        return start
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

        selectedWeekStart = getWeekStart(Calendar.getInstance())
        selectedWeekStart?.let {
            dateAdapter.setSelectedDate(it)
        }
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
