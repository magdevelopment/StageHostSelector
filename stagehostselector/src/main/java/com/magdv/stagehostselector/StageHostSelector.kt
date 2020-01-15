/*
 * Copyright © 2019 MAG Development, LLC. All rights reserved.
 * Project: MAG.Express
 * Date: 22.07.2019
 * Time: 21:58
 * Author: Dmitriy Orteney <d.orteney at magdv.com>
 */

package com.magdv.stagehostselector

import android.content.Context
import android.view.View
import com.magdv.stagehostselector.interceptor.StageHostSelectorInterceptor
import com.magdv.stagehostselector.repository.StageHostSelectorRepository
import com.magdv.stagehostselector.view.StageHostSelectorView
import okhttp3.OkHttpClient

object StageHostSelector {

    fun init(context: Context, defaultHostUrl: String? = null, suggestedUrls: Set<String>? = null) {
        val repository = StageHostSelectorRepository.newInstance(context)
        repository.setDefaultHostUrl(defaultHostUrl)
        if (suggestedUrls != null) {
            repository.addDefaultSuggestionUrls(suggestedUrls)
        }
    }

    fun createView(context: Context): View {
        return StageHostSelectorView(context)
    }
}

fun OkHttpClient.Builder.addStageHostSelectorInterceptor(): OkHttpClient.Builder {
    return if (StageHostSelectorRepository.getInstance() != null) {
        this.addInterceptor(StageHostSelectorInterceptor())
    } else {
        this
    }
}