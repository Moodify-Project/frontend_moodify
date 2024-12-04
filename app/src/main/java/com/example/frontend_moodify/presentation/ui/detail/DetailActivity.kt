package com.example.frontend_moodify.presentation.ui.detail

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.frontend_moodify.R
import com.example.frontend_moodify.data.remote.network.Injection
import com.example.frontend_moodify.data.remote.response.news.Article
import com.example.frontend_moodify.databinding.ActivityDetailBinding
import com.example.frontend_moodify.presentation.viewmodel.BookmarkViewModel
import com.example.frontend_moodify.presentation.viewmodel.BookmarkViewModelFactory
import com.example.frontend_moodify.presentation.viewmodel.DetailViewModel
import com.example.frontend_moodify.presentation.viewmodel.DetailViewModelFactory
import com.example.frontend_moodify.utils.SessionManager

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels {
        DetailViewModelFactory(Injection.provideArticleApiService(SessionManager(this)))
    }
    private val sharedViewModel: BookmarkViewModel by viewModels {
        BookmarkViewModelFactory(Injection.provideArticleRepository(SessionManager(this)))
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

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.apply {
            this.title = title ?: getString(R.string.header_title)
            setDisplayHomeAsUpEnabled(true)
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.articleTitle.text = title
        binding.articleContent.text = description
        binding.articleDate.text = publishDate
        binding.bookmarkCount.text = bookmarkCount.toString()

        Glide.with(this)
            .load(imageUrl)
            .into(binding.headerImage)

        viewModel.isBookmarked.observe(this) { isBookmarked ->
            val iconRes =
                if (isBookmarked) R.drawable.ic_bookmark_active else R.drawable.ic_bookmark_nonactive
            binding.fab.setImageResource(iconRes)
        }

        viewModel.fetchBookmarkStatus(articleId)

        binding.fab.setOnClickListener {
            if (viewModel.isBookmarked.value == true) {
                viewModel.removeBookmark(articleId)
                sharedViewModel.fetchBookmarkedArticles()
                Toast.makeText(this, "Bookmark removed", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.addBookmark(articleId)
                sharedViewModel.fetchBookmarkedArticles()
                Toast.makeText(this, "Bookmark added", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
