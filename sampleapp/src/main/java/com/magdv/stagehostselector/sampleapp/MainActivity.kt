package com.magdv.stagehostselector.sampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.magdv.stagehostselector.sampleapp.network.LoggingInterceptor
import com.magdv.stagehostselector.sampleapp.network.NetworkFactory
import com.magdv.stagehostselector.sampleapp.network.UserApi
import com.magdv.stagehostselector.view.StageHostSelectorView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var logs = mutableListOf<String>()

    private lateinit var adapter: MainAdapter
    private val subscriptions = CompositeDisposable()
    private val loggingInterceptor = LoggingInterceptor()

    private val logCallback: ((String) -> Unit) = { log ->
        logs = ArrayList(logs)
        logs.add(log)
        adapter.submitList(logs)
    }

    private val userApi: UserApi by lazy {
        val client = NetworkFactory.createHttpClient(this, loggingInterceptor)
        return@lazy NetworkFactory.createUserApi(client)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        initList()
        initStageHostSelectorView()
        initFab()
    }

    private fun initList() {
        adapter = MainAdapter()

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = this@MainActivity.adapter
        }
    }

    private fun initStageHostSelectorView() {
        val view = StageHostSelectorView(this)
        view.defaultHostUrl = BuildConfig.API_ENDPOINT
        appBarLayout.addView(view)
    }

    private fun initFab() {
        sendFab.setOnClickListener { requestLogin() }
    }

    private fun requestLogin() {
        subscriptions += userApi.login()
            .subscribe(
                {
                    // Empty
                },
                { error ->
                    // TODO
                }
            )
    }

    override fun onResume() {
        loggingInterceptor.callback = logCallback
        super.onResume()
    }

    override fun onPause() {
        loggingInterceptor.callback = null
        super.onPause()
    }

    override fun onStop() {
        subscriptions.dispose()
        super.onStop()
    }
}
