package com.newsapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newsapp.data.News
import com.newsapp.repository.Repository
import com.newsapp.ui.NewsAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsViewModel(private val repository: Repository) : ViewModel() {

    private val _articleList = MutableLiveData<News?>()
    val articleList: LiveData<News?>
        get() = _articleList

    var articles = ArrayList<News.Article?>()

    var page = 1
    var isLoading: Boolean = false
    var totalCount: Int = 0

    fun loadMore(
        visibleItemCount: Int,
        totalItemCount: Int,
        firstVisibleItemPosition: Int,
        adapter: NewsAdapter?
    ) {
        if (!isLoading && (totalItemCount < totalCount)) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                isLoading = true
                articles.add(null)
                adapter?.notifyItemInserted(articles.size - 1)
                page += 1
                getNewsList()
            }
        }
    }

    fun getNewsList(onFailure: ((String) -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getNewsList(page)
                withContext(Dispatchers.Main) {
                    _articleList.value = response
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    e.message?.let {
                        _articleList.value = null
                        onFailure?.invoke(it)
                    }
                }
            }
        }
    }

}