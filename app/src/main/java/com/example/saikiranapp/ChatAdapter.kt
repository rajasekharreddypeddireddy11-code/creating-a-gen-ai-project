package com.example.saikiranapp

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.chatMessage)
        val container: LinearLayout = itemView as LinearLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatMessage = messages[position]
        holder.messageText.text = chatMessage.message

        val params = holder.messageText.layoutParams as LinearLayout.LayoutParams
        if (chatMessage.role == "user") {
            holder.container.gravity = Gravity.END
            holder.messageText.setBackgroundResource(R.drawable.chat_bubble_user_modern)
            params.marginStart = 120
            params.marginEnd = 0
        } else {
            holder.container.gravity = Gravity.START
            holder.messageText.setBackgroundResource(R.drawable.chat_bubble_model_modern)
            params.marginStart = 0
            params.marginEnd = 120
        }
        holder.messageText.layoutParams = params
    }

    override fun getItemCount(): Int = messages.size
}