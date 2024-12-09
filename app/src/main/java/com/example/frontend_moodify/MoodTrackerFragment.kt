package com.example.frontend_moodify

import android.content.res.Configuration
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frontend_moodify.data.remote.network.Injection
import com.example.frontend_moodify.databinding.FragmentMoodTrackBinding
import com.example.frontend_moodify.presentation.adapter.DateAdapter
import com.example.frontend_moodify.presentation.customview.PieChartView
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

        val sessionManager = SessionManager(requireContext())
        val repository = Injection.provideJournalRepository(sessionManager)
        val factory = MoodViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MoodViewModel::class.java]

        allDates = generateDates()
        selectedWeekStart = getWeekStart(Calendar.getInstance())

        dateAdapter = DateAdapter(allDates, isWeeklyMode = false) { selected ->
            selectedWeekStart = getWeekStart(selected)
            updateDateRangeText()
            updateMoodData(selected)
        }
        setRecyclerViewLayoutManager()
        binding.rvDateSelector.adapter = dateAdapter

        scrollToToday()
        updateDateRangeText()
        updateMoodData()

        animateHeader()
        animateRecyclerView()
        animateJournalContent()

        observeViewModel()

        binding.btnLeft.setOnClickListener { navigateWeek(-1) }
        binding.btnRight.setOnClickListener { navigateWeek(1) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.tvJournalText.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }
    }

    private fun observeViewModel() {
        viewModel.moods.observe(viewLifecycleOwner) { moods ->
            val total = moods.anger + moods.enthusiasm + moods.happiness + moods.sadness + moods.worry
            if (total > 0) {
                val moodPercentages = listOf(
                    moods.happiness.toFloat() / total,
                    moods.sadness.toFloat() / total,
                    moods.anger.toFloat() / total,
                    moods.enthusiasm.toFloat() / total,
                    moods.worry.toFloat() / total
                )

                animatePieChart(moodPercentages)

                // Tampilkan persentase dengan animasi juga
                animatePercentageText(binding.happyPercentage, moodPercentages[0])
                animatePercentageText(binding.sadPercentage, moodPercentages[1])
                animatePercentageText(binding.angryPercentage, moodPercentages[2])
                animatePercentageText(binding.enthusiasticPercentage, moodPercentages[3])
                animatePercentageText(binding.worryPercentage, moodPercentages[4])

                // Conclusion
                val conclusion = generateMoodConclusion(
                    moodPercentages[0] * 100, moodPercentages[1] * 100,
                    moodPercentages[2] * 100, moodPercentages[3] * 100, moodPercentages[4] * 100
                )
                binding.tvJournalText.text = conclusion
            } else {
                binding.happyPercentage.text = "0%"
                binding.sadPercentage.text = "0%"
                binding.angryPercentage.text = "0%"
                binding.enthusiasticPercentage.text = "0%"
                binding.worryPercentage.text = "0%"

                binding.tvJournalText.text = "Data belum tersedia."
                binding.pieChart.setPercentages(List(5) { 0f })
            }
        }

        // Observasi error
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            binding.tvError?.visibility = if (error.isNotEmpty()) View.VISIBLE else View.GONE
            binding.tvError?.text = error
        }

    }

    private fun generateMoodConclusion(
        happiness: Float,
        sadness: Float,
        anger: Float,
        enthusiasm: Float,
        worry: Float
    ): String {
        fun getLevel(value: Float, ranges: List<Pair<Int, Int>>): String {
            val intValue = value.toInt()
            return when {
                intValue in ranges[0].first..ranges[0].second -> "Very Low"
                intValue in ranges[1].first..ranges[1].second -> "Low"
                intValue in ranges[2].first..ranges[2].second -> "Moderate"
                intValue in ranges[3].first..ranges[3].second -> "High"
                else -> "Very High"
            }
        }

        val happinessLevel = getLevel(happiness, listOf(0 to 20, 21 to 40, 41 to 60, 61 to 80, 81 to 100))
        val sadnessLevel = getLevel(sadness, listOf(0 to 10, 11 to 30, 31 to 50, 51 to 70, 71 to 100))
        val angerLevel = getLevel(anger, listOf(0 to 5, 6 to 20, 21 to 40, 41 to 70, 71 to 100))
        val enthusiasmLevel = getLevel(enthusiasm, listOf(0 to 20, 21 to 40, 41 to 60, 61 to 80, 81 to 100))
        val worryLevel = getLevel(worry, listOf(0 to 10, 11 to 30, 31 to 50, 51 to 70, 71 to 100))

        return """
        Over the past week, here is an overview of your emotions:
        - Your happiness level is: $happinessLevel
        - Your sadness level is: $sadnessLevel
        - Your anger level is: $angerLevel
        - Your enthusiasm level is: $enthusiasmLevel
        - Your worry level is: $worryLevel

        Use this information to continue strengthening your positive emotions and effectively managing the negative ones.
    """.trimIndent()
    }

    private fun updateMoodData(selectedDate: Calendar? = null) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateToFetch = selectedDate ?: selectedWeekStart ?: Calendar.getInstance()
        val dateString = dateFormat.format(dateToFetch.time)

        Log.d("MoodTrackerFragment", "Fetching Mood Data for Date: $dateString")
        viewModel.fetchMoods(dateString)
    }

    private fun animatePieChart(moodPercentages: List<Float>) {
        // Menambahkan animasi untuk PieChart secara bertahap
        val chart = binding.pieChart
        val startPercentages = List(5) { 0f } // Mulai dari 0%

        // Set initial data as 0% and animate to actual percentages
        chart.setPercentages(startPercentages)

        // Membuat animasi untuk PieChart agar mengisi dari 0% ke nilai yang ada
        val animator = ObjectAnimator.ofObject(
            chart,
            "percentages", // properti yang ingin dianimasikan
            TypeEvaluator<List<Float>> { fraction, start, end ->
                start.mapIndexed { index, startValue ->
                    startValue + (end[index] - startValue) * fraction
                }
            },
            startPercentages,
            moodPercentages
        ).apply {
            duration = 1000  // Durasi animasi (1 detik)
        }
        animator.start()
    }

    private fun animatePercentageText(textView: TextView, percentage: Float) {
        val animatedPercentage = ValueAnimator.ofFloat(0f, percentage * 100).apply {
            duration = 1000 // Durasi animasi (1 detik)
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                textView.text = String.format("%.1f%%", value)
            }
        }
        animatedPercentage.start()
    }

    private fun navigateWeek(step: Int) {
        if (selectedWeekStart == null) {
            selectedWeekStart = Calendar.getInstance()
        }

        selectedWeekStart?.add(Calendar.WEEK_OF_YEAR, step)
        updateDateRangeText()
        updateMoodData(selectedWeekStart)
    }

    private fun updateDateRangeText() {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val startOfWeek = selectedWeekStart?.apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        val endOfWeek = selectedWeekStart?.apply {
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        }
        binding.tvDate.text = "${sdf.format(startOfWeek?.time)} - ${sdf.format(endOfWeek?.time)}"
    }

    private fun setRecyclerViewLayoutManager() {
        binding.rvDateSelector.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun scrollToToday() {
        val today = Calendar.getInstance()
        val position = allDates.indexOfFirst { it.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) }
        if (position != -1) {
            binding.rvDateSelector.scrollToPosition(position)
        }
    }

    private fun getWeekStart(date: Calendar): Calendar {
        val cal = Calendar.getInstance()
        cal.time = date.time
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return cal
    }

    private fun generateDates(): List<Calendar> {
        val dates = mutableListOf<Calendar>()
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        while (calendar.get(Calendar.MONTH) == currentMonth) {
            dates.add(calendar.clone() as Calendar)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return dates
    }

    private fun animateHeader() {
        ObjectAnimator.ofFloat(binding.tvDate, "translationY", -100f, 0f).apply {
            duration = 500
        }.start()

        ObjectAnimator.ofFloat(binding.btnLeft, "translationX", -100f, 0f).apply {
            duration = 500
        }.start()

        ObjectAnimator.ofFloat(binding.btnRight, "translationX", 100f, 0f).apply {
            duration = 500
        }.start()
    }

    private fun animateRecyclerView() {
        ObjectAnimator.ofFloat(binding.rvDateSelector, "alpha", 0f, 1f).apply {
            duration = 500
        }.start()
    }

    private fun animateJournalContent() {
        ObjectAnimator.ofFloat(binding.tvJournalText, "alpha", 0f, 1f).apply {
            duration = 500
        }.start()

        ObjectAnimator.ofFloat(binding.happyPercentage, "alpha", 0f, 1f).apply {
            duration = 500
        }.start()

        ObjectAnimator.ofFloat(binding.sadPercentage, "alpha", 0f, 1f).apply {
            duration = 500
        }.start()

        ObjectAnimator.ofFloat(binding.angryPercentage, "alpha", 0f, 1f).apply {
            duration = 500
        }.start()

        ObjectAnimator.ofFloat(binding.enthusiasticPercentage, "alpha", 0f, 1f).apply {
            duration = 500
        }.start()

        ObjectAnimator.ofFloat(binding.worryPercentage, "alpha", 0f, 1f).apply {
            duration = 500
        }.start()
    }
}
