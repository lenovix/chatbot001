package com.kamilsudarmi.chatbot001.chatbot2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.kamilsudarmi.chatbot001.api.naiveBayer.ApiClientNB.apiServiceNB
import com.kamilsudarmi.chatbot001.api.naiveBayer.ApiServiceNB
import com.kamilsudarmi.chatbot001.api.naiveBayer.ChatResponse
import com.kamilsudarmi.chatbot001.api.naiveBayer.UserInput
import com.kamilsudarmi.chatbot001.databinding.ActivityChatbot2Binding
import java.util.Locale
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.Gson
import okhttp3.RequestBody


class Chatbot2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityChatbot2Binding

    val TAG = "testing"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbot2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.buttonSend.setOnClickListener {
//            val userInput = binding.editTextUserInput.text.toString()
//            if (userInput.isNotEmpty()) {
//                val chatbotResponse = getChatbotResponse(userInput)
//                displayChatMessage("Chatbot: $chatbotResponse")
//                binding.editTextUserInput.text.clear()
//            }
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
            }else{
                // Mengirim pesan ke server untuk menganalisis keadaan user
//                analyzeEmergencyInfo(userInput)
            }
        }
    }

    private fun analyzeEmergencyInfo(userInput: String) {
        val gson = Gson()
//        val userInput = binding.editTextUserInput.text.toString()
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
                        displayChatMessage("Chatbot: Prediction: $prediction")
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