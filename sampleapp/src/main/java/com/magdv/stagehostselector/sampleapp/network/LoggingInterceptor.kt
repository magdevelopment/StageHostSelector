package com.magdv.stagehostselector.sampleapp.network

import okhttp3.*
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

        return request.createSuccessResponse()
    }

    private fun Request.createSuccessResponse(): Response {
        return Response.Builder()
            .request(this)
            .protocol(Protocol.HTTP_1_1)
            .message("")
            .body(ResponseBody.create(MediaType.get("application/json"), ""))
            .code(200)
            .build()
    }
}