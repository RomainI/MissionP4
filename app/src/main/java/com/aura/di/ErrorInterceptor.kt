package com.aura.di

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ErrorInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code in 400..599) {
            // Handle different error codes here
            when (response.code) {
                500 -> {
                    // Handle server error (500)
                    // You can throw a custom exception or handle it as needed
                    throw IOException("Wrong recipient or Internal Server Error")
                }
                // Add other status codes you want to handle
            }
        }

        return response
    }
}