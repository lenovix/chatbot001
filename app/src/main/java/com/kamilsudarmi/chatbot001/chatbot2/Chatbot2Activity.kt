package com.kamilsudarmi.chatbot001.chatbot2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kamilsudarmi.chatbot001.R
import com.kamilsudarmi.chatbot001.databinding.ActivityChatbot2Binding
import com.kamilsudarmi.chatbot001.databinding.ActivityMainBinding
import java.util.Locale

class Chatbot2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityChatbot2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbot2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.buttonSend.setOnClickListener {
            val userInput = binding.editTextUserInput.text.toString()
            if (userInput.isNotEmpty()) {
                val chatbotResponse = getChatbotResponse(userInput)
                displayChatMessage("You: $userInput")
                displayChatMessage("Chatbot: $chatbotResponse")
                binding.editTextUserInput.text.clear()
            }
        }
    }
    private fun displayChatMessage(message: String) {
        val currentChat = binding.textViewChat.text.toString()
        val newChat = "$currentChat\n$message"
        binding.textViewChat.text = newChat
    }
    private fun getChatbotResponse(input: String): String {
        val lowerCaseInput = input.toLowerCase(Locale.getDefault())

        // Cek apakah pengguna mengatakan "emergency"
        if (lowerCaseInput.contains("emergency")) {
            return "Apakah Anda membutuhkan bantuan darurat? Silakan beri tahu saya keadaannya."
        }

        // Cek apakah pengguna memberikan informasi tentang keadaan darurat
        val isEmergencyInfoProvided = analyzeEmergencyInfo(input)
        if (isEmergencyInfoProvided) {
            // Lakukan prediksi menggunakan model Naive Bayes
            val prediction = predictEmergencyType(input)

            // Berikan respon berdasarkan hasil prediksi
            when (prediction) {
                "Fire" -> return "Pemadam kebakaran telah dihubungi. Silakan amankan diri Anda."
                "Medical" -> return "Ambulans sedang dalam perjalanan. Mohon tetap tenang."
                "Accident" -> return "Tim penyelamat sedang menuju ke lokasi. Harap tunggu sebentar."
                else -> return "Mohon maaf, saya tidak dapat memproses permintaan Anda saat ini."
            }
        }

        // Respon default jika tidak ada kondisi yang terpenuhi
        return "Maaf, saya tidak mengerti. Bisa Anda jelaskan lebih lanjut?"
    }
    private fun analyzeEmergencyInfo(input: String): Boolean {
        // Lakukan analisis menggunakan model Naive Bayes
        // ...

        // Mengembalikan hasil analisis (true atau false)
        return true // Ganti dengan hasil analisis yang sesuai
    }

    private fun predictEmergencyType(input: String): String {
        // Melakukan prediksi menggunakan model Naive Bayes
        // ...

        // Mengembalikan hasil prediksi (misalnya: "Fire", "Medical", "Accident", dll.)
        return "Fire" // Ganti dengan hasil prediksi yang sesuai
    }
}