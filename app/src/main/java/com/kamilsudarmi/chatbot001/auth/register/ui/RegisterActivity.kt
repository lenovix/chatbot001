package com.kamilsudarmi.chatbot001.auth.register.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.kamilsudarmi.chatbot001.MainActivity
import com.kamilsudarmi.chatbot001.R
import com.kamilsudarmi.chatbot001.api.ApiClient.apiService
import com.kamilsudarmi.chatbot001.auth.register.model.RegistrationData
import com.kamilsudarmi.chatbot001.auth.register.model.RegistrationResponse
import com.kamilsudarmi.chatbot001.databinding.ActivityLoginBinding
import com.kamilsudarmi.chatbot001.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnRegister.setOnClickListener {
            registerMethod()
        }
    }

    private fun registerMethod() {
        val name = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        // Validasi input kosong
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        } else{
            // Membuat objek RegistrationData
            val registrationData = RegistrationData(name, email, password)

            // Mengirim permintaan registrasi ke API
            val call = apiService.registerUser(registrationData)
            call.enqueue(object : Callback<RegistrationResponse> {
                override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
                    if (response.isSuccessful) {
                        val registrationResponse = response.body()
                        // Tangani respons sukses

                        // Menyimpan status login menggunakan SharedPreferences
                        val sharedPreferences = getSharedPreferences("login_status", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("isLoggedIn", true)
                        editor.apply()

                        // Mengarahkan pengguna ke MainActivity
                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Optional: Mengakhiri activity registrasi agar tidak dapat dikembalikan
                    } else {
                        // Tangani respons gagal
                    }
                }

                override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                    // Tangani kesalahan jaringan atau request
                }
            })
        }
    }
}