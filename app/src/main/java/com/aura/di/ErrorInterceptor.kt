package com.aura.di

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * class used to catch error
 * in this case, catching error 500 when there is an error sent to the server from the transfer form
 */

class ErrorInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code in 400..599) {
            when (response.code) {
                500 -> {
                    // Handle server error (500)
                    throw IOException("Wrong recipient or Internal Server Error")
                }
            }
        }

        return response
    }
}