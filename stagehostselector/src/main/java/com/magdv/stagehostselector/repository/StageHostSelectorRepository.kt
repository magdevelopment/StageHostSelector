package com.magdv.stagehostselector.repository

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.magdv.stagehostselector.Constants

internal class StageHostSelectorRepository private constructor(
    private val context: Context
) {

    private var urls: Set<String> = emptySet()
    private var url: String? = null
    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun getCurrentHostUrl(): String? {
        return preferences
            .getString(Constants.HOST_URL_STORAGE_KEY, null)
    }

    fun setCurrentHostUrl(currentHostUrl: String?) {
        preferences.edit()
            .putString(Constants.HOST_URL_STORAGE_KEY, currentHostUrl)
            .apply()
    }

    fun getSuggestionHostUrls(): Set<String> {
        return preferences
                   .getStringSet(Constants.HOST_URL_SUGGESTIONS_STORAGE_KEY, null)
               ?: urls.also {
                   setSuggestionHostUrls(it)
               }
    }

    fun setSuggestionHostUrls(suggestionHostUrls: Set<String>) {
        preferences.edit()
            .putStringSet(Constants.HOST_URL_SUGGESTIONS_STORAGE_KEY, suggestionHostUrls)
            .apply()
    }

    fun addDefaultSuggestionUrls(suggestedUrls: Set<String>) {
        this.urls = suggestedUrls
    }

    fun setDefaultHostUrl(hostUrl: String?) {
        this.url = hostUrl
    }

    fun getDefaultHostUrl(): String? {
        return this.url
    }

    companion object {

        @Volatile private var instance: StageHostSelectorRepository? = null

        fun getInstance(): StageHostSelectorRepository? {
            return instance
        }

        fun newInstance(context: Context): StageHostSelectorRepository {
            instance = StageHostSelectorRepository(context)
            return instance!!
        }
    }
}
