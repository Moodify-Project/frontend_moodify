package com.example.frontend_moodify.presentation.ui.profile

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.frontend_moodify.R
import com.example.frontend_moodify.presentation.adapter.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var nationSpinner: Spinner
    private val viewModel: ProfileViewModel by viewModels()

    private val nationsAdapter by lazy {
        ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mutableListOf())
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        nationSpinner = findViewById(R.id.nationSpinner)
        nationSpinner.adapter = nationsAdapter

        // Observe ViewModel LiveData
        viewModel.nations.observe(this, Observer { newNations ->
            nationsAdapter.addAll(newNations)
            nationsAdapter.notifyDataSetChanged()
        })

        viewModel.error.observe(this, Observer { errorMsg ->
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
        })

        // Set spinner scroll listener for lazy loading
        nationSpinner.setOnScrollChangeListener { _, _, _, _, _ ->
            if (!nationSpinner.canScrollVertically(1)) {
                viewModel.loadMoreNations()
            }
        }

        // Fetch initial data
        viewModel.fetchNations()
    }
}
