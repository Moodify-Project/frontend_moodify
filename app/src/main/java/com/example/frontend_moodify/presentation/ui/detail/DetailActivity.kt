package com.example.frontend_moodify.presentation.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.frontend_moodify.R
import com.example.frontend_moodify.databinding.ActivityDetailBinding
import com.example.frontend_moodify.databinding.ActivityMainBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null)
//                .setAnchorView(R.id.fab).show()
        }
    }
}
