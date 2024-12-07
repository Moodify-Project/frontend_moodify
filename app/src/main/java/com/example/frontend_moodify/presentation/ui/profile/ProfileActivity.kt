package com.example.frontend_moodify.presentation.ui.profile

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.frontend_moodify.R
import com.example.frontend_moodify.data.remote.network.Injection
import com.example.frontend_moodify.databinding.ActivityProfileBinding
import com.example.frontend_moodify.presentation.adapter.NationsAdapter
import com.example.frontend_moodify.presentation.viewmodel.NationViewModel
import com.example.frontend_moodify.presentation.viewmodel.NationViewModelFactory
import com.example.frontend_moodify.presentation.viewmodel.ProfileViewModel
import com.example.frontend_moodify.presentation.viewmodel.ProfileViewModelFactory
import com.example.frontend_moodify.utils.SessionManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val REQUEST_IMAGE_PICK = 1
    private val REQUEST_PERMISSION_CODE = 100
    private lateinit var viewModel: NationViewModel
    private lateinit var adapter: NationsAdapter
    private var isLoading = false
    private var currentList = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAndRequestPermissions()

        binding.profileImage.setOnClickListener {
            showImagePreview()
        }

        binding.changeProfileImageButton.setOnClickListener {
            openGallery()
        }
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val sessionManager = SessionManager(this)
        val repositoryProfile = Injection.provideProfileRepository(sessionManager)
        val factoryProfile = ProfileViewModelFactory(repositoryProfile)
        val viewModelProfile = ViewModelProvider(this, factoryProfile).get(ProfileViewModel::class.java)


        val repository = Injection.provideNationsRepository()
        val factory = NationViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[NationViewModel::class.java]

        adapter = NationsAdapter(this, currentList)
        binding.nationSpinner.setAdapter(adapter)

        observeData()

        viewModel.fetchNations()

        setupScrollListener(binding)
        val genderList = listOf("male", "female")
        val genderAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            genderList
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.genderSpinner.adapter = genderAdapter
        binding.genderSpinner.setSelection(0)


        binding.genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedGender = genderList[position]
                Toast.makeText(this@ProfileActivity, "Selected: $selectedGender", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                binding.genderSpinner.setSelection(0)
                Toast.makeText(this@ProfileActivity, "Default set to Male", Toast.LENGTH_SHORT).show()
            }
        }

        viewModelProfile.profile.observe(this) { profile ->
            if (profile != null) {
                binding.nameInput.setText(profile.name)
                val genderPosition = genderList.indexOf(profile.gender)
                if (genderPosition >= 0) {
                    binding.genderSpinner.setSelection(genderPosition)
                } else {
                    binding.genderSpinner.setSelection(0)
                }

                if (profile.country.isNotEmpty()) {
                    binding.nationSpinner.setText(profile.country, false)
                }


                Glide.with(this)
                    .load(profile.urlphoto)
                    .into(binding.profileImage)
            }
        }

        viewModelProfile.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }

        viewModelProfile.getProfile()


        binding.submitButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val gender = binding.genderSpinner.selectedItem.toString()
            val country = binding.nationSpinner.text.toString()

            viewModelProfile.updateProfile(name, gender, country)
        }

        viewModelProfile.updateStatus.observe(this) { success ->
            if (success == true) {
                Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show()
            }
        }

        viewModelProfile.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }

        viewModelProfile.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
    private fun observeData() {
        viewModel.nations.observe(this) { nations ->
            isLoading = false
            Log.d("ProfileActivity", "Received Nations: $nations")
            if (nations.isEmpty()) {
                Log.w("ProfileActivity", "No nations found!")
            } else {
                adapter.updateNations(nations)
            }
        }

        viewModel.error.observe(this) { errorMessage ->
            isLoading = false
            Log.e("ProfileActivity", "Error: $errorMessage")
            Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupScrollListener(binding: ActivityProfileBinding) {
        binding.nationSpinner.setOnItemClickListener { _, _, position, _ ->
            val selectedNation = adapter.getItem(position)
            Log.d("ProfileActivity", "Selected nation: $selectedNation")
            Toast.makeText(this, "Selected: $selectedNation", Toast.LENGTH_SHORT).show()
//            binding.nationSpinner.showDropDown()
        }

        binding.nationSpinner.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                binding.nationSpinner.showDropDown()
                binding.nationSpinner.performClick()
            }
            false
        }

    }
    private fun showImagePreview() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_image_preview)
        val imageView = dialog.findViewById<ImageView>(R.id.imagePreview)
        imageView.setImageDrawable(binding.profileImage.drawable)
        dialog.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        } else {
            Toast.makeText(this, "Tidak ada aplikasi untuk memilih gambar!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            selectedImageUri?.let { uri ->
                val file = getFileFromUri(uri)
                file?.let {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    binding.profileImage.setImageBitmap(bitmap)

                    uploadImageToServer(it)
                } ?: run {
                    Toast.makeText(this, "Gagal memproses file gambar!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadImageToServer(file: File) {
        val mimeType = getMimeType(file)
        Log.d("ProfileActivity", "MIME type detected: $mimeType")

        if (mimeType == null || (mimeType != "image/jpeg" && mimeType != "image/png")) {
            Toast.makeText(this, "Format file tidak didukung! Harus berupa JPEG atau PNG.", Toast.LENGTH_SHORT).show()
            return
        }

        val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestBody)

        Log.d("ProfileActivity", "MIME type: $mimeType")
        Log.d("ProfileActivity", "File name: ${file.name}")

        val sessionManager = SessionManager(this)
        val profileRepository = Injection.provideProfileRepository(sessionManager)
        val call = profileRepository.uploadProfileImage(file)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProfileActivity, "Foto berhasil diunggah!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ProfileActivity, "Gagal mengunggah foto!", Toast.LENGTH_SHORT).show()
                    Log.e("ProfileActivity", "Response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "Terjadi kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("ProfileActivity", "Error: ${t.message}", t)
            }
        })
    }

    private fun getFileFromUri(uri: Uri): File? {
        val contentResolver = contentResolver
        val mimeType = contentResolver.getType(uri)
        val extension = mimeType?.substringAfterLast("/") ?: "jpg" // Default ke jpg jika MIME type tidak terdeteksi
        Log.d("ProfileActivity", "MIME type: $mimeType, Extension: $extension")

        val fileName = "temp_file_${System.currentTimeMillis()}.$extension"
        val tempFile = File(cacheDir, fileName)
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: Exception) {
            Log.e("ProfileActivity", "Error creating file from URI", e)
            return null
        }
        return tempFile
    }

    private fun getMimeType(file: File): String? {
        val extension = file.extension.lowercase()
        return when (extension) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            else -> null
        }
    }


    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        } else {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            val neededPermissions = permissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }

            if (neededPermissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, neededPermissions.toTypedArray(), REQUEST_PERMISSION_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION_CODE) {
            val deniedPermissions = permissions.filterIndexed { index, _ ->
                grantResults[index] != PackageManager.PERMISSION_GRANTED
            }

            if (deniedPermissions.isEmpty()) {
                Toast.makeText(this, "Izin diberikan, Anda dapat melanjutkan.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Izin diperlukan untuk mengakses file!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
