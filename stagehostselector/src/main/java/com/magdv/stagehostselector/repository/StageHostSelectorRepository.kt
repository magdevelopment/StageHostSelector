package com.magdv.stagehostselector.repository

import android.content.SharedPreferences
import com.magdv.stagehostselector.Constants

internal class StageHostSelectorRepository private constructor(
    private val preferences: SharedPreferences
) {

    private var urls: Set<String> = emptySet()

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

    companion object {

        @Volatile private var instance: StageHostSelectorRepository? = null

        fun getInstance(preferences: SharedPreferences): StageHostSelectorRepository {
            return when {
                instance != null -> instance!!
                else -> synchronized(this) {
                    if (instance == null) instance = StageHostSelectorRepository(preferences)
                    instance!!
                }
            }
        }
    }
}
