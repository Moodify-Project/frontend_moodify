package com.example.frontend_moodify.presentation.ui.bookmark

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frontend_moodify.R
import com.example.frontend_moodify.data.remote.network.Injection
import com.example.frontend_moodify.databinding.ActivityBookmarkBinding
import com.example.frontend_moodify.presentation.adapter.BookmarkAdapter
import com.example.frontend_moodify.presentation.ui.detail.DetailActivity
import com.example.frontend_moodify.presentation.viewmodel.BookmarkViewModel
import com.example.frontend_moodify.presentation.viewmodel.BookmarkViewModelFactory
import com.example.frontend_moodify.utils.SessionManager
import kotlinx.coroutines.flow.collect

class BookmarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookmarkBinding
    private val viewModel: BookmarkViewModel by viewModels {
        BookmarkViewModelFactory(Injection.provideArticleRepository(SessionManager(this)))
    }
    private val adapter = BookmarkAdapter { article ->
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("title", article.title)
            putExtra("imageUrl", article.urlToImage)
            putExtra("description", article.description)
            putExtra("publishDate", article.publishedAt)
            putExtra("bookmarkCount", article.bookmarkedCount)
            putExtra("articleId", article.id)
            putExtra("content", article.content)
        }
        Log.d("BookmarkActivity", "publishDate: ${article.id}")

        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewModel.bookmarkedArticles.observe(this) { articles ->
            adapter.submitList(articles)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.fetchBookmarkedArticles()
    }

    private fun setupRecyclerView() {
        binding.favoriteArticlesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@BookmarkActivity)
            adapter = this@BookmarkActivity.adapter
        }
    }
}
