package com.magdv.stagehostselector.sampleapp.network

import android.content.Context
import com.magdv.stagehostselector.interceptor.StageHostSelectorInterceptor
import com.magdv.stagehostselector.sampleapp.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object NetworkFactory {

    fun createHttpClient(context: Context, loggingInterceptor: LoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(StageHostSelectorInterceptor(context, BuildConfig.API_ENDPOINT))
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