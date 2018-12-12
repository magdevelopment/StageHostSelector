package com.magdv.stagehostselector.sampleapp.network

import okhttp3.Interceptor
import okhttp3.Response
import java.util.Date

class LoggingInterceptor : Interceptor {

    var callback: ((HttpLog) -> Unit)? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val httpLog = HttpLog(
            Date(),
            request.url().toString()
        )
        callback?.invoke(httpLog)

        return chain.proceed(request)
    }
}