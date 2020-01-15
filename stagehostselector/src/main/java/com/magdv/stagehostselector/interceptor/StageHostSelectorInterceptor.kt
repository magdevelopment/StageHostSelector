package com.magdv.stagehostselector.interceptor

import com.magdv.stagehostselector.repository.StageHostSelectorRepository
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class StageHostSelectorInterceptor : Interceptor {

    private var cachedHostUrl: String? = null
    private var cachedHostHttpUrl: HttpUrl? = null
    private val repository = StageHostSelectorRepository.getInstance()
    private val defaultHostUrlSegments: List<String> by lazy {
        repository?.getDefaultHostUrl()?.let { HttpUrl.parse(it) }
            ?.pathSegments()
            ?.filter { it.isNotEmpty() }
        ?: listOf()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        if (repository == null) return chain.proceed(chain.request())

        val hostUrl = repository.getCurrentHostUrl()
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
                return false
            }
        }

        return true
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