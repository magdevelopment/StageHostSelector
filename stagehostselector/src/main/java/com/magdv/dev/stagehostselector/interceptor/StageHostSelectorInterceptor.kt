package com.magdv.dev.stagehostselector.interceptor

import android.content.Context
import android.preference.PreferenceManager
import com.magdv.dev.stagehostselector.Constants
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class StageHostSelectorInterceptor(context: Context) : Interceptor {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private var cachedHostUrl: String? = null
    private var cachedHostHttpUrl: HttpUrl? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val hostUrl = sharedPreferences.getString(Constants.HOST_URL_STORAGE_KEY, null)
            ?: return chain.proceed(chain.request())

        if (cachedHostUrl != hostUrl) {
            cachedHostUrl = hostUrl
            cachedHostHttpUrl = HttpUrl.parse(hostUrl) ?: throw Exception("Invalid url: $hostUrl")
        }

        val newRequest = cachedHostHttpUrl?.let {
            val newUrl = chain.request().url().newBuilder()
                .scheme(it.scheme())
                .host(it.url().toURI().host)
                .port(it.port())
                .build()

            return@let chain.request().newBuilder()
                .url(newUrl)
                .build()
        }

        return chain.proceed(newRequest ?: chain.request())
    }
}