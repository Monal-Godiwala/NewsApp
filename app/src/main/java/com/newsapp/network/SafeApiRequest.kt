package com.newsapp.network


import retrofit2.Response
import java.io.IOException

abstract class SafeApiRequest {

    suspend fun <T : Any> apiRequest(call: suspend () -> Response<T>?): T {
        val response = call.invoke()
        val message = "Invalid Request!"

        if (response?.isSuccessful == true && response.body() != null) {
            return response.body()!!
        } else {
            throw IOException(message)
        }
    }

}