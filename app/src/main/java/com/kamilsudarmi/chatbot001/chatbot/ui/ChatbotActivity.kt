package com.kamilsudarmi.chatbot001.chatbot.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatbot.data.Message
import com.example.chatbot.utils.BotResponse
import com.example.chatbot.utils.Constants
import com.example.chatbot.utils.Time
import com.kamilsudarmi.chatbot001.R
import com.kamilsudarmi.chatbot001.databinding.ActivityChatbotBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatbotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatbotBinding

    private lateinit var adapter: MessagingAdapter
    private val botList = listOf("peter", "Francesca", "Luigi", "Igor")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        recyclerView()

        clickEvents()

        val random = (0..3).random()
        customMessage("Hello! today you're speaking with ${botList[random]}, how may i help?")
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun clickEvents(){
        binding.btnSend.setOnClickListener{
            sendMessage()
        }
        binding.etMessage.setOnClickListener{
            GlobalScope.launch {
                delay(1000)
                withContext(Dispatchers.Main){
                    binding.rvMessages.scrollToPosition(adapter.itemCount -1)

                }
            }
        }
    }

    private fun recyclerView(){
        adapter = MessagingAdapter()
        binding.rvMessages.adapter = adapter
        binding.rvMessages.layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun sendMessage(){
        val message = binding.etMessage.text.toString()
        val timeStamp = Time.timeStamp()

        if (message.isNotEmpty()){
            binding.etMessage.setText("")

            adapter.insertMessage(Message(message, Constants.SEND_ID, timeStamp))
            binding.rvMessages.scrollToPosition(adapter.itemCount -1)

            botResponse(message)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun botResponse(message: String){
        val timeStamp = Time.timeStamp()

        GlobalScope.launch {
            delay(1000)

            withContext(Dispatchers.Main){
                val response = BotResponse.basicResponses(message)

                adapter.insertMessage(Message(response, Constants.RECEIVE_ID, timeStamp))
                binding.rvMessages.scrollToPosition(adapter.itemCount - 1)

                when(response){
                    Constants.OPEN_GOOGLE -> {
                        val site = Intent(Intent.ACTION_VIEW)
                        site.data = Uri.parse("https://www.google.com/")
                        startActivity(site)
                    }
                    Constants.OPEN_SEARCH -> {
                        val site = Intent(Intent.ACTION_VIEW)
                        val searchTerm: String? = message.substringAfter("search")
                        site.data = Uri.parse("https://www.google.com/search?&q=${searchTerm}")
                        startActivity(site)
                    }
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onStart() {
        super.onStart()

        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main){
                binding.rvMessages.scrollToPosition(adapter.itemCount -1)

            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun customMessage(message: String){
        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main){
                val timeStamp = Time.timeStamp()
                adapter.insertMessage(Message(message, Constants.RECEIVE_ID, timeStamp))

                binding.rvMessages.scrollToPosition(adapter.itemCount-1)

            }
        }
    }
}