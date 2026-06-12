package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.theme.IndiyaariTheme
import com.example.ui.viewmodel.ChatViewModel
import com.example.ui.screens.ProfileSetupScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ChatScreen

class MainActivity : ComponentActivity() {
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IndiyaariTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val profileCompleted by viewModel.profileCompleted.collectAsState()
                    val userProfile by viewModel.userProfile.collectAsState()
                    val matchedPeers by viewModel.matchedPeers.collectAsState()
                    val currentPeer by viewModel.currentChatPeer.collectAsState()
                    val currentRoom by viewModel.currentRoom.collectAsState()
                    val messages by viewModel.activeMessages.collectAsState(initial = emptyList())
                    val chatInput by viewModel.chatInput.collectAsState()
                    val isMatching by viewModel.isMatching.collectAsState()
                    val matchStatusLabel by viewModel.matchStatusLabel.collectAsState()
                    
                    val isSecureMode by viewModel.isSecureMode.collectAsState()
                    val handshakeData by viewModel.handshakeData.collectAsState()
                    val isPerformingHandshake by viewModel.isPerformingHandshake.collectAsState()
                    
                    val isVideoCallEnabled by viewModel.isVideoCallEnabled.collectAsState()
                    val isVideoModeratorActive by viewModel.isVideoModeratorActive.collectAsState()
                    
                    val isPeerTyping by viewModel.isPeerTyping.collectAsState()
                    val moderationWarning by viewModel.moderationWarning.collectAsState()
                    val isApiKeyMissing by viewModel.isApiKeyMissing.collectAsState()

                    when {
                        !profileCompleted -> {
                            ProfileSetupScreen(
                                onProfileSaved = { profile ->
                                    viewModel.saveProfile(profile)
                                }
                            )
                        }
                        currentPeer != null || currentRoom != null -> {
                            ChatScreen(
                                currentPeer = currentPeer,
                                currentRoom = currentRoom,
                                messages = messages,
                                inputText = chatInput,
                                onInputChange = { viewModel.updateChatInput(it) },
                                onSendMessage = { viewModel.sendMessage() },
                                onBack = { viewModel.leaveActiveChat() },
                                isSecureMode = isSecureMode,
                                onToggleSecureMode = { viewModel.toggleSecureMode() },
                                isPerformingHandshake = isPerformingHandshake,
                                handshakeDetails = handshakeData,
                                isVideoCallEnabled = isVideoCallEnabled,
                                onVideoCallToggle = { viewModel.setVideoCallEnabled(it) },
                                isVideoModeratorActive = isVideoModeratorActive,
                                onVideoModeratorToggle = { viewModel.toggleVideoModerator() },
                                isPeerTyping = isPeerTyping,
                                moderationWarning = moderationWarning,
                                onDismissModeration = { viewModel.dismissModerationWarning() },
                                onSkip = { viewModel.skipToNextPeer() }
                            )
                        }
                        else -> {
                            DashboardScreen(
                                userProfile = userProfile,
                                isApiKeyMissing = isApiKeyMissing,
                                matchedPeers = matchedPeers,
                                interestRooms = viewModel.interestRoomsList,
                                isMatching = isMatching,
                                matchStatusLabel = matchStatusLabel,
                                onStartMatching = { ps, pl, pi, po ->
                                    viewModel.startMatchmaking(ps, pl, pi, po)
                                },
                                onJoinRoom = { room ->
                                    viewModel.joinInterestRoom(room)
                                },
                                onSelectionChat = { peer ->
                                    viewModel.resumeActiveChat(peer)
                                },
                                onDeleteChat = { peerId ->
                                    viewModel.deleteChatHistory(peerId)
                                },
                                onLogout = { viewModel.logoutProfile() }
                            )
                        }
                    }
                }
            }
        }
    }
}
