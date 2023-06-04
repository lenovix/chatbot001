package com.example.chatbot.utils

import com.kamilsudarmi.chatbot001.chatbot.utils.Constants.OPEN_GOOGLE
import com.kamilsudarmi.chatbot001.chatbot.utils.Constants.OPEN_SEARCH
import com.kamilsudarmi.chatbot001.chatbot.utils.SolveMath
import com.kamilsudarmi.chatbot001.chatbot.utils.Time
import java.lang.Exception

object BotResponse {
    fun basicResponses(_message: String): String{
        val random = (0..2).random()
        val message = _message.lowercase()

        return when{
            //Hello
            message.contains("hello") ->{
                when (random){
                    0 -> "Hello there!"
                    1 -> "Sup"
                    2 -> "Peu Haba!"
                    else -> "error"
                }
            }

            message.contains("flip") && message.contains("coin") ->{
                var r = (0..1).random()
                val result = if (r == 0) "heads" else "tails"

                "I flipped a coin and it landed on $result"
            }

            // Solve Math
            message.contains("solve") ->{
                val equation:String? = message.substringAfter("solve")

                return try {
                    val answer = SolveMath.solveMath(equation ?: "0")
                    answer.toString()
                }catch (e: Exception){
                    "Sorry, I can't solve that!"
                }
            }

            //How are you
            message.contains("how are you") ->{
                when (random){
                    0 -> "I'm doing fine, thanks for asking"
                    1 -> "I'm hungry"
                    2 -> "Pretty good! how about you?"
                    else -> "error"
                }
            }

            // get the current time
            message.contains("time") && message.contains("?") ->{
                Time.timeStamp()
            }

            // opens google
            message.contains("open") && message.contains("google") ->{
                OPEN_GOOGLE
            }

            // open search
            message.contains("search") ->{
                OPEN_SEARCH
            }

            else ->{
                when (random){
                    0 -> "I don't understand.."
                    1 -> "Idk"
                    2 -> "Try asking you something different!"
                    else -> "error"
                }
            }
        }
    }
}