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
    private val colors = listOf(Color.YELLOW, Color.MAGENTA, Color.RED, Color.BLUE)
    private val percentages = listOf(0.19f, 0.32f, 0.29f, 0.12f)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val size = min(width, height).toFloat()
        val radius = size / 2
        val centerX = width / 2f
        val centerY = height / 2f
        var startAngle = 0f

        percentages.forEachIndexed { index, percentage ->
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