package com.magdv.stagehostselector.repository

import android.content.SharedPreferences
import com.magdv.stagehostselector.Constants
import com.magdv.stagehostselector.common.CurrentHostUrlChangeListener

internal class StageHostSelectorRepositoryImpl(
    private val preferences: SharedPreferences,
    private val defaultHostUrl: String?,
    private val suggestionHostUrls: Set<String>
) : StageHostSelectorRepository {

    private var listener: CurrentHostUrlChangeListener? = null

    override fun getCurrentHostUrl(): String? {
        return preferences
            .getString(Constants.HOST_URL_STORAGE_KEY, null)
    }

    override fun setCurrentHostUrl(currentHostUrl: String?) {
        listener?.onCurrentHostUrlChanged(currentHostUrl)
        preferences.edit()
            .putString(Constants.HOST_URL_STORAGE_KEY, currentHostUrl)
            .apply()
    }

    override fun getSuggestionHostUrls(): Set<String> {
        return preferences
                   .getStringSet(Constants.HOST_URL_SUGGESTIONS_STORAGE_KEY, null)
               ?: suggestionHostUrls.also {
                   setSuggestionHostUrls(it)
               }
    }

    override fun setSuggestionHostUrls(suggestionHostUrls: Set<String>) {
        preferences.edit()
            .putStringSet(Constants.HOST_URL_SUGGESTIONS_STORAGE_KEY, suggestionHostUrls)
            .apply()
    }

    override fun getDefaultHostUrl(): String? {
        return defaultHostUrl
    }

    override fun subscribeCurrentHostUrl(listener: CurrentHostUrlChangeListener) {
        this.listener = listener
        listener.onCurrentHostUrlChanged(getCurrentHostUrl() ?: getDefaultHostUrl())
    }
}
