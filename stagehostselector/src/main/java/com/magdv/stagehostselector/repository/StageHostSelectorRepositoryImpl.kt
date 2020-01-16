package com.magdv.stagehostselector.repository

import android.content.SharedPreferences
import com.magdv.stagehostselector.Constants
import com.magdv.stagehostselector.common.CurrentHostUrlChangeListener
import com.magdv.stagehostselector.common.ObservableCurrentHostUrl

internal class StageHostSelectorRepositoryImpl private constructor(
    private val preferences: SharedPreferences
) : StageHostSelectorRepository {

    private var observable: ObservableCurrentHostUrl? = null

    override fun getCurrentHostUrl(): String? {
        return preferences
            .getString(Constants.HOST_URL_STORAGE_KEY, null)
    }

    override fun setCurrentHostUrl(currentHostUrl: String?) {
        observable?.currentHostUrl = currentHostUrl
        preferences.edit()
            .putString(Constants.HOST_URL_STORAGE_KEY, currentHostUrl)
            .apply()
    }

    override fun getSuggestionHostUrls(): Set<String> {
        return preferences
                   .getStringSet(Constants.HOST_URL_SUGGESTIONS_STORAGE_KEY, null)
               ?: urls.also {
                   setSuggestionHostUrls(it)
               }
    }

    override fun setSuggestionHostUrls(suggestionHostUrls: Set<String>) {
        preferences.edit()
            .putStringSet(Constants.HOST_URL_SUGGESTIONS_STORAGE_KEY, suggestionHostUrls)
            .apply()
    }

    override fun getDefaultHostUrl(): String? {
        return url
    }

    override fun subscribeCurrentHostUrl(listener: CurrentHostUrlChangeListener) {
        observable = ObservableCurrentHostUrl(listener)
        observable?.currentHostUrl = getCurrentHostUrl() ?: getDefaultHostUrl()
    }

    companion object {

        private var instance: StageHostSelectorRepositoryImpl? = null
        private var urls: Set<String> = emptySet()
        private var url: String? = null

        fun newInstance(preferences: SharedPreferences, defaultHostUrl: String?, suggestionHostUrls: Set<String>?): StageHostSelectorRepositoryImpl {
            instance = StageHostSelectorRepositoryImpl(preferences)
            addDefaultSuggestionUrls(suggestionHostUrls)
            setDefaultHostUrl(defaultHostUrl)
            return instance!!
        }

        private fun addDefaultSuggestionUrls(suggestedUrls: Set<String>?) {
            this.urls = suggestedUrls ?: emptySet()
        }

        private fun setDefaultHostUrl(hostUrl: String?) {
            this.url = hostUrl
        }
    }


}
