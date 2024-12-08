package com.example.frontend_moodify.presentation.ui.detail

import android.animation.ObjectAnimator
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.frontend_moodify.R
import com.example.frontend_moodify.data.remote.network.Injection
import com.example.frontend_moodify.databinding.ActivityDetailBinding
import com.example.frontend_moodify.presentation.viewmodel.BookmarkViewModel
import com.example.frontend_moodify.presentation.viewmodel.BookmarkViewModelFactory
import com.example.frontend_moodify.presentation.viewmodel.DetailViewModel
import com.example.frontend_moodify.presentation.viewmodel.DetailViewModelFactory
import com.example.frontend_moodify.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

        // Initial animations
        fadeInToolbar()
        fadeInProgressBar()
        fadeInFabButton()

        if (!isNetworkAvailable()) {
            showOfflineLayout()
        } else {
            loadData()
        }

        val articleId = intent.getStringExtra("articleId") ?: run {
            Toast.makeText(this, "Article ID is null", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val title = intent.getStringExtra("title")
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
            binding.bookmarkCount.text = article.bookmarkedCount.toString()

            Glide.with(this)
                .load(article.urlToImage)
                .into(binding.headerImage)

            // Fade-in article content after loading data
            fadeInArticleContent()
        }

        viewModel.loading.observe(this, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.error.observe(this, Observer { errorMessage ->
            errorMessage?.let {
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

            // FAB animation (scale)
            animateFab()
        }
    }

    private fun fadeInToolbar() {
        val toolbarAnimator = ObjectAnimator.ofFloat(binding.topAppBar, "alpha", 0f, 1f)
        toolbarAnimator.duration = 300
        toolbarAnimator.start()
    }

    private fun fadeInProgressBar() {
        val progressBarAnimator = ObjectAnimator.ofFloat(binding.progressBar, "alpha", 0f, 1f)
        progressBarAnimator.duration = 500
        progressBarAnimator.start()
    }

    private fun fadeInFabButton() {
        val fabAnimator = ObjectAnimator.ofFloat(binding.fab, "alpha", 0f, 1f)
        fabAnimator.duration = 500
        fabAnimator.start()
    }

    private fun fadeInArticleContent() {
        val contentAnimator = ObjectAnimator.ofFloat(binding.articleContent, "alpha", 0f, 1f)
        contentAnimator.duration = 500
        contentAnimator.start()

        val headerImageAnimator = ObjectAnimator.ofFloat(binding.headerImage, "alpha", 0f, 1f)
        headerImageAnimator.duration = 500
        headerImageAnimator.start()

        val articleTitleAnimator = ObjectAnimator.ofFloat(binding.articleTitle, "alpha", 0f, 1f)
        articleTitleAnimator.duration = 500
        articleTitleAnimator.start()

        val articleDateAnimator = ObjectAnimator.ofFloat(binding.articleDate, "alpha", 0f, 1f)
        articleDateAnimator.duration = 500
        articleDateAnimator.start()

        val bookmarkCountAnimator = ObjectAnimator.ofFloat(binding.bookmarkCount, "alpha", 0f, 1f)
        bookmarkCountAnimator.duration = 500
        bookmarkCountAnimator.start()
    }

    private fun animateFab() {
        val scaleXAnimator = ObjectAnimator.ofFloat(binding.fab, "scaleX", 1f, 1.2f, 1f)
        val scaleYAnimator = ObjectAnimator.ofFloat(binding.fab, "scaleY", 1f, 1.2f, 1f)
        scaleXAnimator.duration = 300
        scaleYAnimator.duration = 300
        scaleXAnimator.start()
        scaleYAnimator.start()
    }

    private fun isNetworkAvailable(): Boolean {
        // Check network status
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
            val date = inputFormat.parse(dateString)

            val outputFormat = SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            e.printStackTrace()
            "Invalid date"
        }
    }
}
