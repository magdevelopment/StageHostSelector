package com.magdv.stagehostselector.sampleapp

import android.app.Application
import com.magdv.stagehostselector.StageHostSelector

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        StageHostSelector.init(
            this,
            BuildConfig.API_ENDPOINT,
            setOf(
                "http://example.com/alternative/",
                "http://172.21.19.123:3500/",
                "http://example.com/alternative/first",
                "http://example.com:8080/alternative/first"
            )
        )
    }
}