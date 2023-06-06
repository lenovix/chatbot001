package com.kamilsudarmi.chatbot001.api.naiveBayer

import com.kamilsudarmi.chatbot001.Constant.BASE_URL_FLASK
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClientNB {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_FLASK)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiServiceNB: ApiServiceNB by lazy {
        retrofit.create(ApiServiceNB::class.java)
    }
}