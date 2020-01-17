package com.magdv.stagehostselector

import android.content.Context
import android.preference.PreferenceManager
import android.view.View
import com.magdv.stagehostselector.StageHostSelector.repository
import com.magdv.stagehostselector.interceptor.StageHostSelectorInterceptor
import com.magdv.stagehostselector.repository.StageHostSelectorRepository
import com.magdv.stagehostselector.repository.StageHostSelectorRepositoryImpl
import com.magdv.stagehostselector.view.StageHostSelectorView
import okhttp3.OkHttpClient

object StageHostSelector {

    var repository: StageHostSelectorRepository? = null

    fun init(context: Context, defaultHostUrl: String? = null, suggestedUrls: Set<String>? = null) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        repository = StageHostSelectorRepositoryImpl.newInstance(preferences, defaultHostUrl, suggestedUrls)
    }

    fun createView(context: Context): View? {
        return repository?.let { StageHostSelectorView(repository!!, context) }
    }
}

fun OkHttpClient.Builder.addStageHostSelectorInterceptor(): OkHttpClient.Builder {
    return if (repository != null) {
        this.addInterceptor(StageHostSelectorInterceptor(repository!!))
    } else {
        this
    }
}