package com.kamilsudarmi.chatbot001.chatbot

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kamilsudarmi.chatbot001.api.naiveBayer.ApiClientNB.apiServiceNB
import com.kamilsudarmi.chatbot001.api.naiveBayer.ChatResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.Gson
import com.kamilsudarmi.chatbot001.Constant
import com.kamilsudarmi.chatbot001.MainActivity
import com.kamilsudarmi.chatbot001.R
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

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        permissionCheck()

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
    var addressUser: String = "address"
    var latLongUser: String = "address"
    private fun analyzeEmergencyInfo(userInput: String) {
        val gson = Gson()
        val requestMap = mapOf("texts" to listOf(userInput))
        val requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json"), gson.toJson(requestMap))

        getLastKnownLocation()
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
                        //Log.d("address", "onCreate: $addressUser")
                        //Log.d("latlong", "onCreate: $latLongUser")

                        // Mengirim data ke server
                        val requestUnit = RequestUnit(
                            user_id = user_id, // Ganti dengan nilai user ID yang sesuai
                            address = "kamil", // Ganti dengan alamat pengguna yang sesuai
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

    private fun permissionCheck() {
        // Meminta izin jika belum diberikan
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                ChatbotActivity.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        } else {
            if (checkLocationPermission()) {
                getLastKnownLocation()
            } else {
                // Jika izin tidak diberikan, minta izin
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    ChatbotActivity.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )
            }
        }
    }
    private fun checkLocationPermission(): Boolean {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val granted = PackageManager.PERMISSION_GRANTED
        return ContextCompat.checkSelfPermission(this, permission) == granted
    }

    private fun getLastKnownLocation() {
        val locationTextView = findViewById<TextView>(R.id.locationTextView)
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude

                        val geocoder = Geocoder(this)
                        val addresses: List<Address> =
                            geocoder.getFromLocation(latitude, longitude, 1) as List<Address>

                        val address = addresses[0].getAddressLine(0)
                        val city = addresses[0].locality
                        val country = addresses[0].countryName

                        locationTextView.text = "$latitude, $longitude\n$address, $city, $country"
                        addressUser = "$address, $city, $country"
                        latLongUser = "$latitude, $longitude"
                    }
                }
        } else {
            // Jika izin tidak diberikan, minta izin
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                ChatbotActivity.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }
    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }
}