package com.magdv.stagehostselector.sampleapp.network

import io.reactivex.Completable
import retrofit2.http.GET

interface UserApi {

    @GET("user/login")
    fun login(): Completable

    @GET("/logout")
    fun logout(): Completable
}