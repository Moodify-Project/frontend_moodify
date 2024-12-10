package com.example.frontend_moodify

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.frontend_moodify.data.remote.network.Injection
import com.example.frontend_moodify.data.remote.response.login.TokenResponse
import com.example.frontend_moodify.databinding.FragmentHomeBinding
import com.example.frontend_moodify.presentation.adapter.ArticleAdapter
import com.example.frontend_moodify.presentation.ui.detail.DetailActivity
import com.example.frontend_moodify.presentation.ui.profile.ProfileActivity
import com.example.frontend_moodify.presentation.viewmodel.ArticleViewModel
import com.example.frontend_moodify.presentation.viewmodel.ArticleViewModelFactory
import com.example.frontend_moodify.presentation.viewmodel.ProfileViewModel
import com.example.frontend_moodify.presentation.viewmodel.ProfileViewModelFactory
import com.example.frontend_moodify.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ArticleViewModel
    private lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        _binding = FragmentHomeBinding.bind(view)

        if (!isNetworkAvailable()) {
            showNoConnectionLayout()
        } else {
            val repository = Injection.provideArticleRepository(sessionManager)
            val factory = ArticleViewModelFactory(repository)
            viewModel = ViewModelProvider(this, factory)[ArticleViewModel::class.java]

            val repositoryProfile = Injection.provideProfileRepository(sessionManager)
            val factoryProfile = ProfileViewModelFactory(repositoryProfile)
            val viewModelProfile = ViewModelProvider(this, factoryProfile).get(ProfileViewModel::class.java)

            viewModelProfile.profile.observe(viewLifecycleOwner) { profile ->
                val name = profile?.name?.takeIf { it.isNotBlank() } ?: "Moodifyers"
                Log.d("HomeFragment", "$name")
                // Pastikan TextView tersedia sebelum animasi
                binding.nameUser?.let { textView ->
                    textView.text = "" // Set teks kosong sebelum animasi
                    animateText("Helo, $name") // Animasi teks
                }

                // Set gambar pengguna jika ada, atau gunakan gambar default
                binding.userImage?.let { imageView ->
                    if (profile?.urlphoto != null) {
                        Glide.with(this)
                            .load(profile.urlphoto)
                            .into(imageView)
                    }
                }

                // Jika profil tidak ada, tambahkan animasi pada container
                if (profile == null) {
                    animateProfileContainer()
                }
            }
            viewModelProfile.getProfile()

            val adapter = ArticleAdapter { article ->
                val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                    putExtra("articleId", article.id)
                    putExtra("title", article.title)
                }
                startActivity(intent)
            }

            binding.articlesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.articlesRecyclerView.adapter = adapter

            viewModel.articles.observe(viewLifecycleOwner) {
                adapter.submitList(it)
                animateRecyclerViewItems()
            }

            viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }

            viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
                showMaintenanceLayout()
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }

            val currentDate = getCurrentDate()
            binding.dateTextView?.let {
                it.text = currentDate
            }

            viewModel.fetchArticles()
        }
    }

    private fun animateText(text: String) {
        val textView = binding.nameUser
        val handler = Handler(Looper.getMainLooper())
        var index = 0

        handler.post(object : Runnable {
            override fun run() {
                textView?.let {
                    if (index <= text.length) {
                        it.text = text.substring(0, index++)
                        it.alpha = 0f
                        it.animate().alpha(1f).setDuration(150).start()
                        handler.postDelayed(this, 100)
                    }
                }
            }
        })
    }

    private fun animateProfileContainer() {
        val profileTranslationY = ObjectAnimator.ofFloat(binding.containerProfile, "translationY", -50f, 0f).setDuration(500)
        profileTranslationY.start()
    }

    private fun animateRecyclerViewItems() {
        val layoutManager = binding.articlesRecyclerView.layoutManager as LinearLayoutManager
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        for (i in firstVisibleItemPosition until binding.articlesRecyclerView.adapter!!.itemCount) {
            val itemView = layoutManager.findViewByPosition(i)
            itemView?.let {
                val animatorSet = AnimatorSet()

                val translationY = ObjectAnimator.ofFloat(it, "translationY", 50f, 0f).setDuration(300)
                val alpha = ObjectAnimator.ofFloat(it, "alpha", 0f, 1f).setDuration(300)
                animatorSet.playTogether(translationY, alpha)
                animatorSet.start()
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun showNoConnectionLayout() {
        binding.emptyStateLayout.root.visibility = View.VISIBLE
        binding.articlesRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun showMaintenanceLayout() {
        binding.maintenanceStateLayout.root.visibility = View.VISIBLE
        binding.articlesRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
        val formattedDate = dateFormat.format(Date())
        return formattedDate.uppercase()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
