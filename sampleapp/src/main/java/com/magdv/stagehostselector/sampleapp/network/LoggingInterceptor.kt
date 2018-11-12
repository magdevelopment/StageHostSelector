package com.magdv.stagehostselector.sampleapp.network

import okhttp3.Interceptor
import okhttp3.Response

class LoggingInterceptor : Interceptor {

    var callback: ((String) -> Unit)? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val url = request.url()
        callback?.invoke("${url.host()}${url.encodedPath()}")

        val response = chain.proceed(request)

        // On New Response

        return response
    }
}