package com.kamilsudarmi.chatbot001

import android.Manifest
import android.content.Context
import android.content.Intent
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
import com.kamilsudarmi.chatbot001.Constant.BASE_URL_NODE
import com.kamilsudarmi.chatbot001.api.ApiService
import com.kamilsudarmi.chatbot001.auth.login.ui.LoginActivity
import com.kamilsudarmi.chatbot001.chatbot.ChatbotActivity
import com.kamilsudarmi.chatbot001.databinding.ActivityMainBinding
import com.kamilsudarmi.chatbot001.requestUnit.RequestUnit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random

class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding

    private val imageArray = arrayOf(
        R.drawable.img1,
        R.drawable.img2,
        R.drawable.img3
    )

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        checkLoginStatus()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        permissionCheck()

        displayRandomImage()

        binding.fabChatbot.setOnClickListener {
            val chatbotPage = Intent(this, ChatbotActivity::class.java)
            startActivity(chatbotPage)
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }

        binding.btnCallAmbulance.setOnClickListener {
            val sharedPreferences = getSharedPreferences("login_status", Context.MODE_PRIVATE)
            val user_id = sharedPreferences.getString("user_id", "3")
            Log.d("address", "onCreate: $addressUser")
            Log.d("latlong", "onCreate: $latLongUser")
            val requestUnit = RequestUnit(
                user_id = user_id, // Ganti dengan nilai user ID yang sesuai
                address = addressUser, // Ganti dengan alamat pengguna yang sesuai
                situation = "Emergency", // Ganti dengan situasi yang sesuai
                unit = "Ambulance", // Ganti dengan unit yang dipilih oleh pengguna
                status = "Pending" // Ganti dengan status yang sesuai
            )
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL_NODE) // Ganti dengan base URL dari REST API Anda
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiService::class.java)

            apiService.sendRequestUnit(requestUnit).enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    Log.d("check", "onResponse: ${response.code()}")
                    if (response.isSuccessful) {
                        Log.d("kirim", "onResponse: data berhasil dikirim")
                    } else {
                        Log.d("gagal", "onResponse: data tidak berhasil dikirim")
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d("gagal", "onResponse: $t")
                }
            })


        }
    }

    private fun logout() {
        // Hapus status login dari SharedPreferences
        val sharedPreferences = getSharedPreferences("login_status", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

        // Arahkan pengguna kembali ke LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Optional: Tutup MainActivity agar pengguna tidak dapat kembali ke sini setelah logout
    }


    private fun checkLoginStatus() {
        val sharedPreferences = getSharedPreferences("login_status", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val userName = sharedPreferences.getString("userName", "user")

        if (!isLoggedIn) {
            // Jika pengguna belum pernah login, arahkan ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Optional: Tutup MainActivity agar pengguna tidak dapat kembali ke sini tanpa login
        } else {
            val welcomeMessage = "Welcome, $userName!"
            binding.tvWelcome.text = welcomeMessage
        }
    }

    private fun permissionCheck() {
        // Meminta izin jika belum diberikan
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        } else {
            if (checkLocationPermission()) {
                getLastKnownLocation()
            } else {
                // Jika izin tidak diberikan, minta izin
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )
            }
        }
    }

    private fun displayRandomImage() {
        val randomIndex = Random.nextInt(imageArray.size)
        val randomImage = imageArray[randomIndex]
        binding.imgTipsHealty.setImageResource(randomImage)
        Log.d("random image", "displayRandomImage: $randomIndex")
    }

    private fun checkLocationPermission(): Boolean {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val granted = PackageManager.PERMISSION_GRANTED
        return ContextCompat.checkSelfPermission(this, permission) == granted
    }

    var addressUser: String = "address"
    var latLongUser: String = "address"
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
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation()
            } else {
                // Jika izin ditolak, berikan penanganan yang sesuai, misalnya menampilkan pesan kepada pengguna
            }
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }


}