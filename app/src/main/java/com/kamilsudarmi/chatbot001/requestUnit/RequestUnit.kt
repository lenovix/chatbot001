package com.kamilsudarmi.chatbot001.requestUnit

data class RequestUnit(
    val user_id: String?,
    val address: String,
    val situation: String,
    val unit: String,
    val status: String
)
