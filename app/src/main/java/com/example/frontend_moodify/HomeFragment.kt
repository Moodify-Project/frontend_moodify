package com.example.frontend_moodify

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.frontend_moodify.data.remote.network.Injection
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
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: ArticleViewModel
    private lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        binding = FragmentHomeBinding.bind(view)

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
                if (profile != null) {
                    binding.nameUser?.let {
                        val name = profile.name
                        it.text = "Hello, $name"
                    }
                    binding.userImage?.let {
                        Glide.with(this)
                            .load(profile.urlphoto)
                            .into(it)
                    }
                    animateProfileContainer()
                }
            }
            viewModelProfile.getProfile()

            val adapter = ArticleAdapter { article ->
                val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                    putExtra("articleId", article.id)
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
}
