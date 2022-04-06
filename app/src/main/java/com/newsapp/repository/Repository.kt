package com.newsapp.repository

import com.newsapp.data.News
import com.newsapp.network.ApiService
import com.newsapp.network.SafeApiRequest

const val PAGE_SIZE = 10

class Repository(private val apiService: ApiService?) : SafeApiRequest() {

    suspend fun getNewsList(page: Int): News =
        apiRequest { apiService?.getNewsList(page, PAGE_SIZE) }

}