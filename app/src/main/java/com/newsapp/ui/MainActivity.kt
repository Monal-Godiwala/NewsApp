package com.newsapp.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newsapp.R
import com.newsapp.databinding.ActivityMainBinding
import com.newsapp.network.ApiClient
import com.newsapp.network.ApiService
import com.newsapp.repository.PAGE_SIZE
import com.newsapp.repository.Repository
import com.newsapp.ui.viewmodel.NewsViewModel
import com.newsapp.ui.viewmodel.NewsViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var dataBind: ActivityMainBinding

    private var apiService: ApiService? =
        ApiClient.getClient()?.create(ApiService::class.java)
    private val repository = Repository(apiService)
    private val factory = NewsViewModelFactory(repository)
    private val viewModel: NewsViewModel by lazy {
        ViewModelProvider(this, factory)[NewsViewModel::class.java]
    }

    private var newsAdapter: NewsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBind = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initViews()
        bindData()

    }

    private fun initViews() {

        newsAdapter = NewsAdapter(viewModel.articles)

        viewModel.getNewsList { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }

        observeNewsList()
    }

    private fun bindData() {

        dataBind.listNews.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = newsAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager?

                    /**  Load More */
                    val visibleItemCount = layoutManager?.childCount ?: 0
                    val totalItemCount = layoutManager?.itemCount ?: 0
                    val firstVisibleItemPosition =
                        layoutManager?.findFirstVisibleItemPosition() ?: 0

                    viewModel.loadMore(
                        visibleItemCount,
                        totalItemCount,
                        firstVisibleItemPosition,
                        newsAdapter
                    )

                }
            })
        }

    }

    private fun observeNewsList() {
        viewModel.articleList.observe(this) { articleListResponse ->
            articleListResponse?.articles?.let { articles ->

                /** Set total lead count */
                viewModel.totalCount =
                    if (articleListResponse.totalResults ?: 0 > 100)
                        100
                    else
                        articleListResponse.totalResults ?: 0

                /** Remove spinner load more view */
                if (viewModel.page != 1) {
                    viewModel.articles.removeAt(viewModel.articles.size - 1)
                    dataBind.listNews.adapter?.notifyItemRemoved(viewModel.articles.size)
                }

                /** Add latest chuck to all leads array */
                viewModel.articles.addAll(articles)
                dataBind.listNews.adapter?.notifyItemRangeInserted(
                    viewModel.page * PAGE_SIZE,
                    PAGE_SIZE
                )

                /** Hide Loader */
                if (viewModel.page == 1) {
                    dataBind.loader.visibility = View.GONE
                }
                viewModel.isLoading = false
            }
        }
    }
}