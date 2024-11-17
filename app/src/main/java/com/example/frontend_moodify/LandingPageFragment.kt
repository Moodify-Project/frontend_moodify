package com.example.frontend_moodify


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class LandingPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Gunakan layout XML untuk fragment
        return inflater.inflate(R.layout.fragment_landing_page, container, false)
    }
}
