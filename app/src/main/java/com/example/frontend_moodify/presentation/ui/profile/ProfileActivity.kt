package com.example.frontend_moodify.presentation.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.frontend_moodify.R
import com.example.frontend_moodify.data.remote.network.Injection
import com.example.frontend_moodify.data.remote.response.profile.Profile
import com.example.frontend_moodify.presentation.repository.ProfileRepository
import com.example.frontend_moodify.utils.SessionManager

class ProfileActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var profileRepository: ProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sessionManager = SessionManager(this)
        profileRepository = ProfileRepository(
            Injection.provideProfileApiService(sessionManager)
        )

        fetchUserProfile()

        // Menambahkan klik listener pada gambar profil
        val profileImage = findViewById<ImageView>(R.id.profileImage)
        profileImage.setOnClickListener {
            openGallery()
        }
    }

    private fun fetchUserProfile() {
        profileRepository.getProfile { profile ->
            profile?.result?.let { updateUI(it) }
        }
    }

    private fun updateUI(userProfile: Profile) {
        val profileImage = findViewById<ImageView>(R.id.profileImage)
        val nameInput = findViewById<TextView>(R.id.nameInput)
        val genderSpinner = findViewById<Spinner>(R.id.genderSpinner)
        val nationSpinner = findViewById<Spinner>(R.id.nationSpinner)

        // Update data
        nameInput.text = userProfile.name
        // Populate gender and nation spinners as needed
        // ...

        // Load profile image
        Glide.with(this)
            .load(userProfile.urlphoto)
            .placeholder(R.drawable.user_profile)
            .into(profileImage)

        fetchGenderData { genderList ->
            val genderAdapter = ArrayAdapter(
                this, android.R.layout.simple_spinner_item, genderList
            )
            genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genderSpinner.adapter = genderAdapter

            // Set selected value based on user profile
            userProfile.gender?.let {
                val position = genderList.indexOf(it)
                if (position >= 0) genderSpinner.setSelection(position)
            }
        }

    }
    private fun fetchGenderData(callback: (List<String>) -> Unit) {
        // Simulasi data gender dari API
        val genderList = listOf("Male", "Female", "Other")
        callback(genderList)
    }
    // Fungsi untuk membuka galeri
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.type = "image/*"
        startActivity(intent)
    }
}