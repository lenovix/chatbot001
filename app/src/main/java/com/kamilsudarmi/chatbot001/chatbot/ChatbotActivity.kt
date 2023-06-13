package com.kamilsudarmi.chatbot001.chatbot

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.kamilsudarmi.chatbot001.api.naiveBayer.ApiClientNB.apiServiceNB
import com.kamilsudarmi.chatbot001.api.naiveBayer.ChatResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.Gson
import com.kamilsudarmi.chatbot001.Constant
import com.kamilsudarmi.chatbot001.api.ApiService
import com.kamilsudarmi.chatbot001.api.naiveBayer.UserInput
import com.kamilsudarmi.chatbot001.databinding.ActivityChatbotBinding
import com.kamilsudarmi.chatbot001.requestUnit.RequestUnit
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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

    var unitEmergency: String = ""
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
                        unitEmergency = prediction
                        Log.d("Unit1", unitEmergency)
                        val sharedPreferences = getSharedPreferences("login_status", Context.MODE_PRIVATE)
                        val user_id = sharedPreferences.getString("user_id", "")
                        val sharedPreferencesAdress = getSharedPreferences("address", Context.MODE_PRIVATE)
                        val user_address = sharedPreferencesAdress.getString("address", "").toString()
                        Log.d("address", "onCreate: $user_address")
                        //Log.d("latlong", "onCreate: $latLongUser")

                        // Mengirim data ke server
                        val requestUnit = RequestUnit(
                            user_id = user_id, // Ganti dengan nilai user ID yang sesuai
                            address = user_address, // Ganti dengan alamat pengguna yang sesuai
                            situation = userInput, // Ganti dengan situasi yang sesuai
                            unit = unitEmergency, // Ganti dengan unit yang dipilih oleh pengguna
                            status = "Pending" // Ganti dengan status yang sesuai
                        )

                        val retrofit = Retrofit.Builder()
                            .baseUrl(Constant.BASE_URL_NODE) // Ganti dengan base URL dari REST API Anda
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()

                        val apiService = retrofit.create(ApiService::class.java)

                        apiService.sendRequestUnit(requestUnit).enqueue(object : Callback<Unit> {
                            override fun onResponse(
                                call: Call<Unit>,
                                response: Response<Unit>
                            ) {
                                Log.d("check", "onResponse: ${response.code()}")
                                if (response.isSuccessful) {
                                    Log.d("kirim", "onResponse: data berhasil dikirim")
                                    val message = "data berhasil dikirim"
                                    displayChatMessage(message)
                                } else {
                                    Log.d("gagal", "onResponse: data tidak berhasil dikirim")
                                    val message = "data tidak berhasil dikirim"
                                    displayChatMessage(message)
                                }
                            }

                            override fun onFailure(call: Call<Unit>, t: Throwable) {
                                displayChatMessage("Failed to send request")
                            }
                        })
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