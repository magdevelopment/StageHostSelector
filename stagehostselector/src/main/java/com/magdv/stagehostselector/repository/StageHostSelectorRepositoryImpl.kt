package com.magdv.stagehostselector.repository

import android.content.SharedPreferences
import com.magdv.stagehostselector.Constants
import com.magdv.stagehostselector.common.CurrentHostUrlChangeListener

internal class StageHostSelectorRepositoryImpl private constructor(
    private val preferences: SharedPreferences,
    private var defaultHostUrl: String?,
    private var suggestionHostUrls: Set<String>
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

    companion object {

        fun newInstance(preferences: SharedPreferences, defaultHostUrl: String?, suggestionHostUrls: Set<String>?): StageHostSelectorRepositoryImpl {
            return StageHostSelectorRepositoryImpl(preferences, defaultHostUrl, suggestionHostUrls ?: emptySet())
        }
    }


}
