package com.magdv.stagehostselector.interceptor

import android.content.Context
import android.preference.PreferenceManager
import com.magdv.stagehostselector.Constants
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class StageHostSelectorInterceptor(
    context: Context,
    defaultHostUrl: String
) : Interceptor {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private var cachedHostUrl: String? = null
    private var cachedHostHttpUrl: HttpUrl? = null
    private val defaultHostUrlSegments = HttpUrl.parse(defaultHostUrl)?.pathSegments() ?: mutableListOf()

    override fun intercept(chain: Interceptor.Chain): Response {
        val hostUrl = sharedPreferences.getString(Constants.HOST_URL_STORAGE_KEY, null)
                      ?: return chain.proceed(chain.request())

        if (cachedHostUrl != hostUrl) {
            cachedHostUrl = hostUrl
            cachedHostHttpUrl = HttpUrl.parse(hostUrl) ?: throw Exception("Invalid url: $hostUrl")
        }

        val newRequest = cachedHostHttpUrl?.let { cachedHostHttpUrl ->
            var defaultHostUrlSegmentsCount = defaultHostUrlSegments.count()
            if (defaultHostUrlSegments[defaultHostUrlSegmentsCount - 1] == "") {
                defaultHostUrlSegmentsCount -= 1
            }
            val chainRequestPathSegments = chain.request().url().pathSegments()

            var isPassedDefaultSegmentPath = false
            for (i in 0 until defaultHostUrlSegmentsCount) {
                if (defaultHostUrlSegments[i] != chainRequestPathSegments[i]) {
                    isPassedDefaultSegmentPath = true
                    break
                }
            }

            var pathSegments = ""
            val chainRequestUrlSegmentCount = chainRequestPathSegments.count()
            val startSegment = if (isPassedDefaultSegmentPath) 0 else defaultHostUrlSegmentsCount
            for (i in startSegment until chainRequestUrlSegmentCount) {
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