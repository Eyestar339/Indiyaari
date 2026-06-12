package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.InterestRoom
import com.example.data.model.Message
import com.example.data.model.PeerProfile
import com.example.data.model.UserProfile
import com.example.data.repository.ChatRepository
import com.example.service.GeminiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = ChatRepository(database.messageDao(), database.peerDao())

    // Profile State persisted in SharedPreferences
    private val sharedPrefs = application.getSharedPreferences("indiyaari_prefs", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow(loadProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _profileCompleted = MutableStateFlow(sharedPrefs.getBoolean("profile_completed", false))
    val profileCompleted: StateFlow<Boolean> = _profileCompleted.asStateFlow()

    // History of all matches
    val matchedPeers: StateFlow<List<PeerProfile>> = repository.matchedPeers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Matchmaking State
    private val _isMatching = MutableStateFlow(false)
    val isMatching: StateFlow<Boolean> = _isMatching.asStateFlow()

    private val _matchStatusLabel = MutableStateFlow("")
    val matchStatusLabel: StateFlow<String> = _matchStatusLabel.asStateFlow()

    // Active Chat Session
    private val _currentChatPeer = MutableStateFlow<PeerProfile?>(null)
    val currentChatPeer: StateFlow<PeerProfile?> = _currentChatPeer.asStateFlow()

    private val _currentRoom = MutableStateFlow<InterestRoom?>(null)
    val currentRoom: StateFlow<InterestRoom?> = _currentRoom.asStateFlow()

    // Chat room list
    val interestRoomsList: List<InterestRoom> = repository.interestRooms

    // Dynamic message stream for active peer or room
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val activeMessages: Flow<List<Message>> = combine(
        _currentChatPeer,
        _currentRoom
    ) { peer, room ->
        peer?.id ?: room?.id ?: ""
    }.flatMapLatest { sessionId ->
        if (sessionId.isNotEmpty()) {
            repository.getMessages(sessionId)
        } else {
            flowOf(emptyList())
        }
    }

    // Encrypted messaging features (Diffie-Hellman variables)
    private val _isSecureMode = MutableStateFlow(false)
    val isSecureMode: StateFlow<Boolean> = _isSecureMode.asStateFlow()

    private val _handshakeData = MutableStateFlow<Map<String, String>?>(null)
    val handshakeData: StateFlow<Map<String, String>?> = _handshakeData.asStateFlow()

    private val _isPerformingHandshake = MutableStateFlow(false)
    val isPerformingHandshake: StateFlow<Boolean> = _isPerformingHandshake.asStateFlow()

    // Video Chat view features
    private val _isVideoCallEnabled = MutableStateFlow(false)
    val isVideoCallEnabled: StateFlow<Boolean> = _isVideoCallEnabled.asStateFlow()

    private val _isVideoModeratorActive = MutableStateFlow(true) // Default AI Guard is ON
    val isVideoModeratorActive: StateFlow<Boolean> = _isVideoModeratorActive.asStateFlow()

    private val _chatInput = MutableStateFlow("")
    val chatInput: StateFlow<String> = _chatInput.asStateFlow()

    private val _isPeerTyping = MutableStateFlow(false)
    val isPeerTyping: StateFlow<Boolean> = _isPeerTyping.asStateFlow()

    // Moderation Warning Banner
    private val _moderationWarning = MutableStateFlow<String?>(null)
    val moderationWarning: StateFlow<String?> = _moderationWarning.asStateFlow()

    // Gemini API Setup Warning
    private val _isApiKeyMissing = MutableStateFlow(!GeminiService.hasValidApiKey())
    val isApiKeyMissing: StateFlow<Boolean> = _isApiKeyMissing.asStateFlow()

    // Last used matchmaking parameters for Omegle-style Skip/Next
    private var lastPreferState: String = "Any State"
    private var lastPreferLanguage: String = "Any Language"
    private var lastPreferInterest: String = "Any Interest"
    private var lastPreferOccupation: String = "Any Occupation"

    // Save profile logic
    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            _userProfile.value = profile
            sharedPrefs.edit().apply {
                putString("name", profile.name)
                putString("state", profile.state)
                putString("language", profile.language)
                putString("occupation", profile.occupation)
                putString("interests", profile.interests.joinToString(","))
                putString("gender", profile.gender)
                putBoolean("profile_completed", true)
                apply()
            }
            _profileCompleted.value = true
        }
    }

    fun logoutProfile() {
        viewModelScope.launch {
            sharedPrefs.edit().clear().apply()
            _userProfile.value = UserProfile()
            _profileCompleted.value = false
            _currentChatPeer.value = null
            _currentRoom.value = null
        }
    }

    fun resumeActiveChat(peer: PeerProfile) {
        viewModelScope.launch {
            _currentRoom.value = null
            _currentChatPeer.value = peer
            _isSecureMode.value = false
            _handshakeData.value = null
            _isVideoCallEnabled.value = false
        }
    }

    fun updateChatInput(text: String) {
        _chatInput.value = text
    }

    fun dismissModerationWarning() {
        _moderationWarning.value = null
    }

    fun setVideoCallEnabled(enabled: Boolean) {
        _isVideoCallEnabled.value = enabled
    }

    fun toggleVideoModerator() {
        _isVideoModeratorActive.value = !_isVideoModeratorActive.value
    }

    /**
     * Start Matchmaking Search, scanning through Indian States & matching interests
     */
    fun startMatchmaking(
        preferState: String?,
        preferLanguage: String?,
        preferInterest: String?,
        preferOccupation: String?
    ) {
        lastPreferState = preferState ?: "Any State"
        lastPreferLanguage = preferLanguage ?: "Any Language"
        lastPreferInterest = preferInterest ?: "Any Interest"
        lastPreferOccupation = preferOccupation ?: "Any Occupation"

        viewModelScope.launch {
            _isMatching.value = true
            _currentChatPeer.value = null
            _currentRoom.value = null
            _isSecureMode.value = false
            _handshakeData.value = null

            val matchStates = listOf(
                "Connecting with Maharashtra...",
                "Searching creators in Karnataka...",
                "Filtering matches in Delhi NCR...",
                "Finding artist circles in West Bengal...",
                "Scanning tech groups in Karnataka...",
                "Verifying background safeties...",
                "Connecting state tunnels securely..."
            )

            for (status in matchStates) {
                _matchStatusLabel.value = status
                delay(800)
            }

            // Create simulated peer
            val peer = repository.matchPeer(
                myProfile = _userProfile.value,
                preferState = preferState,
                preferLanguage = preferLanguage,
                preferInterest = preferInterest,
                preferOccupation = preferOccupation
            )

            _currentChatPeer.value = peer
            _isMatching.value = false
        }
    }

    /**
     * Join specific thematic Adda / Interest Room
     */
    fun joinInterestRoom(room: InterestRoom) {
        viewModelScope.launch {
            _currentChatPeer.value = null
            _currentRoom.value = room
            _isSecureMode.value = false
            _handshakeData.value = null
            _isVideoCallEnabled.value = false

            // Save welcome room message
            val welcome = Message(
                sessionId = room.id,
                senderName = "Room Host",
                isMe = false,
                text = "👋 Welcome to the '${room.name}' Room! Here, you connect with members interested in ${room.category}. Automated AI Moderation is active. Keep it safe!"
            )
            repository.saveMessage(welcome)

            // Trigger some simulated active chat messages in room
            simulateRoomReplies(room.id, room.category)
        }
    }

    /**
     * Trigger secure mode Diffie-Hellman Handshake
     */
    fun toggleSecureMode() {
        val nextMode = !_isSecureMode.value
        if (nextMode) {
            val peer = _currentChatPeer.value ?: return
            viewModelScope.launch {
                _isPerformingHandshake.value = true
                delay(1200) // Aesthetic visual lag
                val keys = repository.performEncryptionHandshake(peer.name)
                _handshakeData.value = keys
                _isSecureMode.value = true
                _isPerformingHandshake.value = false

                // Add informational system message
                val secureAlert = Message(
                    sessionId = peer.id,
                    senderName = "System Token",
                    isMe = false,
                    text = "🔒 Secured Tunnel Activated via Diffie-Hellman Key Exchange. Shared Secret: [${keys["sharedSecret"]}]. All future messages show encrypted cipher payloads prior to local on-device decryption!"
                )
                repository.saveMessage(secureAlert)
            }
        } else {
            _isSecureMode.value = false
            _handshakeData.value = null
        }
    }

    /**
     * Send chat message - Handles local saving, simulation of AES encryption,
     * and automatic moderation using real-time Gemini API or local checks if API unavailable
     */
    fun sendMessage() {
        val input = _chatInput.value.trim()
        if (input.isEmpty()) return

        val sessionId = _currentChatPeer.value?.id ?: _currentRoom.value?.id ?: return
        val isPm = _currentChatPeer.value != null

        viewModelScope.launch {
            _chatInput.value = ""

            // 1. Run Automated Moderation
            val (isSafe, reason) = withContext(Dispatchers.IO) {
                GeminiService.moderateMessage(input)
            }

            if (!isSafe) {
                // Show moderation warning feedback and abort message insertion
                _moderationWarning.value = "⚠️ Your message was blocked by Indiyaari Automated Moderator: $reason"
                return@launch
            }

            // 2. Prepare Message
            val secretKey = _handshakeData.value?.get("sharedSecret") ?: "Y_DEFAULT"
            val encryptThis = _isSecureMode.value

            val msg = Message(
                sessionId = sessionId,
                senderName = "You",
                isMe = true,
                text = if (encryptThis) input else input,
                isEncrypted = encryptThis,
                encryptedPayload = if (encryptThis) repository.encryptSimulated(input, secretKey) else ""
            )

            // Save message
            repository.saveMessage(msg)

            // 3. Trigger peer simulation replies
            if (isPm) {
                triggerPeerReply(sessionId, secretKey, encryptThis)
            } else {
                // Interest group reply
                _currentRoom.value?.let { room ->
                    simulateRoomRepliesSingle(room.id, room.category)
                }
            }
        }
    }

    /**
     * Leave active chat and return to dashboard
     */
    fun leaveActiveChat() {
        _currentChatPeer.value = null
        _currentRoom.value = null
        _isSecureMode.value = false
        _handshakeData.value = null
        _isVideoCallEnabled.value = false
    }

    /**
     * Skip to next peer directly and trigger matchmaking (Omegle style)
     */
    fun skipToNextPeer() {
        viewModelScope.launch {
            _currentChatPeer.value = null
            _isSecureMode.value = false
            _handshakeData.value = null
            _isVideoCallEnabled.value = false
            
            startMatchmaking(
                preferState = lastPreferState,
                preferLanguage = lastPreferLanguage,
                preferInterest = lastPreferInterest,
                preferOccupation = lastPreferOccupation
            )
        }
    }

    /**
     * Delete a historical chat session from database
     */
    fun deleteChatHistory(peerId: String) {
        viewModelScope.launch {
            repository.clearSession(peerId)
        }
    }

    // Simulate conversational companion reply via Gemini
    private suspend fun triggerPeerReply(sessionId: String, secretKey: String, encryptThis: Boolean) {
        val peer = _currentChatPeer.value ?: return
        if (peer.id != sessionId) return

        _isPeerTyping.value = true
        delay(Random.nextLong(1500, 3000)) // Human-like pause

        viewModelScope.launch(Dispatchers.IO) {
            // Get last message history for context
            val history = database.messageDao().getMessagesForSession(sessionId).first().dropLast(1)
            
            // Invoke Gemini Service for contextual reply
            val peerReplyText = GeminiService.generatePeerResponse(
                peerName = peer.name,
                peerState = peer.state,
                peerLanguage = peer.language,
                peerOccupation = peer.occupation,
                peerInterests = peer.interests,
                history = history
            )

            val replyMsg = Message(
                sessionId = sessionId,
                senderName = peer.name,
                isMe = false,
                text = if (encryptThis) peerReplyText else peerReplyText,
                isEncrypted = encryptThis,
                encryptedPayload = if (encryptThis) repository.encryptSimulated(peerReplyText, secretKey) else ""
            )

            database.messageDao().insertMessage(replyMsg)
            _isPeerTyping.value = false
        }
    }

    // Simulates continuous activity in a group room when first joined
    private fun simulateRoomReplies(roomId: String, category: String) {
        viewModelScope.launch {
            val names = listOf("Rohan", "Shreya", "Kabir", "Neha", "Arjun", "Malini", "Karthik")
            val statements = when (category) {
                "Technology" -> listOf(
                    "Anyone here working on local Compose widgets? The memory management is superb.",
                    "Did you guys watch the new AI safety models? India is putting up active guidelines.",
                    "Bangalore roads are flooded today, software engineers are writing codes on boats 😂",
                    "Room automated security is brilliant. Keeps the hackers away!"
                )
                "Art & Design" -> listOf(
                    "Madhubani style works so well on canvas. Combining it with street art this week in Mumbai.",
                    "Currently trying vector graphics for an Indian history book. The colors of Rajasthani paintings are so vibrant.",
                    "Satyajit Ray's visual framing was pure aesthetic art form.",
                    "Where do you buy authentic handmade color pigments?"
                )
                "Music" -> listOf(
                    "Listening to Raag Yaman on Sitar this early morning. Instantly calms the mind.",
                    "Did you catch the new independent music drop from Delhi? Insane rap flows.",
                    "Classical fusion with electronic lo-fi beats is the future of yaari music.",
                    "Tabla players here? Looking for a decent collab!"
                )
                else -> listOf(
                    "Namaste everyone! Welcome to this awesome corner.",
                    "Arre, what's cooking guys? Let's talk about our hobbies.",
                    "Perfect evening for a hot cup of Chai and good connections!"
                )
            }

            for (i in 0 until 2) {
                delay(3000 + i * 2000L)
                val peerName = names.random()
                val chatMsg = Message(
                    sessionId = roomId,
                    senderName = peerName,
                    isMe = false,
                    text = statements.random()
                )
                repository.saveMessage(chatMsg)
            }
        }
    }

    private fun simulateRoomRepliesSingle(roomId: String, category: String) {
        viewModelScope.launch {
            _isPeerTyping.value = true
            delay(2000)
            val names = listOf("Rohan", "Shreya", "Kabir", "Neha", "Arjun", "Malini", "Karthik")
            val statements = when (category) {
                "Technology" -> listOf(
                    "Sahi baat hai! AI tools are speeding up code integrations by 10x.",
                    "That makes complete sense. Are you working on web or mobile apps?",
                    "Arre, super cool viewpoint!"
                )
                "Art & Design" -> listOf(
                    "Inspirational! You have a highly creative perspective.",
                    "Absolutely, the symmetry in Indian architecture is breathtaking.",
                    "Wah! Let's schedule a collab."
                )
                "Music" -> listOf(
                    "Oh, I love that genre too! Music brings people closer indeed.",
                    "Absolutely rhythm speaks louder than words.",
                    "Kyrre's beats always hit different."
                )
                else -> listOf(
                    "Arey wah, fantastic! Welcome to the group discussions.",
                    "True that. Glad we connected here on Indiyaari!",
                    "Chai pe charcha continues!"
                )
            }
            val chatMsg = Message(
                sessionId = roomId,
                senderName = names.random(),
                isMe = false,
                text = statements.random()
            )
            repository.saveMessage(chatMsg)
            _isPeerTyping.value = false
        }
    }

    private fun loadProfile(): UserProfile {
        val name = sharedPrefs.getString("name", "") ?: ""
        val state = sharedPrefs.getString("state", "") ?: ""
        val language = sharedPrefs.getString("language", "") ?: ""
        val occupation = sharedPrefs.getString("occupation", "") ?: ""
        val interestsStr = sharedPrefs.getString("interests", "") ?: ""
        val interests = if (interestsStr.isNotEmpty()) interestsStr.split(",") else emptyList()
        val gender = sharedPrefs.getString("gender", "") ?: ""
        return UserProfile(name, state, language, occupation, interests, gender)
    }
}
