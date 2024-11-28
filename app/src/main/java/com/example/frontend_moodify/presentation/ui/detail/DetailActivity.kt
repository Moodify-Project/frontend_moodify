package com.example.frontend_moodify.presentation.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.frontend_moodify.R
import com.example.frontend_moodify.data.remote.network.ArticleApiService
import com.example.frontend_moodify.data.remote.network.Injection
import com.example.frontend_moodify.databinding.ActivityDetailBinding
import com.example.frontend_moodify.databinding.ActivityMainBinding
import com.example.frontend_moodify.presentation.viewmodel.DetailViewModel
import com.example.frontend_moodify.presentation.viewmodel.DetailViewModelFactory
import com.example.frontend_moodify.utils.SessionManager

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels {
        DetailViewModelFactory(Injection.provideArticleApiService(SessionManager(this)))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val articleId = intent.getStringExtra("articleId") ?: return
        val title = intent.getStringExtra("title")
        val imageUrl = intent.getStringExtra("imageUrl")
        val description = intent.getStringExtra("description")
        val publishDate = intent.getStringExtra("publishDate")
        val bookmarkCount = intent.getIntExtra("bookmarkCount", -1)

        Log.d("DetailActivity", "bookmarkCount received: $bookmarkCount")

        binding.articleTitle.text = title
        binding.articleContent.text = description
        binding.articleDate.text = publishDate
        binding.bookmarkCount.text = bookmarkCount.toString()

        Glide.with(this)
            .load(imageUrl)
            .into(binding.headerImage)

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewModel.isBookmarked.observe(this) { isBookmarked ->
            val iconRes = if (isBookmarked) R.drawable.ic_bookmark_active else R.drawable.ic_bookmark_nonactive
            binding.fab.setImageResource(iconRes)
        }
        binding.fab.setOnClickListener { view ->
            viewModel.toggleBookmark(articleId)
        }
        val isInitiallyBookmarked = intent.getBooleanExtra("isBookmarked", false)
        viewModel.setBookmarkStatus(isInitiallyBookmarked)
    }
}
