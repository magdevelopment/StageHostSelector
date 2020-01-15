package com.magdv.stagehostselector.interceptor

import android.content.Context
import android.preference.PreferenceManager
import com.magdv.stagehostselector.Constants
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class StageHostSelectorInterceptor(
    context: Context,
    private val defaultHostUrl: String
) : Interceptor {

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

        val newRequest = cachedHostHttpUrl?.let { cachedHostHttpUrl ->

            val defaultHostUrlSegmentsCount = HttpUrl.parse(defaultHostUrl)?.pathSegments()?.count() ?: 0
            val necessaryPartHostUrlSegmentsCount = if (defaultHostUrlSegmentsCount == 0) 0 else defaultHostUrlSegmentsCount - 1
            val chainRequestUrlSegmentCount = chain.request().url().pathSegments().count()

            var pathSegments = ""
            for (i in necessaryPartHostUrlSegmentsCount until chainRequestUrlSegmentCount) {
                pathSegments += "${chain.request().url().pathSegments()[i]}/"
            }

            val newUrl = cachedHostHttpUrl.newBuilder()
                .addPathSegments(pathSegments)
                .build()

            return@let chain.request().newBuilder()
                .url(newUrl)
                .build()
        }

        return chain.proceed(newRequest ?: chain.request())
    }
}