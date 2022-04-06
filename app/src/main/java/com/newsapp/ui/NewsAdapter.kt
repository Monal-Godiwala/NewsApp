package com.newsapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.newsapp.R
import com.newsapp.data.News
import com.newsapp.databinding.ListItemNewsBinding
import com.newsapp.util.GlideApp


class NewsAdapter(private val articleList: ArrayList<News.Article?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        context = parent.context

        return if (viewType == VIEW_TYPE_ITEM) {
            NewsViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.list_item_news,
                    parent,
                    false
                )
            )
        } else {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_load_progress, parent, false)
            LoadMoreViewHolder(view)
        }

    }

    override fun getItemCount() = articleList.size

    override fun getItemViewType(position: Int): Int =
        if (articleList[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NewsViewHolder) {
            holder.bindItems(articleList[position])
        }
    }

    inner class NewsViewHolder(private val dataBind: ListItemNewsBinding) :
        RecyclerView.ViewHolder(dataBind.root) {

        @SuppressLint("SetTextI18n")
        fun bindItems(article: News.Article?) {
            dataBind.apply {
                textTitle.text = article?.title
                textDescription.text = article?.description

                context?.let {
                    GlideApp.with(it)
                        .load(article?.urlToImage)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageArticle)
                }

            }
        }

    }

    inner class LoadMoreViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)

}