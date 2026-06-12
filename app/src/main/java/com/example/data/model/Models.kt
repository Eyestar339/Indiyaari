package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class UserProfile(
    val name: String = "",
    val state: String = "",
    val language: String = "",
    val occupation: String = "",
    val interests: List<String> = emptyList(),
    val gender: String = "",
    val avatarSeed: String = "avatar_1"
)

@Entity(tableName = "peers")
data class PeerProfile(
    @PrimaryKey val id: String,
    val name: String,
    val state: String,
    val language: String,
    val occupation: String,
    val interests: String, // Comma-separated or serialized
    val avatarSeed: String, // String for generating a unique visual avatar
    val matchedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: String, // Match ID or Room ID
    val senderName: String, // "You" or partner's name
    val isMe: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    
    // High-tech Encrypted Private Messaging features
    val isEncrypted: Boolean = false,
    val encryptedPayload: String = "" // Holds the base64 AES encrypted payload
)

data class InterestRoom(
    val id: String,
    val name: String,
    val iconName: String,
    val description: String,
    val category: String,
    val activeCount: Int
)
