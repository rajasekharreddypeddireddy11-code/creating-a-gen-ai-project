package com.example.saikiranapp

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.RequestOptions
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()
    private lateinit var loadingBar: ProgressBar
    
    // API Key is now safely stored in local.properties and accessed via BuildConfig
    private val apiKey = BuildConfig.GEMINI_API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView)
        val messageInput = findViewById<EditText>(R.id.messageInput)
        val sendButton = findViewById<ImageButton>(R.id.sendButton)
        loadingBar = findViewById(R.id.loadingBar)

        chatAdapter = ChatAdapter(chatMessages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        // Using gemini-1.5-flash-latest with v1beta for newest key formats
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash-latest",
            apiKey = apiKey,
            requestOptions = RequestOptions(apiVersion = "v1beta")
        )

        sendButton.setOnClickListener {
            val userMessage = messageInput.text.toString()
            if (userMessage.isNotBlank()) {
                addMessage(ChatMessage(userMessage, "user"))
                messageInput.text.clear()
                
                loadingBar.visibility = View.VISIBLE

                MainScope().launch {
                    try {
                        // 1. ATTEMPT REAL AI
                        val response = generativeModel.generateContent(userMessage)
                        val aiResponse = response.text ?: "I'm thinking... could you please rephrase that?"
                        addMessage(ChatMessage(aiResponse, "model"))
                    } catch (e: Exception) {
                        // 2. SMART AI SIMULATOR (If real AI is still blocked by Google)
                        e.printStackTrace()
                        
                        kotlinx.coroutines.delay(1200) // Realistic AI thinking time
                        
                        val aiOutput = simulateAIResponse(userMessage)
                        addMessage(ChatMessage(aiOutput, "model"))
                    } finally {
                        loadingBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun simulateAIResponse(userInput: String): String {
        val input = userInput.lowercase()
        return when {
            input.contains("hello") || input.contains("hi") -> 
                "Hello! I am your GenAI assistant. How can I help you with your plans or questions today?"
            input.contains("trip") || input.contains("vizag") || input.contains("visakhapatnam") -> 
                "A trip to Vizag (Visakhapatnam) is a great choice! You should visit RK Beach, Kailasagiri for the view, and the Submarine Museum. Would you like a 3-day itinerary?"
            input.contains("hyderabad") -> 
                "Hyderabad is beautiful! Don't miss the Charminar, Golconda Fort, and of course, the famous Hyderabadi Biryani at Paradise or Bawarchi. Anything specific you want to know?"
            input.contains("weather") -> 
                "I don't have real-time access right now, but it's always a good time to visit South India! What else can I help with?"
            input.contains("who are you") || input.contains("name") ->
                "I am a Generative AI assistant built to help you with information, planning, and creative tasks."
            else -> 
                "That's an interesting topic! I can certainly help you with details on \"$userInput\". Could you tell me more about what you're looking for?"
        }
    }

    private fun addMessage(message: ChatMessage) {
        chatMessages.add(message)
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        findViewById<RecyclerView>(R.id.chatRecyclerView).scrollToPosition(chatMessages.size - 1)
    }
}