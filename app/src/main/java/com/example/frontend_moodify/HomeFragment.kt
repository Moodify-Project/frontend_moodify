package com.example.frontend_moodify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frontend_moodify.data.remote.response.news.Article
import com.example.frontend_moodify.presentation.adapter.ArticleAdapter

//class HomeFragment : Fragment(R.layout.fragment_home) {
//
//    private lateinit var articleViewModel: ArticleViewModel
//    private lateinit var binding: FragmentHomeBinding
//    private lateinit var articleAdapter: ArticleAdapter
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding = FragmentHomeBinding.bind(view)
//        articleAdapter = ArticleAdapter()
//
//        // Inisialisasi RecyclerView
//        binding.articlesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        binding.articlesRecyclerView.adapter = articleAdapter
//
//        // Menggunakan Injection untuk mendapatkan ArticleRepository
//        val repository = Injection.provideArticleRepository()
//
//        // Membuat ViewModel menggunakan factory pattern
//        val viewModelFactory = ArticleViewModelFactory(repository)
//        articleViewModel = ViewModelProvider(this, viewModelFactory).get(ArticleViewModel::class.java)
//
//        val token = SessionManager(requireContext()).getAccessToken()
//        if (token != null) {
//            observeViewModel()
//            articleViewModel.fetchArticles(token) // Ambil data artikel menggunakan token
//        } else {
//            // Tangani jika token tidak ada (misalnya, logout atau show error message)
//            Toast.makeText(requireContext(), "Token not available", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun observeViewModel() {
//        lifecycleScope.launchWhenStarted {
//            articleViewModel.articles.collect { articles ->
//                if (articles.isNotEmpty()) {
//                    articleAdapter.submitList(articles)
//                } else {
//                    // Tampilkan pesan jika tidak ada artikel
//                    Toast.makeText(requireContext(), "No articles found", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//        lifecycleScope.launchWhenStarted {
//            articleViewModel.loading.collect { isLoading ->
//                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//            }
//        }
//
//        lifecycleScope.launchWhenStarted {
//            articleViewModel.error.collect { errorMessage ->
//                errorMessage?.let {
//                    // Tampilkan pesan error
//                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//}
class HomeFragment : Fragment() {

    private lateinit var articlesRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        articlesRecyclerView = view.findViewById(R.id.articlesRecyclerView)
        progressBar = view.findViewById(R.id.progressBar)

        setupRecyclerView()
        return view
    }

    private fun setupRecyclerView() {
        // Contoh data artikel
        val articles = listOf(
            Article("Judul Artikel 1", "Deskripsi artikel 1", "27 Nov 2024", R.drawable.banner_article),
            Article("Judul Artikel 2", "Deskripsi artikel 2", "26 Nov 2024", R.drawable.banner_article),
            // Tambahkan lebih banyak data
        )

        // Atur adapter dan layout manager
        articlesRecyclerView.layoutManager = LinearLayoutManager(context)
        articlesRecyclerView.adapter = ArticleAdapter(articles)
    }
}

