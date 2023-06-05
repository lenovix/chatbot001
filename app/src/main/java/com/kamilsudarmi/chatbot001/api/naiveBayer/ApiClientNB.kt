package com.kamilsudarmi.chatbot001.api.naiveBayer

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClientNB {
    private const val BASE_URL = "http://192.168.1.5:5000" // Ganti dengan URL server Anda

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiServiceNB: ApiServiceNB by lazy {
        retrofit.create(ApiServiceNB::class.java)
    }
}