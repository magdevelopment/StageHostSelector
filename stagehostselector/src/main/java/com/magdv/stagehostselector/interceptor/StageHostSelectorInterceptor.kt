package com.magdv.stagehostselector.interceptor

import android.content.Context
import android.preference.PreferenceManager
import com.magdv.stagehostselector.Constants
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.math.max

class StageHostSelectorInterceptor(
    context: Context,
    defaultHostUrl: String
) : Interceptor {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private var cachedHostUrl: String? = null
    private var cachedHostHttpUrl: HttpUrl? = null
    private val defaultHostUrlSegments = HttpUrl.parse(defaultHostUrl)?.pathSegments()

    override fun intercept(chain: Interceptor.Chain): Response {
        val hostUrl = sharedPreferences.getString(Constants.HOST_URL_STORAGE_KEY, null)
                      ?: return chain.proceed(chain.request())

        if (cachedHostUrl != hostUrl) {
            cachedHostUrl = hostUrl
            cachedHostHttpUrl = HttpUrl.parse(hostUrl) ?: throw Exception("Invalid url: $hostUrl")
        }

        val newRequest = cachedHostHttpUrl?.let { cachedHostHttpUrl ->
            var defaultHostUrlSegmentsCount = defaultHostUrlSegments?.count() ?: 0
            if (defaultHostUrlSegmentsCount == 1 && defaultHostUrlSegments?.get(0) == "") {
                defaultHostUrlSegmentsCount -= 1
            }
            val chainRequestUrlSegmentCount = chain.request().url().pathSegments().count()

            var pathSegments = ""
            val chainRequestPathSegments = chain.request().url().pathSegments()
            for (i in defaultHostUrlSegmentsCount until chainRequestUrlSegmentCount) {
                pathSegments += "${chainRequestPathSegments[i]}/"
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