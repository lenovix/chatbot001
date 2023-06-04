package com.kamilsudarmi.chatbot001.chatbot.utils

import android.annotation.SuppressLint
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

object Time {
    @SuppressLint("SimpleDateFormat")
    fun timeStamp(): String{
        val timeStamp = Timestamp(System.currentTimeMillis())
        val sdf = SimpleDateFormat("HH:mm")
        val time = sdf.format(Date(timeStamp.time))

        return time.toString()
    }
}