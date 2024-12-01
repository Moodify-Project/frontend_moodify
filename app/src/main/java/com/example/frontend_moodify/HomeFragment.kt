package com.example.frontend_moodify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frontend_moodify.data.remote.network.Injection
import com.example.frontend_moodify.data.remote.response.login.TokenResponse
import com.example.frontend_moodify.databinding.FragmentHomeBinding
import com.example.frontend_moodify.presentation.adapter.ArticleAdapter
import com.example.frontend_moodify.presentation.ui.detail.DetailActivity
import com.example.frontend_moodify.presentation.viewmodel.ArticleViewModel
import com.example.frontend_moodify.presentation.viewmodel.ArticleViewModelFactory
import com.example.frontend_moodify.utils.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: ArticleViewModel
    private lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        binding = FragmentHomeBinding.bind(view)

        val repository = Injection.provideArticleRepository(sessionManager)
        val factory = ArticleViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ArticleViewModel::class.java]

        val adapter = ArticleAdapter { article ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("title", article.title)
                putExtra("imageUrl", article.urlToImage)
                putExtra("description", article.description)
                putExtra("publishDate", article.publishedAt)
                putExtra("bookmarkCount", article.bookmarkedCount)
                putExtra("articleId", article.id)
                putExtra("content", article.content)
            }
            Log.d("HomeFragment", "publishDate: ${article.id}")

            startActivity(intent)
        }

        binding.articlesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.articlesRecyclerView.adapter = adapter

        viewModel.articles.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        val currentDate = getCurrentDate()
        binding.dateTextView.text = currentDate

        viewModel.fetchArticles()
    }
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
        val formattedDate = dateFormat.format(Date())
        return formattedDate.uppercase()
    }
}




