package com.newsapp.network


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val BASE_URL = "https://newsapi.org/"

    private const val HTTP_CLIENT_CONNECT_TIMEOUT = 60
    private const val HTTP_CLIENT_READ_TIMEOUT = 60

    fun getClient(): Retrofit? {

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()

        httpClient.addInterceptor(logging)
            .connectTimeout(HTTP_CLIENT_CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(HTTP_CLIENT_READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

    }
}