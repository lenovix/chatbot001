package com.kamilsudarmi.chatbot001.api

import com.kamilsudarmi.chatbot001.auth.login.model.LoginModel
import com.kamilsudarmi.chatbot001.auth.response.UserResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("/login")
    fun login(@Body loginModel: LoginModel): Call<UserResponse>
}