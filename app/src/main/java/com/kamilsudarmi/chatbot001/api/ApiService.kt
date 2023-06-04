package com.kamilsudarmi.chatbot001.api

import com.kamilsudarmi.chatbot001.auth.login.model.LoginRequest
import com.kamilsudarmi.chatbot001.auth.login.model.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}