package com.example.frontend_moodify.presentation.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.frontend_moodify.R
import com.example.frontend_moodify.data.remote.response.news.Article
import com.example.frontend_moodify.databinding.ItemArticleBinding
import com.example.frontend_moodify.presentation.ui.detail.DetailActivity

//class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {
//
//    private val articles = mutableListOf<Article>()
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
//        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ArticleViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
//        val article = articles[position]
//        holder.bind(article)
//    }
//
//    override fun getItemCount(): Int = articles.size
//
//    fun submitList(newArticles: List<Article>) {
//        articles.clear()
//        articles.addAll(newArticles)
//        notifyDataSetChanged()
//    }
//
//    inner class ArticleViewHolder(private val binding: ItemArticleBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(article: Article) {
//            binding.apply {
//                articleTitle.text = article.title
//                articleDescription.text = article.description
//                // Atur data lainnya di sini, misalnya image, date, dll
//            }
//        }
//    }
//}

//class ArticleAdapter : ListAdapter<Article, ArticleAdapter.ArticleViewHolder>(DiffCallback()) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        val binding = ItemArticleBinding.inflate(inflater, parent, false)
//        return ArticleViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
//        holder.bind(getItem(position))
//    }
//
//    inner class ArticleViewHolder(private val binding: ItemArticleBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(article: Article) {
//            binding.articleTitle.text = article.title
//            binding.articleDescription.text = article.description
//            binding.articleDate.text = article.publishedAt
//            Glide.with(binding.root.context)
//                .load(article.urlToImage)
//                .into(binding.articleImage)
//
//            itemView.setOnClickListener {
//                val intent = Intent(itemView.context, DetailActivity::class.java)
////                intent.putExtra("ARTICLE", article)
//                itemView.context.startActivity(intent)
//            }
//        }
//    }
//
//    class DiffCallback : DiffUtil.ItemCallback<Article>() {
//        override fun areItemsTheSame(oldItem: Article, newItem: Article) = oldItem.id == newItem.id
//        override fun areContentsTheSame(oldItem: Article, newItem: Article) = oldItem == newItem
//    }
//}

class ArticleAdapter(private val articles: List<Article>) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.articleImage)
        val title: TextView = view.findViewById(R.id.articleTitle)
        val description: TextView = view.findViewById(R.id.articleDescription)
        val date: TextView = view.findViewById(R.id.articleDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.image.setImageResource(article.imageResId)
        holder.title.text = article.title
        holder.description.text = article.description
        holder.date.text = article.date
    }

    override fun getItemCount(): Int {
        return articles.size
    }
}