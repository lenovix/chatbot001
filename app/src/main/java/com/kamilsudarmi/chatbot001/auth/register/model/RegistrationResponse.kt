package com.kamilsudarmi.chatbot001.auth.register.model

data class RegistrationResponse(
    val message: String,
    val user: User
)

data class User(
    val user_id: String,
    val name: String,
    val email: String,
    val password: String
)
