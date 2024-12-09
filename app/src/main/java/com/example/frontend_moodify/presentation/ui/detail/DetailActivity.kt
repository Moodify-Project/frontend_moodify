package com.example.frontend_moodify.presentation.ui.detail

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import com.example.frontend_moodify.R
import com.example.frontend_moodify.data.remote.network.Injection
import com.example.frontend_moodify.data.remote.response.news.Article
import com.example.frontend_moodify.databinding.ActivityDetailBinding
import com.example.frontend_moodify.presentation.viewmodel.BookmarkViewModel
import com.example.frontend_moodify.presentation.viewmodel.BookmarkViewModelFactory
import com.example.frontend_moodify.presentation.viewmodel.DetailViewModel
import com.example.frontend_moodify.presentation.viewmodel.DetailViewModelFactory
import com.example.frontend_moodify.utils.SessionManager
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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

        if (!isNetworkAvailable()) {
            showOfflineLayout()
        } else {
            loadData()
        }

        val articleId = intent.getStringExtra("articleId") ?: run {
            Log.e("DetailActivity", "Article ID is null")
            finish()
            return
        }
        val title = intent.getStringExtra("title")

        Log.d("DetailActivity", "Received article ID: $articleId")
//        val imageUrl = intent.getStringExtra("imageUrl")
//        val description = intent.getStringExtra("content")
//        val publishDate = intent.getStringExtra("publishDate")
//        val bookmarkCount = intent.getIntExtra("bookmarkCount", -1)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.apply {
            this.title = title ?: getString(R.string.header_title)
            setDisplayHomeAsUpEnabled(true)
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewModel.articleDetail.observe(this) { article ->
            binding.articleTitle.text = article.title
            binding.articleContent.text = article.content
            val formattedDate = formatDate(article.publishedAt)
            binding.articleDate.text = formattedDate
            val countBookmark = article.countBookmarked
            binding.bookmarkCount.text = countBookmark.toString()

            Log.d("DetailActivity", "BookmarkCount: $countBookmark")
            Glide.with(this)
                .load(article.urlToImage)
                .into(binding.headerImage)
        }

        viewModel.loading.observe(this, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) Log.d("DetailActivity", "Loading article details...")
        })

        viewModel.error.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                Log.e("DetailActivity", "Error occurred: $it")
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.isBookmarked.observe(this) { isBookmarked ->
            val iconRes =
                if (isBookmarked) R.drawable.ic_bookmark_active else R.drawable.ic_bookmark_nonactive
            binding.fab.setImageResource(iconRes)
        }

        viewModel.fetchBookmarkStatus(articleId)
        viewModel.fetchArticleDetail(articleId)

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
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun showOfflineLayout() {
        binding.emptyStateLayout.root.visibility = View.VISIBLE
        binding.scrollableContent.visibility = View.GONE
    }
    private fun loadData() {
        binding.emptyStateLayout.root.visibility = View.GONE
        binding.scrollableContent.visibility = View.VISIBLE
    }
    private fun formatDate(dateString: String?): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(dateString)

            val outputFormat = SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            e.printStackTrace()
            "Invalid date"
        }
    }
}
