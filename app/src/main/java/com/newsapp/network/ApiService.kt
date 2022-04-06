package com.newsapp.network

import com.newsapp.data.News
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("v2/everything")
    suspend fun getNewsList(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("domains") domains: String = "techcrunch.com,thenextweb.com",
        @Query("apiKey") apiKey: String = "406ab5ea708d4c0e9101d898a945247d"
    ): Response<News>

}