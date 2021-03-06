package com.magdv.stagehostselector.sampleapp.network

import com.magdv.stagehostselector.addStageHostSelectorInterceptor
import com.magdv.stagehostselector.sampleapp.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object NetworkFactory {

    fun createHttpClient(loggingInterceptor: LoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addStageHostSelectorInterceptor()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    fun createUserApi(client: OkHttpClient): UserApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()

        return retrofit.create(UserApi::class.java)
    }
}