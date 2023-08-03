package com.kamilsudarmi.chatbot001.chatbot

import android.content.Context
import android.content.Intent
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
import com.kamilsudarmi.chatbot001.MainActivity
import com.kamilsudarmi.chatbot001.api.ApiService
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

        welcomeInformation()

        binding.buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.buttonSend.setOnClickListener {
            val userInput = binding.editTextUserInput.text.toString().trim()
            if (userInput.isNotEmpty()) {
                displayChatMessage("YOU: $userInput")
                analyzeUserInput(userInput)
                binding.editTextUserInput.setText("")
            }
        }
    }
    private fun welcomeInformation(){
        displayChatMessage("INFORMATION:")
        displayChatMessage("To use the unit analysis feature, you can type:")
        displayChatMessage("e/emer/emergency")
    }

    private fun displayChatMessage(message: String) {
        val currentChat = binding.textViewChat.text.toString()
        val newChat = "$currentChat\n$message"
        binding.textViewChat.text = newChat
    }

    var waitingForUserInput: Boolean = false
    var waitingForUserInputAddress: Boolean = false
    var addressUser: String = ""
    var emergencyUser = ""
    private fun analyzeUserInput(userInput: String) {
        if (userInput.lowercase() == "emergency" || userInput.lowercase() == "emer" || userInput.lowercase() == "e") {
            displayChatMessage("BOT: What has happened?")
            waitingForUserInput = true
        } else {
            if (waitingForUserInput) {
                emergencyUser = userInput
                displayChatMessage("Bot: Where?")
                waitingForUserInput = false
                waitingForUserInputAddress = true
            }else{
                if (waitingForUserInputAddress){
                    addressUser = userInput
                    waitingForUserInputAddress = false
                    analyzeEmergencyInfo(emergencyUser, addressUser)
                }
            }
        }
    }

    var unitEmergency: String = ""
    
    private fun analyzeEmergencyInfo(userInput: String, userAddress: String) {
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
                        displayChatMessage("Ok, Report will be forwarded to unit $prediction")
                        unitEmergency = prediction
                        Log.d("Unit1", unitEmergency)
                        val sharedPreferences = getSharedPreferences("login_status", Context.MODE_PRIVATE)
                        val userId = sharedPreferences.getString("user_id", "")

                        val latLongUser = ""
                        val requestUnit = RequestUnit(
                            user_id = userId,
                            address = userAddress,
                            latlong = latLongUser,
                            situation = userInput,
                            unit = unitEmergency,
                            status = "Pending"
                        )

                        val retrofit = Retrofit.Builder()
                            .baseUrl(Constant.BASE_URL_NODE)
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
                                    Log.d("kirim", "onResponse: data sent successfully")
                                    Toast.makeText(this@ChatbotActivity, "data sent successfully", Toast.LENGTH_SHORT).show()
                                } else {
                                    Log.d("gagal", "onResponse: data was not sent successfully")
                                    Toast.makeText(this@ChatbotActivity, "data was not sent successfully", Toast.LENGTH_SHORT).show()
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