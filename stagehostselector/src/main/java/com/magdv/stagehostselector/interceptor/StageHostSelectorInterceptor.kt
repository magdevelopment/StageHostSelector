package com.magdv.stagehostselector.interceptor

import android.content.Context
import android.preference.PreferenceManager
import com.magdv.stagehostselector.Constants
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class StageHostSelectorInterceptor(
    context: Context,
    private val defaultHostUrl: String? = null
) : Interceptor {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private var cachedHostUrl: String? = null
    private var cachedHostHttpUrl: HttpUrl? = null
    private val defaultHostUrlSegments: List<String> by lazy {
        defaultHostUrl?.let { HttpUrl.parse(it) }
            ?.pathSegments()
            ?.filter { it.isNotEmpty() }
            ?: listOf()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val hostUrl = sharedPreferences.getString(Constants.HOST_URL_STORAGE_KEY, null)
            ?: return chain.proceed(chain.request())

        if (cachedHostUrl != hostUrl) {
            cachedHostUrl = hostUrl
            cachedHostHttpUrl = HttpUrl.parse(hostUrl) ?: throw Exception("Invalid url: $hostUrl")
        }

        val newRequest = cachedHostHttpUrl?.let { cachedHostHttpUrl ->
            val chainRequestPathSegments = chain.request().url().pathSegments()

            val newUrl = if (shouldReplacePathSegments(defaultHostUrlSegments, chainRequestPathSegments)) {
                cachedHostHttpUrl.addPathSegments(
                    chainRequestPathSegments.subList(
                        fromIndex = defaultHostUrlSegments.count(),
                        toIndex = chainRequestPathSegments.count()
                    )
                )
            } else {
                chain.request().url().replaceBaseUrl(cachedHostHttpUrl)
            }

            return@let chain.request().newBuilder()
                .url(newUrl)
                .build()
        }

        return chain.proceed(newRequest ?: chain.request())
    }

    private fun shouldReplacePathSegments(
        initialPathSegments: List<String>,
        requestPathSegments: List<String>
    ): Boolean {
        initialPathSegments.forEachIndexed { index, segment ->
            if (requestPathSegments[index] != segment) {
                return false;
            }
        }

        return true;
    }

    private fun HttpUrl.addPathSegments(pathSegments: List<String>): HttpUrl {
        return newBuilder()
            .addPathSegments(pathSegments.joinToString("/") { it })
            .build()
    }

    private fun HttpUrl.replaceBaseUrl(newBaseUrl: HttpUrl): HttpUrl {
        return newBuilder()
            .scheme(newBaseUrl.scheme())
            .host(newBaseUrl.host())
            .port(newBaseUrl.port())
            .build()
    }
}