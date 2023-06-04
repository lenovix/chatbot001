package com.kamilsudarmi.chatbot001

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kamilsudarmi.chatbot001.chatbot.ui.ChatbotActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity(){
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var imageView: ImageView
    private val imageArray = arrayOf(
        R.drawable.img1,
        R.drawable.img2,
        R.drawable.img3
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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

        imageView = findViewById(R.id.img_tipsHealty)
        displayRandomImage()

        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            val chatbotPage = Intent(this, ChatbotActivity::class.java)
            startActivity(chatbotPage)
        }
    }

    private fun displayRandomImage() {
        val randomIndex = Random.nextInt(imageArray.size)
        val randomImage = imageArray[randomIndex]
        imageView.setImageResource(randomImage)
        Log.d("random image", "displayRandomImage: $randomIndex")
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