package com.example.frontend_moodify.presentation.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class PieChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Warna khusus untuk setiap mood
    private val colors = listOf(
        Color.parseColor("#FFD700"), // Happiness - Kuning cerah
        Color.parseColor("#1E90FF"), // Sadness - Biru sedang
        Color.parseColor("#FF4500"), // Anger - Merah terang
        Color.parseColor("#FFA500"), // Enthusiasm - Oranye terang
        Color.parseColor("#2F4F4F")  // Worry - Abu-abu kebiruan
    )

    // Persentase default
    private var percentages: List<Float> = listOf(0f, 0f, 0f, 0f, 0f)

    /**
     * Fungsi untuk mengupdate data persentase dari luar
     */
    fun setPercentages(newPercentages: List<Float>) {
        percentages = newPercentages
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val size = min(width, height).toFloat()
        val radius = size / 2
        val centerX = width / 2f
        val centerY = height / 2f
        var startAngle = 0f

        // Periksa jika semua persentase nol
        val totalPercentage = percentages.sum()
        if (totalPercentage == 0f) {
            // Gambar teks jika semua persentase nol
            paint.color = Color.GRAY
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = radius / 4
            canvas.drawText(
                "No Data Available",
                centerX, centerY + (paint.textSize / 4), paint
            )
            return
        }

        // Gambar pie chart
        percentages.forEachIndexed { index, percentage ->
            if (percentage > 0) {
                paint.color = colors[index]
                val sweepAngle = percentage * 360
                canvas.drawArc(
                    centerX - radius, centerY - radius,
                    centerX + radius, centerY + radius,
                    startAngle, sweepAngle, true, paint
                )
                startAngle += sweepAngle
            }
        }
    }
}
