package com.kamilsudarmi.chatbot001.auth.login.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kamilsudarmi.chatbot001.MainActivity
import com.kamilsudarmi.chatbot001.R
import com.kamilsudarmi.chatbot001.api.ApiClient
import com.kamilsudarmi.chatbot001.auth.login.model.LoginModel
import com.kamilsudarmi.chatbot001.auth.response.UserResponse
import com.kamilsudarmi.chatbot001.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        loginMethod()
    }

    private fun loginMethod() {
        // Mendapatkan input dari UI
        val username = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        // Membuat objek loginModel
        val loginModel = LoginModel(username, password)

        // Memanggil metode login pada ApiService
        val call = ApiClient.apiService.login(loginModel)
        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    // Proses respon dari server
                    val message = userResponse?.message
                    val user = userResponse?.user
                    // Misalnya, menyimpan token otentikasi atau menampilkan data pengguna
                    if (user != null) {
                        val userId = user.id
                        val username = user.username
                        val email = user.email
                        // Lakukan sesuatu dengan data pengguna yang diterima
                        // Arahkan pengguna ke MainActivity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Selesai dengan LoginActivity agar tidak dapat kembali dengan tombol back
                } else {
                    // Menangani respon tidak berhasil (misalnya, kesalahan server atau data login yang tidak valid)
                    val errorBody = response.errorBody()?.string()
                    // Lakukan sesuatu dengan pesan kesalahan atau respons yang tidak berhasil
                }
            }
        }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

}