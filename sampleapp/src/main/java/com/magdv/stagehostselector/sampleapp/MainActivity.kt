package com.magdv.stagehostselector.sampleapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.magdv.stagehostselector.StageHostSelector
import com.magdv.stagehostselector.sampleapp.network.HttpLog
import com.magdv.stagehostselector.sampleapp.network.LoggingInterceptor
import com.magdv.stagehostselector.sampleapp.network.NetworkFactory
import com.magdv.stagehostselector.sampleapp.network.UserApi
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var logs = mutableListOf<HttpLog>()

    private lateinit var adapter: MainAdapter
    private val subscriptions = CompositeDisposable()
    private val loggingInterceptor = LoggingInterceptor()

    private val logCallback: ((HttpLog) -> Unit) = { log ->
        logs = ArrayList(logs)
        logs.add(0, log)
        adapter.submitList(logs)
    }

    private val userApi: UserApi by lazy {
        val client = NetworkFactory.createHttpClient(loggingInterceptor)
        return@lazy NetworkFactory.createUserApi(client)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        initStageHostSelector()
        initList()
        initStageHostSelectorView()
        initFab()
    }

    private fun initStageHostSelector() {
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

    private fun initList() {
        adapter = MainAdapter()

        recyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(context)
            linearLayoutManager.orientation = RecyclerView.VERTICAL
            linearLayoutManager.reverseLayout = false
            linearLayoutManager.stackFromEnd = false

            layoutManager = linearLayoutManager
            adapter = this@MainActivity.adapter

            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun initStageHostSelectorView() {
        appBarLayout.addView(StageHostSelector.createView(this))
    }

    private fun initFab() {
        sendFab.setOnClickListener { doRequest() }
    }

    private fun doRequest() {
        if (Random.nextBoolean()) {
            subscriptions += userApi.login()
                .subscribe(
                    {
                        // Empty
                    },
                    { error ->
                        // TODO
                    }
                )
        } else {
            subscriptions += userApi.logout()
                .subscribe(
                    {
                        // Empty
                    },
                    { error ->
                        // TODO
                    }
                )
        }
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
