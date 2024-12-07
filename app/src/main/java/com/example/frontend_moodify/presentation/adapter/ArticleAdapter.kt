package com.example.frontend_moodify.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.frontend_moodify.data.remote.response.news.Article
import com.example.frontend_moodify.databinding.ItemArticleBinding
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.text.SimpleDateFormat

class ArticleAdapter(private val onClick: (Article) -> Unit) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    private val articles = mutableListOf<Article>()

    fun submitList(newArticles: List<Article>) {
        articles.clear()
        articles.addAll(newArticles)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount(): Int = articles.size

    inner class ArticleViewHolder(private val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) {
            binding.articleTitle.text = article.title
            binding.articleDescription.text = article.description
            val formattedDate = formatDate(article.publishedAt)
            binding.articleDate.text = formattedDate
            Glide.with(binding.root).load(article.urlToImage).into(binding.articleImage)

            binding.root.setOnClickListener {
                onClick(article)
                Log.d("HomeFragment", "Clicked article ID: ${article.id}")
            }
        }
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