package com.kamilsudarmi.chatbot001.api

import com.kamilsudarmi.chatbot001.auth.login.model.LoginRequest
import com.kamilsudarmi.chatbot001.auth.login.model.LoginResponse
import com.kamilsudarmi.chatbot001.auth.register.model.RegistrationData
import com.kamilsudarmi.chatbot001.auth.register.model.RegistrationResponse
import com.kamilsudarmi.chatbot001.requestUnit.RequestUnit
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("/register")
    fun registerUser(@Body registrationData: RegistrationData): Call<RegistrationResponse>

    @POST("/request_unit")
    fun sendRequestUnit(@Body requestUnit: RequestUnit): Call<Unit>
}