package com.magdv.stagehostselector.repository

import com.magdv.stagehostselector.common.CurrentHostUrlChangeListener

interface StageHostSelectorRepository {

    fun getCurrentHostUrl(): String?

    fun setCurrentHostUrl(currentHostUrl: String?)

    fun getSuggestionHostUrls(): Set<String>

    fun setSuggestionHostUrls(suggestionHostUrls: Set<String>)

    fun getDefaultHostUrl(): String?

    fun subscribeCurrentHostUrl(listener: CurrentHostUrlChangeListener)
}
