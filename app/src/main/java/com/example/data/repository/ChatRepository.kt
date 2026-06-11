package com.example.data.repository

import com.example.data.database.MessageDao
import com.example.data.database.PeerDao
import com.example.data.model.InterestRoom
import com.example.data.model.Message
import com.example.data.model.PeerProfile
import com.example.data.model.UserProfile
import com.example.service.GeminiService
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import kotlin.random.Random

class ChatRepository(
    private val messageDao: MessageDao,
    private val peerDao: PeerDao
) {
    // Expose local match history
    val matchedPeers: Flow<List<PeerProfile>> = peerDao.getAllPeers()

    // Expose messages for a session
    fun getMessages(sessionId: String): Flow<List<Message>> =
        messageDao.getMessagesForSession(sessionId)

    // Save message locally
    suspend fun saveMessage(message: Message) {
        messageDao.insertMessage(message)
    }

    // Clear session messages
    suspend fun clearSession(sessionId: String) {
        messageDao.deleteMessagesForSession(sessionId)
        peerDao.deletePeerById(sessionId)
    }

    // Static list of premium rooms
    val interestRooms = listOf(
        InterestRoom(
            id = "room_tech",
            name = "Tech Yaars",
            iconName = "Code",
            description = "Startups, coding, AI advancements & hackathons across India.",
            category = "Technology",
            activeCount = 42
        ),
        InterestRoom(
            id = "room_art",
            name = "ChitraKala",
            iconName = "Palette",
            description = "Indian traditional paintings, street art galleries, digital illustrations.",
            category = "Art & Design",
            activeCount = 28
        ),
        InterestRoom(
            id = "room_music",
            name = "Swar & Beats",
            iconName = "MusicNote",
            description = "From Hindustani classical ragas to modern Bollywood, indie, and rap.",
            category = "Music",
            activeCount = 67
        ),
        InterestRoom(
            id = "room_food",
            name = "Masala Adda",
            iconName = "Restaurant",
            description = "Exploring street food recipes, regional spices, biryani cults, and fine dining.",
            category = "Culture",
            activeCount = 35
        ),
        InterestRoom(
            id = "room_cinema",
            name = "Cinephiles Hub",
            iconName = "Movie",
            description = "Discussing Bollywood classics, regional masterpieces, and Malayalam scripts.",
            category = "Cinema",
            activeCount = 51
        )
    )

    /**
     * Generate simulated peer profile that matches preferred criteria
     */
    suspend fun matchPeer(
        myProfile: UserProfile,
        preferState: String?,
        preferLanguage: String?,
        preferInterest: String?,
        preferOccupation: String?
    ): PeerProfile {
        val maleNames = listOf("Aarav", "Kabir", "Rohan", "Karthik", "Amit", "Rajesh", "Dev", "Vikram", "Ashwin", "Abhishek")
        val femaleNames = listOf("Priyanka", "Ananya", "Simran", "Malini", "Kavya", "Tanya", "Aditi", "Shreya", "Neha", "Vidya")
        val isMale = Random.nextBoolean()
        val name = if (isMale) maleNames.random() else femaleNames.random()

        val states = listOf(
            "Maharashtra", "Karnataka", "Delhi", "Tamil Nadu", "West Bengal", 
            "Kerala", "Telangana", "Punjab", "Rajasthan", "Uttar Pradesh", "Gujarat"
        )
        val selectedState = if (!preferState.isNullOrEmpty() && preferState != "Any State") {
            preferState
        } else {
            states.random()
        }

        // Language matching based on state or preference
        val selectedLang = when {
            !preferLanguage.isNullOrEmpty() && preferLanguage != "Any Language" -> preferLanguage
            selectedState == "Maharashtra" -> "Marathi"
            selectedState == "Karnataka" -> "Kannada"
            selectedState == "Tamil Nadu" -> "Tamil"
            selectedState == "West Bengal" -> "Bengali"
            selectedState == "Kerala" -> "Malayalam"
            selectedState == "Telangana" -> "Telugu"
            selectedState == "Punjab" -> "Punjabi"
            selectedState == "Gujarat" -> "Gujarati"
            else -> listOf("Hindi", "Hindi", "English").random() // default
        }

        val occupations = listOf(
            "Software Engineer", "Indian Classical Dancer", "UI/UX Designer", 
            "Culinary Chef", "Sitar Player", "Startup Founder", "Street Art Painter", 
            "Cricket Coach", "Medical Student", "Indie Photographer", "Architecture Student"
        )
        val selectedOccupation = if (!preferOccupation.isNullOrEmpty() && preferOccupation != "Any Occupation") {
            preferOccupation
        } else {
            occupations.random()
        }

        val interestsPool = listOf(
            "Art", "Coding", "Sitar Music", "Cricket", "Cooking", "Photography", "Bollywood", "Writing", "Dance"
        )
        val chosenInterests = mutableSetOf<String>()
        if (!preferInterest.isNullOrEmpty() && preferInterest != "Any Interest") {
            chosenInterests.add(preferInterest)
        }
        // Let's add overlap from user's interests sometimes
        if (myProfile.interests.isNotEmpty()) {
            chosenInterests.add(myProfile.interests.random())
        }
        while (chosenInterests.size < 3) {
            chosenInterests.add(interestsPool.random())
        }

        val uuid = UUID.randomUUID().toString()
        val peer = PeerProfile(
            id = uuid,
            name = name,
            state = selectedState,
            language = selectedLang,
            occupation = selectedOccupation,
            interests = chosenInterests.joinToString(", "),
            avatarSeed = "avatar_${Random.nextInt(1, 10)}"
        )

        peerDao.insertPeer(peer)

        // Save automatic welcome message indicating connection
        val systemMsg = Message(
            sessionId = peer.id,
            senderName = "System",
            isMe = false,
            text = "🤝 matched with $name from $selectedState! Connection encrypted. Tap on video icon to start dynamic call overlay.",
            timestamp = System.currentTimeMillis()
        )
        messageDao.insertMessage(systemMsg)

        return peer
    }

    /**
     * Diffie-Hellman Sim Key Negotiation & AES crypto visualizer values.
     * Generates simulated local encryption visual data. Good for educational high tech representation
     */
    fun performEncryptionHandshake(peerName: String): Map<String, String> {
        val prime = "23" // Small prime for display
        val base = "5" // Generator
        val prKeyA = Random.nextInt(2, 10).toString() // User's private key
        val prKeyB = Random.nextInt(2, 10).toString() // Peer's private key
        
        // A = g^a mod p
        val pubA = (Math.pow(base.toDouble(), prKeyA.toDouble()).toLong() % prime.toLong()).toString()
        // B = g^b mod p
        val pubB = (Math.pow(base.toDouble(), prKeyB.toDouble()).toLong() % prime.toLong()).toString()

        // Shared secret = B^a mod p = A^b mod p
        val secretA = (Math.pow(pubB.toDouble(), prKeyA.toDouble()).toLong() % prime.toLong()).toString()
        
        return mapOf(
            "prime" to prime,
            "generator" to base,
            "myPrivate" to prKeyA,
            "myPublic" to pubA,
            "peerPublic" to pubB,
            "sharedSecret" to secretA,
            "cipher" to "AES-256-GCM"
        )
    }

    /**
     * Secure simulated AES encoder (reverses or adds noise representing robust encryption)
     */
    fun encryptSimulated(plainText: String, secretKey: String): String {
        val encoded = plainText.map { char ->
            (char.code + secretKey.length).toChar()
        }.joinToString("")
        return "IV_" + Random.nextInt(100, 999) + "_YAARI_" + android.util.Base64.encodeToString(encoded.toByteArray(), android.util.Base64.NO_WRAP)
    }

    fun decryptSimulated(encryptedText: String, secretKey: String): String {
        return try {
            val base64Part = encryptedText.substringAfter("_YAARI_")
            val decodedBytes = android.util.Base64.decode(base64Part, android.util.Base64.NO_WRAP)
            val shifted = String(decodedBytes)
            shifted.map { char ->
                (char.code - secretKey.length).toChar()
            }.joinToString("")
        } catch (e: Exception) {
            "Decryption failed"
        }
    }
}
