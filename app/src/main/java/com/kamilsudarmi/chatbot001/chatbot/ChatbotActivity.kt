package com.kamilsudarmi.chatbot001.chatbot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.kamilsudarmi.chatbot001.api.naiveBayer.ApiClientNB.apiServiceNB
import com.kamilsudarmi.chatbot001.api.naiveBayer.ChatResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.Gson
import com.kamilsudarmi.chatbot001.databinding.ActivityChatbotBinding
import okhttp3.RequestBody


class ChatbotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatbotBinding

    val TAG = "testing"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.buttonSend.setOnClickListener {
            val userInput = binding.editTextUserInput.text.toString().trim()
            if (userInput.isNotEmpty()) {
                displayChatMessage("You: $userInput")
                analyzeUserInput(userInput)
                binding.editTextUserInput.setText("")
            }
        }
    }

    private fun displayChatMessage(message: String) {
        val currentChat = binding.textViewChat.text.toString()
        val newChat = "$currentChat\n$message"
        binding.textViewChat.text = newChat
    }

    var waitingForUserInput: Boolean = false
    private fun analyzeUserInput(userInput: String) {
        if (userInput.lowercase() == "emergency") {
            displayChatMessage("Bot: Ada apa?")
            waitingForUserInput = true
        } else {
            if (waitingForUserInput){
                analyzeEmergencyInfo(userInput)
                waitingForUserInput = false
            }
        }
    }

    private fun analyzeEmergencyInfo(userInput: String) {
        val gson = Gson()
        val requestMap = mapOf("texts" to listOf(userInput))
        val requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json"), gson.toJson(requestMap))

        val call = apiServiceNB.analyzeEmergencyInfo(requestBody)

        call.enqueue(object : Callback<List<ChatResponse>> {
            override fun onResponse(
                call: Call<List<ChatResponse>>,
                response: Response<List<ChatResponse>>
            ) {
                if (response.isSuccessful) {
                    val predictions = response.body()
                    if (!predictions.isNullOrEmpty()) {
                        val prediction = predictions[0].prediction
                        displayChatMessage("Chatbot: Analisis keadaan: $prediction")
                    }
                } else {
                    showToast("Failed to process the request")
                }
            }

            override fun onFailure(call: Call<List<ChatResponse>>, t: Throwable) {
                showToast("An error occurred: ${t.message}")
            }
        })
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}