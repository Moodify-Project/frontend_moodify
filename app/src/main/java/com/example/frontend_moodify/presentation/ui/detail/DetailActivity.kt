package com.example.frontend_moodify.presentation.ui.detail

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.frontend_moodify.R
import com.example.frontend_moodify.data.remote.network.Injection
import com.example.frontend_moodify.databinding.ActivityDetailBinding
import com.example.frontend_moodify.presentation.viewmodel.DetailViewModel
import com.example.frontend_moodify.presentation.viewmodel.DetailViewModelFactory
import com.example.frontend_moodify.utils.SessionManager

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels {
        DetailViewModelFactory(Injection.provideArticleApiService(SessionManager(this)))
    }

    private val sharedPref by lazy {
        getSharedPreferences("BookmarkPreferences", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val articleId = intent.getStringExtra("articleId") ?: return
        val title = intent.getStringExtra("title")
        val imageUrl = intent.getStringExtra("imageUrl")
        val description = intent.getStringExtra("content")
        val publishDate = intent.getStringExtra("publishDate")
        val bookmarkCount = intent.getIntExtra("bookmarkCount", -1)

        // Check saved bookmark status
        val isInitiallyBookmarked = getSavedBookmarkStatus(articleId)

        // Set article details
        binding.articleTitle.text = title
        binding.articleContent.text = description
        binding.articleDate.text = publishDate
        binding.bookmarkCount.text = bookmarkCount.toString()

        Glide.with(this)
            .load(imageUrl)
            .into(binding.headerImage)

        // Handle Back Button
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Observe Bookmark Status
        viewModel.isBookmarked.observe(this) { isBookmarked ->
            val iconRes = if (isBookmarked) R.drawable.ic_bookmark_active else R.drawable.ic_bookmark_nonactive
            binding.fab.setImageResource(iconRes)
        }

        // Handle FAB Click
        binding.fab.setOnClickListener {
            if (viewModel.isBookmarked.value == true) {
                // Remove bookmark
                viewModel.removeBookmark(articleId)
                removeBookmarkStatus(articleId)
                Toast.makeText(this, "Bookmark removed", Toast.LENGTH_SHORT).show()
            } else {
                // Add bookmark
                viewModel.addBookmark(articleId)
                saveBookmarkStatus(articleId, true)
                Toast.makeText(this, "Bookmark added", Toast.LENGTH_SHORT).show()
            }
        }

        // Set Initial Bookmark Status
        viewModel.setBookmarkStatus(isInitiallyBookmarked)
    }

    private fun saveBookmarkStatus(articleId: String, isBookmarked: Boolean) {
        sharedPref.edit()
            .putBoolean(articleId, isBookmarked)
            .apply()
    }

    private fun removeBookmarkStatus(articleId: String) {
        sharedPref.edit()
            .remove(articleId)
            .apply()
    }

    private fun getSavedBookmarkStatus(articleId: String): Boolean {
        return sharedPref.getBoolean(articleId, false)
    }
}
