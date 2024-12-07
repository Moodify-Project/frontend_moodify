//package com.example.frontend_moodify
//
//import android.content.res.Configuration
//import android.graphics.text.LineBreaker
//import android.os.Build
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModelProvider
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.frontend_moodify.data.remote.network.Injection
//import com.example.frontend_moodify.databinding.FragmentMoodTrackBinding
//import com.example.frontend_moodify.presentation.adapter.DateAdapter
//import com.example.frontend_moodify.presentation.viewmodel.MoodViewModel
//import com.example.frontend_moodify.presentation.viewmodel.MoodViewModelFactory
//import com.example.frontend_moodify.utils.SessionManager
//import java.text.SimpleDateFormat
//import java.util.*
//
//class MoodTrackerFragment : Fragment() {
//    private var _binding: FragmentMoodTrackBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var dateAdapter: DateAdapter
//    private var allDates: List<Calendar> = emptyList()
//    private var selectedWeekStart: Calendar? = null
//    private lateinit var viewModel: MoodViewModel
//
//    // Map untuk menyimpan konten jurnal berdasarkan tanggal
//    private val journalContent = mutableMapOf<String, String>()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentMoodTrackBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val sessionManager = SessionManager(requireContext())
//        val repository = Injection.provideJournalRepository(sessionManager)
//        val factory = MoodViewModelFactory(repository)
//        viewModel = ViewModelProvider(this, factory)[MoodViewModel::class.java]
//
//
//        allDates = generateDates()
//        selectedWeekStart = getWeekStart(Calendar.getInstance()) // Default ke minggu ini
//
//        dateAdapter = DateAdapter(allDates, isWeeklyMode = true) { selected ->
//            selectedWeekStart = getWeekStart(selected)
//            updateDateRangeText()
//            updateJournalText(selected)
//        }
//        observeViewModel()
//
////        binding.rvDateSelector.layoutManager =
////            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        setRecyclerViewLayoutManager()
//        binding.rvDateSelector.adapter = dateAdapter
//
//        // Inisialisasi konten jurnal
//        initializeJournalContent()
//
//        // Scroll ke hari ini dan tampilkan jurnal hari ini
//        scrollToToday()
//        updateDateRangeText()
//        updateJournalText(Calendar.getInstance())
//
//        // Tombol navigasi mingguan
//        binding.btnLeft.setOnClickListener { navigateWeek(-1) }
//        binding.btnRight.setOnClickListener { navigateWeek(1) }
//
//        // Justifikasi teks untuk versi Android yang mendukung
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            binding.tvJournalText.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
//        }
//    }
//
//    private fun observeViewModel() {
//        // Mengamati data mood
//        viewModel.moods.observe(viewLifecycleOwner) { moods ->
//            val total = moods.anger + moods.enthusiasm + moods.happiness + moods.sadness + moods.worry
//            if (total > 0) {
//                binding.happyPercentage.text = "${(moods.happiness * 100) / total}%"
//                binding.sadPercentage.text = "${(moods.sadness * 100) / total}%"
//                binding.angryPercentage.text = "${(moods.anger * 100) / total}%"
//                binding.enthusiasticPercentage.text = "${(moods.enthusiasm * 100) / total}%"
//                binding.worryPercentage.text = "${(moods.worry * 100) / total}%"
//            } else {
//                binding.happyPercentage.text = "0%"
//                binding.sadPercentage.text = "0%"
//                binding.angryPercentage.text = "0%"
//                binding.enthusiasticPercentage.text = "0%"
//                binding.worryPercentage.text = "0%"
//            }
//        }
//
//        // Mengamati error message
//        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
//            binding.tvError?.visibility = if (error.isNotEmpty()) View.VISIBLE else View.GONE
//            binding.tvError?.text = error
//        }
//
//        // Mengamati loading state
////        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
////            binding.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
////        }
//    }
//
//    private fun setRecyclerViewLayoutManager() {
//        val orientation = resources.configuration.orientation
//        val layoutManager = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            // Mode landscape: Scroll secara vertikal
//            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//        } else {
//            // Mode portrait: Scroll secara horizontal
//            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        }
//        binding.rvDateSelector.layoutManager = layoutManager
//    }
//
//    // Navigasi minggu ke kiri atau kanan
//    private fun navigateWeek(step: Int) {
//        if (selectedWeekStart == null) {
//            selectedWeekStart = Calendar.getInstance()
//        }
//
//        val currentIndex = allDates.indexOfFirst { isSameDay(it, selectedWeekStart!!) }
//        val newIndex = (currentIndex + (step * 7)).coerceIn(0, allDates.size - 1)
//
//        selectedWeekStart = getWeekStart(allDates[newIndex])
//        updateDateRangeText()
//
//        selectedWeekStart?.let {
//            dateAdapter.setSelectedDate(it)
//        }
//
//        binding.rvDateSelector.scrollToPosition(newIndex)
//    }
//
//
//    // Perbarui rentang tanggal di UI
//    private fun updateDateRangeText() {
//        val endOfWeek = selectedWeekStart?.clone() as Calendar
//        endOfWeek.add(Calendar.DAY_OF_WEEK, 6)
//        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
//        binding.tvDate.text = "${dateFormat.format(selectedWeekStart?.time)} - ${dateFormat.format(endOfWeek.time)}"
//    }
//
//    // Perbarui teks jurnal berdasarkan tanggal
//    private fun updateJournalText(selectedDate: Calendar) {
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val selectedDateString = dateFormat.format(selectedDate.time)
//
//        val content = journalContent[selectedDateString] ?: "Tidak ada jurnal untuk tanggal ini."
//        binding.tvJournalText.text = content
//    }
//
//    // Scroll ke hari ini dan pilih
//    private fun scrollToToday() {
//        val todayIndex = allDates.indexOfFirst { isSameDay(it, Calendar.getInstance()) }
//        binding.rvDateSelector.scrollToPosition(todayIndex)
//
//        selectedWeekStart = getWeekStart(Calendar.getInstance())
//        selectedWeekStart?.let {
//            dateAdapter.setSelectedDate(it)
//        }
//    }
//
//    // Dapatkan awal minggu dari tanggal tertentu
//    private fun getWeekStart(date: Calendar): Calendar {
//        val start = date.clone() as Calendar
//        start.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
//        if (start.after(date)) {
//            // Jika awal minggu lebih besar dari tanggal saat ini,
//            // artinya tanggal saat ini termasuk minggu sebelumnya.
//            start.add(Calendar.DAY_OF_MONTH, -7)
//        }
//        return start
//    }
//
//    // Generate daftar tanggal selama dua tahun
//    private fun generateDates(): List<Calendar> {
//        val calendar = Calendar.getInstance()
//        calendar.add(Calendar.DAY_OF_MONTH, -365)
//        val dates = mutableListOf<Calendar>()
//
//        for (i in 0 until 730) {
//            dates.add(calendar.clone() as Calendar)
//            calendar.add(Calendar.DAY_OF_MONTH, 1)
//        }
//        return dates
//    }
//
//    // Bandingkan apakah dua tanggal berada di hari yang sama
//    private fun isSameDay(date1: Calendar, date2: Calendar): Boolean {
//        return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
//                date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR)
//    }
//
//    // Inisialisasi konten jurnal
//    private fun initializeJournalContent() {
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val today = Calendar.getInstance()
//
//        // Contoh data
//        journalContent[dateFormat.format(today.time)] = "Hari ini adalah hari yang luar biasa!"
//        today.add(Calendar.DAY_OF_MONTH, -1)
//        journalContent[dateFormat.format(today.time)] = "Kemarin cukup sibuk, tapi berhasil menyelesaikan banyak tugas."
//
//        today.add(Calendar.DAY_OF_MONTH, -1)
//        journalContent[dateFormat.format(today.time)] = "Dua hari yang lalu penuh tantangan, tetapi berhasil teratasi."
//    }
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//}
