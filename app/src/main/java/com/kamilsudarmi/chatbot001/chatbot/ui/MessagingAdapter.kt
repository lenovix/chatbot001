package com.kamilsudarmi.chatbot001.chatbot.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbot.data.Message
import com.example.chatbot.utils.Constants.RECEIVE_ID
import com.example.chatbot.utils.Constants.SEND_ID
import com.kamilsudarmi.chatbot001.R

class MessagingAdapter: RecyclerView.Adapter<MessagingAdapter.MessagingViewHolder>() {

    val messageList = mutableListOf<Message>()
    inner class MessagingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                messageList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagingViewHolder {
        return MessagingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false))
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: MessagingViewHolder, position: Int) {
        val currentMessage = messageList[position]

        when (currentMessage.id){
            SEND_ID -> {
                holder.itemView.findViewById<TextView>(R.id.tv_message).apply {
                    text = currentMessage.message
                    visibility = View.VISIBLE
                }
                holder.itemView.findViewById<TextView>(R.id.tv_bot_message).visibility = View.GONE
            }

            RECEIVE_ID -> {
                holder.itemView.findViewById<TextView>(R.id.tv_bot_message).apply {
                    text = currentMessage.message
                    visibility = View.VISIBLE
                }
                holder.itemView.findViewById<TextView>(R.id.tv_message).visibility = View.GONE
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun insertMessage(message: Message){
        this.messageList.add(message)
        notifyItemInserted(messageList.size)
        notifyDataSetChanged()
    }
}