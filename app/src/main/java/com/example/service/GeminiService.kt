package com.example.service

import android.util.Log
import com.example.data.model.Message
import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>? = null
)

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiService {
    private const val TAG = "GeminiService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val api: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApi::class.java)
    }

    /**
     * Checks if a valid API Key is present in the BuildConfig secrets.
     */
    fun hasValidApiKey(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return !key.isNullOrEmpty() && key != "MY_GEMINI_API_KEY" && key != "placeholder"
    }

    /**
     * Moderates text input. Returns a pair of (isSafe, reason).
     */
    suspend fun moderateMessage(text: String): Pair<Boolean, String> {
        if (!hasValidApiKey()) {
            return Pair(true, "API Key not configured. Moderation assumed safe.")
        }

        val prompt = "Analyze this message: \"$text\". Does it violate safety rules (hate speech, cyberbullying, sexual content, severe slurs, threats)? Start your response with SAFE or UNSAFE."
        val systemInstruction = """
            You are an automated, high-tech real-time content moderator for 'Indiyaari', a friendly Indian connecting app. 
            Analyze the input text objectively. 
            If it is normal, friendly, debate, or casual slang, respond exactly: SAFE.
            If it is clearly abusive, threatening, contains hate speech, sexual harassment, or highly unsafe contents, respond: UNSAFE [brief 1-sentence explanation].
            Keep your response extremely concise, starting either with SAFE or UNSAFE.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemInstruction)))
        )

        return try {
            val response = api.generateContent(BuildConfig.GEMINI_API_KEY, request)
            val resultText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: ""
            Log.d(TAG, "Moderation result: $resultText")

            if (resultText.startsWith("UNSAFE", ignoreCase = true)) {
                val reason = resultText.substringAfter("UNSAFE").trim()
                Pair(false, if (reason.isNotEmpty()) reason else "Message flagged by AI Moderation.")
            } else {
                Pair(true, "Safe")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Moderation API Error", e)
            Pair(true, "Moderation bypassed due to connection issue: ${e.message}")
        }
    }

    /**
     * Generates a conversational message matching a specific peer profile and previous chat history.
     */
    suspend fun generatePeerResponse(
        peerName: String,
        peerState: String,
        peerLanguage: String,
        peerOccupation: String,
        peerInterests: String,
        history: List<Message>
    ): String {
        if (!hasValidApiKey()) {
            return "Arre, I would love to talk to you! But it seems you haven't set your Google AI Studio Gemini API Key yet. Check the settings panel on top of your app workspace to add it!"
        }

        // Keep last 15 messages for keeping conversation logic without exceeding tokens
        val conversationalTurns = history.takeLast(15).map { msg ->
            val sender = if (msg.isMe) "User" else peerName
            Content(parts = listOf(Part(text = "$sender: ${msg.text}")))
        }

        // Append final matching instruction to continue conversation
        val prompt = "Generate $peerName's next response as a casual chat bubble. Respond naturally, organic and continue the discussion."

        val systemInstruction = """
            You are playing a roleplay character on 'Indiyaari', a 1-to-1 matchmaking app connecting Indian souls.
            Your persona:
            Name: $peerName
            From State: $peerState, India
            Language(s): $peerLanguage and English
            Occupation: $peerOccupation
            Passions/Interests: $peerInterests
            
            Rules of Persona Play:
            1. Keep your replies short and conversational (1-2 sentences), matching the style of WhatsApp or messaging apps.
            2. Never reveal you are an AI or Gemini. Speak as a real human from $peerState.
            3. Use casual Indian English or occasional friendly Hinglish words if you are from Hindi states, or local-colored phrases (like 'yaara', 'machas', 'da', 'na', etc.) based on your state.
            4. Speak warmly, respectfully, but with friendly informal energy (yaari!).
            5. Incorporate details of your state or occupation if relevant, especially if referencing your shared interests ($peerInterests).
            6. If the user asks about encrypted messaging, reply enthusiastically that you love how Indiyaari has Diffie-Hellman encryption!
        """.trimIndent()

        val contentsList = conversationalTurns + Content(parts = listOf(Part(text = prompt)))

        val request = GenerateContentRequest(
            contents = contentsList,
            systemInstruction = Content(parts = listOf(Part(text = systemInstruction)))
        )

        return try {
            val response = api.generateContent(BuildConfig.GEMINI_API_KEY, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: "Acha, let's keep talking!"
        } catch (e: Exception) {
            Log.e(TAG, "Chat API Error", e)
            "Arey! I lost my signal for a second. Let's try sending that again! (Error: ${e.message})"
        }
    }
}
