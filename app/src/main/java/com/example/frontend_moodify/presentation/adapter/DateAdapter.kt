package com.example.frontend_moodify.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.frontend_moodify.R
import com.example.frontend_moodify.databinding.ItemDateBinding
import java.text.SimpleDateFormat
import java.util.*

class DateAdapter(
    private val dates: List<Calendar>,
    private val isWeeklyMode: Boolean,
    private val onDateSelected: (Calendar) -> Unit
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    private var selectedPosition = -1
    private val today = Calendar.getInstance()

    // Set date selection, including preventing changes for today's date
    fun setSelectedDate(date: Calendar) {
        val index = dates.indexOfFirst { isSameDay(it, date) }
        if (index >= 0) {
            selectedPosition = index
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding = ItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val calendar = dates[position]
        // Format day and date
        val dayFormat = SimpleDateFormat("E", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())

        holder.binding.tvDay.text = dayFormat.format(calendar.time).substring(0, 1)
        holder.binding.tvDate.text = dateFormat.format(calendar.time)

        val dateTextColor = ContextCompat.getColor(holder.binding.root.context, R.color.dateTextColor)

        if (isSameDay(calendar, today)) {
            holder.binding.tvDate.setTextColor(Color.WHITE)
            holder.binding.tvDay.setTextColor(Color.WHITE)
            holder.binding.root.setBackgroundResource(R.drawable.bg_selected_date)
            if (selectedPosition == position) {
                holder.binding.root.setBackgroundResource(R.drawable.bg_selected_date)
            }
        } else {
            if (holder.adapterPosition == selectedPosition) {
                holder.binding.tvDate.setTextColor(Color.WHITE)
                holder.binding.tvDay.setTextColor(Color.WHITE)
                holder.binding.root.setBackgroundResource(R.drawable.bg_calendar_day_active)
            } else {
                holder.binding.tvDate.setTextColor(dateTextColor)
                holder.binding.tvDay.setTextColor(
                    ContextCompat.getColor(holder.binding.root.context, R.color.grey)
                )
                holder.binding.root.setBackgroundResource(android.R.color.transparent)
            }
        }

        holder.binding.root.setOnClickListener {
//            if (!isSameDay(calendar, today)) {
                selectedPosition = holder.adapterPosition
                notifyDataSetChanged()
                onDateSelected(calendar)
//            }
        }
    }

    override fun getItemCount(): Int = dates.size

    class DateViewHolder(val binding: ItemDateBinding) : RecyclerView.ViewHolder(binding.root)

    private fun isSameDay(date1: Calendar, date2: Calendar): Boolean {
        return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
                date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR)
    }
}
