package com.magdv.stagehostselector.sampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.magdv.stagehostselector.view.StageHostSelectorView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        val view = StageHostSelectorView(this)
        view.defaultHostUrl = BuildConfig.API_ENDPOINT
        rootLayout.addView(view)
    }
}
