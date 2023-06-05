package com.kamilsudarmi.chatbot001.api.naiveBayer

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiServiceNB {
    @Headers("Content-Type: application/json")
    @POST("/predict")
    fun analyzeEmergencyInfo(@Body request: RequestBody): Call<List<ChatResponse>>
}
