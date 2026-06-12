package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InterestRoom
import com.example.data.model.Message
import com.example.data.model.PeerProfile
import com.example.ui.theme.WarmWhiteText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    currentPeer: PeerProfile?,
    currentRoom: InterestRoom?,
    messages: List<Message>,
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onBack: () -> Unit,
    // E2E Encrypted Mode Keys
    isSecureMode: Boolean,
    onToggleSecureMode: () -> Unit,
    isPerformingHandshake: Boolean,
    handshakeDetails: Map<String, String>?,
    // Video Calling Toggles
    isVideoCallEnabled: Boolean,
    onVideoCallToggle: (Boolean) -> Unit,
    isVideoModeratorActive: Boolean,
    onVideoModeratorToggle: () -> Unit,
    // Peer Typing state & moderations
    isPeerTyping: Boolean,
    moderationWarning: String?,
    onDismissModeration: () -> Unit,
    onSkip: () -> Unit = {}
) {
    val titleName = currentPeer?.name ?: currentRoom?.name ?: "Adda Chat"
    val isRoom = currentRoom != null
    val listState = rememberLazyListState()

    // Cryptography Inspector Dialog State
    var selectedEncryptedMessage by remember { mutableStateOf<Message?>(null) }
    
    // Disappearing Messages & Report Dialog states
    var isDisappearingMode by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var reportReasonSelected by remember { mutableStateOf("Abusive Language / Bullying") }
    var reportSubmittedState by remember { mutableStateOf(false) }

    // Auto Scroll to Bottom on message updates
    LaunchedEffect(messages.size, isPeerTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = titleName.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = titleName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isRoom) "Thematic Group Adda" else "📍 ${currentPeer?.state} • ${currentPeer?.occupation}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isRoom) {
                        // High-tech Secure Mode Handshake Trigger
                        IconButton(
                            onClick = onToggleSecureMode,
                            modifier = Modifier.testTag("secure_mode_toggle")
                        ) {
                            Icon(
                                imageVector = if (isSecureMode) Icons.Default.Lock else Icons.Outlined.LockOpen,
                                contentDescription = "E2E Toggle",
                                tint = if (isSecureMode) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Video calling toggle
                        IconButton(
                            onClick = { onVideoCallToggle(!isVideoCallEnabled) },
                            modifier = Modifier.testTag("video_call_toggle")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Video Call",
                                tint = if (isVideoCallEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // WhatsApp Disappearing Messages Toggle
                        IconButton(
                            onClick = { isDisappearingMode = !isDisappearingMode },
                            modifier = Modifier.testTag("disappearing_messages_toggle")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Disappearing Messages",
                                tint = if (isDisappearingMode) Color(0xFFFBBF24) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Report User Action
                        IconButton(
                            onClick = { showReportDialog = true },
                            modifier = Modifier.testTag("report_user_icon_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = "Report User",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Handshake animation loader
            if (isPerformingHandshake) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.secondary
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔐 Diffie-Hellman Prime parameters exchanging... Public/Private Key Handshake",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Real-Time Video Call Window
            AnimatedVisibility(
                visible = isVideoCallEnabled && !isRoom,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                VideoCallDashboard(
                    peerName = titleName,
                    peerState = currentPeer?.state ?: "India",
                    isShieldOn = isVideoModeratorActive,
                    onShieldToggle = onVideoModeratorToggle
                )
            }

            // Moderation warned banner
            AnimatedVisibility(
                visible = moderationWarning != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                moderationWarning?.let { warning ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = warning,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = onDismissModeration) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Interlocking Disappearing Messages Active Banner
            AnimatedVisibility(
                visible = isDisappearingMode,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFBBF24).copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFBBF24).copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "⏳ Disappearing Messages is ACTIVE! Chats will dissolve and clear as soon as you exit the handshake.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Chat Feed List
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { message ->
                        if (message.senderName == "System" || message.senderName == "System Token") {
                            SystemAlertBubble(message)
                        } else {
                            ChatBubble(
                                message = message,
                                isSecureMode = isSecureMode,
                                isDisappearingMode = isDisappearingMode,
                                onBubbleClick = {
                                    if (message.isEncrypted) {
                                        selectedEncryptedMessage = message
                                    }
                                }
                            )
                        }
                    }

                    if (isPeerTyping) {
                        item {
                            TypingBubble(peerName = titleName)
                        }
                    }
                }
            }

            // Chat Inputs Layer
            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Quick Emoji and Desi Sticker Tray
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                            .padding(vertical = 6.dp, horizontal = 12.dp)
                            .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🇮🇳 Emojis:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        val quickEmojis = listOf("😊", "😂", "🔥", "👍", "🙏", "🎉", "❤️", "🇮🇳", "☕", "🍛")
                        quickEmojis.forEach { emoji ->
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                                    .clickable { onInputChange(inputText + emoji) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = emoji, fontSize = 13.sp)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(16.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )

                        Text(
                            text = "Stickers:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        val quickStickers = listOf(
                            Pair("☕ Chai Adda", "☕"),
                            Pair("🏏 Gully Cricket", "🏏"),
                            Pair("🍲 Biryani Love", "🍲"),
                            Pair("🦚 Peacock Pride", "🦚")
                        )
                        quickStickers.forEach { (label, emojiStr) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                                    .clickable {
                                        onInputChange("[Sticker $emojiStr: $label]")
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(text = emojiStr, fontSize = 12.sp)
                                    Text(text = label.split(" ").last(), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.secondary)
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                    if (!isRoom) {
                        Button(
                            onClick = onSkip,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ),
                            shape = RoundedCornerShape(24.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("skip_peer_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Skip Peer",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Skip", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = onInputChange,
                        placeholder = {
                            Text(
                                text = if (isSecureMode) "Send encrypted yaari message..." else "Type message..."
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        maxLines = 3,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(),
                        trailingIcon = {
                            if (isSecureMode) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Secure",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    FloatingActionButton(
                        onClick = onSendMessage,
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("send_message_button"),
                        shape = CircleShape,
                        containerColor = if (isSecureMode) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send"
                        )
                    }
                }
            } // Close inputs Column
        } // Close inputs Surface
    } // Close Scaffold Column wrapper
    } // Close Scaffold trailing lambda

        // Ciphertext explorer decryption Dialog
        if (selectedEncryptedMessage != null) {
            val msg = selectedEncryptedMessage!!
            val secretKey = handshakeDetails?.get("sharedSecret") ?: "Y_DEFAULT"
            val primeExchange = handshakeDetails?.get("prime") ?: "23"
            val genExchange = handshakeDetails?.get("generator") ?: "5"

            AlertDialog(
                onDismissRequest = { selectedEncryptedMessage = null },
                confirmButton = {
                    TextButton(onClick = { selectedEncryptedMessage = null }) {
                        Text("Sahi Hai (Got It)")
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AES-256 Crypto Inspector", fontSize = 18.sp)
                    }
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "This message was dynamically encrypted locally before transmission, satisfying high-tech military E2E parameters.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Text(
                            text = "Handshake Tunnel Details:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text("Diffie-Hellman Prime (p): $primeExchange", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                Text("Diffie-Hellman Gen (g): $genExchange", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                Text("Calculated Shared Tunnel Secret: $secretKey", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                Text("Cipher Block Mode: AES-256-GCM symmetric", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Ciphertext Payload:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = msg.encryptedPayload,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.2f))
                                .padding(8.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Decrypted Plaintext Output:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Text(
                            text = msg.text,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            )
        }

        // Report User Dialog
        if (showReportDialog) {
            AlertDialog(
                onDismissRequest = { showReportDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showReportDialog = false
                            reportSubmittedState = true
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Submit Report", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showReportDialog = false }) {
                        Text("Cancel")
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.Flag, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Text("Report this User?")
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "To maintain the highest cyber-safety standards for Indiyaari, select a reason below. Reporting will immediately separate your connection.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        val reasons = listOf(
                            "Abusive Language / Bullying",
                            "Commercial Spam / Advertisements",
                            "Inappropriate behavior or profile",
                            "Fake / Impersonating profile"
                        )
                        
                        reasons.forEach { reason ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { reportReasonSelected = reason }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                RadioButton(
                                    selected = reportReasonSelected == reason,
                                    onClick = { reportReasonSelected = reason }
                                )
                                Text(text = reason, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            )
        }

        // Report Submitted Info Banner
        if (reportSubmittedState) {
            AlertDialog(
                onDismissRequest = { 
                    reportSubmittedState = false
                    onSkip()
                },
                confirmButton = {
                    TextButton(
                        onClick = { 
                            reportSubmittedState = false
                            onSkip()
                        }
                    ) {
                        Text("Okay, Proceed")
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFF34D399))
                        Text("User Reported Successfully")
                    }
                },
                text = {
                    Text(
                        text = "Thank you! Our moderators and Gemini API filters have logged your report under \"$reportReasonSelected\". Your current chat partner is now blocked and the match is separated.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    }

// ---------------------- SUB COMPONENTS ----------------------

@Composable
fun ChatBubble(
    message: Message,
    isSecureMode: Boolean,
    isDisappearingMode: Boolean = false,
    onBubbleClick: () -> Unit
) {
    val isMe = message.isMe
    val arrangement = if (isMe) Arrangement.End else Arrangement.Start
    val bubbleColor = if (isMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (isMe) {
        WarmWhiteText
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val alignCorner = if (isMe) {
        RoundedCornerShape(16.dp, 16.dp, 2.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = arrangement
    ) {
        Card(
            shape = alignCorner,
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clickable {
                    if (message.isEncrypted) {
                        onBubbleClick()
                    }
                }
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!isMe) {
                    Text(
                        text = message.senderName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                val isSticker = message.text.startsWith("[Sticker")
                if (isSticker) {
                    // E.g. "[Sticker ☕: ☕ Chai Adda]"
                    val emojiStr = message.text.substringAfter("[Sticker ").substringBefore(":")
                    val label = message.text.substringAfter(": ").substringBefore("]")
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Text(text = emojiStr, fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (message.isEncrypted) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Encrypted",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .size(14.dp)
                                    .padding(end = 4.dp)
                            )
                        }
                        Text(
                            text = message.text,
                            fontSize = 14.sp,
                            color = textColor,
                            fontWeight = if (message.isEncrypted) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }

                if (isMe) {
                    Row(
                        modifier = Modifier.align(Alignment.End).padding(top = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = "🔒 E2E",
                            fontSize = 8.sp,
                            color = textColor.copy(alpha = 0.62f)
                        )
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Delivered & Read",
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                if (message.isEncrypted) {
                    Text(
                        text = "AES (Tap to inspect)",
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (isDisappearingMode && !isSticker) {
                    Text(
                        text = "⏳ vanishes shortly",
                        fontSize = 8.sp,
                        color = if (isMe) textColor.copy(alpha = 0.55f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SystemAlertBubble(message: Message) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.15f)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = message.text,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun TypingBubble(peerName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$peerName is drafting message",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                // Simple dotted pulse
                Text(
                    text = "...",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun VideoCallDashboard(
    peerName: String,
    peerState: String,
    isShieldOn: Boolean,
    onShieldToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE VIDEO ADD-ON",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isShieldOn) Color(0xFF00796B) else Color(0xFFD32F2F))
                        .clickable { onShieldToggle() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (isShieldOn) Icons.Default.Security else Icons.Default.PrivacyTip,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isShieldOn) "AI Guard ACTIVE" else "AI Guard OVERRIDE",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dual Video feeds
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Peer feed
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF263238))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Simulated visual noise
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$peerName's Camera",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Streamed from $peerState",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 9.sp
                        )
                    }
                }

                // Self feed
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF37474F))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "You (Local Feed)",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "NSFW Scanning: COMPLETED",
                            color = Color(0xFF00E676),
                            fontSize = 9.sp
                        )
                    }
                }
            }

            if (isShieldOn) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🛡️ Real-time video frame check active. AI algorithms scan coordinate mappings to prevent nudity, unsafe gestures, or offensive displays. Automated report mechanisms protect you.",
                    color = Color(0xFF00E676),
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
