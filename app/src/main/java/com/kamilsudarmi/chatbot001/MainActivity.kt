package com.kamilsudarmi.chatbot001

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.kamilsudarmi.chatbot001.chatbot.ui.ChatbotActivity

class MainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            val chatbotPage = Intent(this, ChatbotActivity::class.java)
            startActivity(chatbotPage)
        }

    }
}