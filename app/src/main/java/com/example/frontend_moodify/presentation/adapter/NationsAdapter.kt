package com.example.frontend_moodify.presentation.adapter

import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter

class NationsAdapter(context: Context, private var nations: List<String>) :
    ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, nations) {

    fun updateNations(newNations: List<String>) {
        Log.d("NationsAdapter", "Updating nations with size: ${newNations.size}")
        nations = newNations
        clear()
        addAll(newNations)
        notifyDataSetChanged()
    }
}
