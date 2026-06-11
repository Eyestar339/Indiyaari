package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InterestRoom
import com.example.data.model.PeerProfile
import com.example.data.model.UserProfile
import com.example.ui.theme.WarmWhiteText

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userProfile: UserProfile,
    isApiKeyMissing: Boolean,
    matchedPeers: List<PeerProfile>,
    interestRooms: List<InterestRoom>,
    isMatching: Boolean,
    matchStatusLabel: String,
    onStartMatching: (preferState: String, preferLang: String, preferInterest: String, preferOcc: String) -> Unit,
    onJoinRoom: (InterestRoom) -> Unit,
    onSelectionChat: (PeerProfile) -> Unit,
    onDeleteChat: (String) -> Unit,
    onLogout: () -> Unit
) {
    var activeTab by remember { mutableIntStateOf(0) } // 0 = Yaari Finder, 1 = Thematic Adda, 2 = Friend History

    // Filtering Criteria States for Finder
    var preferState by remember { mutableStateOf("Any State") }
    var preferLang by remember { mutableStateOf("Any Language") }
    var preferInterest by remember { mutableStateOf("Any Interest") }
    var preferOcc by remember { mutableStateOf("Any Occupation") }

    var filterStateExpanded by remember { mutableStateOf(false) }
    var filterLangExpanded by remember { mutableStateOf(false) }
    var filterIntExpanded by remember { mutableStateOf(false) }
    var filterOccExpanded by remember { mutableStateOf(false) }

    val stateChoices = listOf("Any State", "Maharashtra", "Karnataka", "Delhi", "Tamil Nadu", "West Bengal", "Kerala", "Telangana", "Punjab", "Rajasthan", "Gujarat")
    val langChoices = listOf("Any Language", "Hindi", "English", "Bengali", "Marathi", "Telugu", "Tamil", "Gujarati", "Kannada", "Malayalam", "Punjabi")
    val intChoices = listOf("Any Interest", "Art", "Coding", "Sitar Music", "Cricket", "Cooking", "Photography", "Bollywood", "Writing", "Dance")
    val occChoices = listOf("Any Occupation", "Software Engineer", "Indian Classical Dancer", "UI/UX Designer", "Culinary Chef", "Sitar Player", "Startup Founder", "Street Art Painter")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header Top Bar - Elegant Dark
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Indiyaari",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                        )
                        Text(
                            text = "12.4k online now",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = userProfile.name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "📍 ${userProfile.state}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    val initials = userProfile.name.split(" ")
                        .filter { it.isNotBlank() }
                        .take(2)
                        .joinToString("") { it.take(1).uppercase() }
                        .ifEmpty { "YA" }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .clickable { onLogout() }
                            .testTag("logout_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // API Key Check Alert Banner
            if (isApiKeyMissing) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(16.dp),
                    border = AssistChipDefaults.assistChipBorder(enabled = true)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Warning",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Prototyping Simulator Alert",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "To unlock automated real-time moderation & conversational matches powered by Gemini, please input your GEMINI_API_KEY inside the Secrets panel of Google AI Studio.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Tab Navigation Selection - Custom Elegant Dark design representation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val navItems = listOf(
                    Triple("Yaari Finder", Icons.Default.Radar, 0),
                    Triple("Thematic Adda", Icons.Default.Forum, 1),
                    Triple("Friend Logs", Icons.Default.Contacts, 2)
                )
                
                navItems.forEach { (label, icon, index) ->
                    val isSelected = activeTab == index
                    val activeBgColor = MaterialTheme.colorScheme.surfaceVariant // #4A4458 under Elegant Dark
                    val tintColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) activeBgColor else Color.Transparent)
                            .clickable { activeTab = index }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = tintColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = tintColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Body Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (activeTab) {
                    0 -> SearchTabContent(
                        isMatching = isMatching,
                        matchStatusLabel = matchStatusLabel,
                        preferState = preferState,
                        preferLang = preferLang,
                        preferInterest = preferInterest,
                        preferOcc = preferOcc,
                        stateChoices = stateChoices,
                        langChoices = langChoices,
                        intChoices = intChoices,
                        occChoices = occChoices,
                        filterStateExpanded = filterStateExpanded,
                        filterLangExpanded = filterLangExpanded,
                        filterIntExpanded = filterIntExpanded,
                        filterOccExpanded = filterOccExpanded,
                        onStateExpChange = { filterStateExpanded = it },
                        onLangExpChange = { filterLangExpanded = it },
                        onIntExpChange = { filterIntExpanded = it },
                        onOccExpChange = { filterOccExpanded = it },
                        onStateSelect = { preferState = it },
                        onLangSelect = { preferLang = it },
                        onIntSelect = { preferInterest = it },
                        onOccSelect = { preferOcc = it },
                        onStartMatch = {
                            onStartMatching(preferState, preferLang, preferInterest, preferOcc)
                        }
                    )
                    1 -> RoomsTabContent(
                        rooms = interestRooms,
                        onJoinRoom = onJoinRoom
                    )
                    2 -> HistoryTabContent(
                        peers = matchedPeers,
                        onSelectChat = onSelectionChat,
                        onDeleteChat = onDeleteChat
                    )
                }
            }
        }
    }
}

// ---------------------- SUB COMPONENTS ----------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTabContent(
    isMatching: Boolean,
    matchStatusLabel: String,
    preferState: String,
    preferLang: String,
    preferInterest: String,
    preferOcc: String,
    stateChoices: List<String>,
    langChoices: List<String>,
    intChoices: List<String>,
    occChoices: List<String>,
    filterStateExpanded: Boolean,
    filterLangExpanded: Boolean,
    filterIntExpanded: Boolean,
    filterOccExpanded: Boolean,
    onStateExpChange: (Boolean) -> Unit,
    onLangExpChange: (Boolean) -> Unit,
    onIntExpChange: (Boolean) -> Unit,
    onOccExpChange: (Boolean) -> Unit,
    onStateSelect: (String) -> Unit,
    onLangSelect: (String) -> Unit,
    onIntSelect: (String) -> Unit,
    onOccSelect: (String) -> Unit,
    onStartMatch: () -> Unit
) {
    if (isMatching) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                MatchmakerRadar()

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Searching Connection Tunnels...",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = matchStatusLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = "End-to-End Cryptography negotiation active",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Omegle-style Interest Search",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Filter connections based on Indian regions, mother tongues, skills, or shared interest topics. Matching creates a secure, sandboxed 1-on-1 private chat.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = AssistChipDefaults.assistChipBorder(enabled = true)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Connection Fit Options",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.tertiary
                    )

                    // State Choice
                    ExposedDropdownMenuBox(
                        expanded = filterStateExpanded,
                        onExpandedChange = onStateExpChange
                    ) {
                        OutlinedTextField(
                            value = preferState,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Preferred State") },
                            leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = filterStateExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = filterStateExpanded,
                            onDismissRequest = { onStateExpChange(false) }
                        ) {
                            stateChoices.forEach { stateItem ->
                                DropdownMenuItem(
                                    text = { Text(stateItem) },
                                    onClick = {
                                        onStateSelect(stateItem)
                                        onStateExpChange(false)
                                    }
                                )
                            }
                        }
                    }

                    // Language Choice
                    ExposedDropdownMenuBox(
                        expanded = filterLangExpanded,
                        onExpandedChange = onLangExpChange
                    ) {
                        OutlinedTextField(
                            value = preferLang,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Preferred Language") },
                            leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = filterLangExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = filterLangExpanded,
                            onDismissRequest = { onLangExpChange(false) }
                        ) {
                            langChoices.forEach { langItem ->
                                DropdownMenuItem(
                                    text = { Text(langItem) },
                                    onClick = {
                                        onLangSelect(langItem)
                                        onLangExpChange(false)
                                    }
                                )
                            }
                        }
                    }

                    // Interest Choice
                    ExposedDropdownMenuBox(
                        expanded = filterIntExpanded,
                        onExpandedChange = onIntExpChange
                    ) {
                        OutlinedTextField(
                            value = preferInterest,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Preferred Interest Topic") },
                            leadingIcon = { Icon(Icons.Default.LocalActivity, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = filterIntExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = filterIntExpanded,
                            onDismissRequest = { onIntExpChange(false) }
                        ) {
                            intChoices.forEach { intItem ->
                                DropdownMenuItem(
                                    text = { Text(intItem) },
                                    onClick = {
                                        onIntSelect(intItem)
                                        onIntExpChange(false)
                                    }
                                )
                            }
                        }
                    }

                    // Occupation Choice
                    ExposedDropdownMenuBox(
                        expanded = filterOccExpanded,
                        onExpandedChange = onOccExpChange
                    ) {
                        OutlinedTextField(
                            value = preferOcc,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Preferred Occupation Skill") },
                            leadingIcon = { Icon(Icons.Default.HomeWork, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = filterOccExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = filterOccExpanded,
                            onDismissRequest = { onOccExpChange(false) }
                        ) {
                            occChoices.forEach { occItem ->
                                DropdownMenuItem(
                                    text = { Text(occItem) },
                                    onClick = {
                                        onOccSelect(occItem)
                                        onOccExpChange(false)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onStartMatch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("match_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Find Yaar Connection",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun RoomsTabContent(
    rooms: List<InterestRoom>,
    onJoinRoom: (InterestRoom) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Thematic India Addas",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Step into community rooms curated by matching passions. Keep things secure and respectful; conversations are fully moderated.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        rooms.forEach { room ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onJoinRoom(room) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = AssistChipDefaults.assistChipBorder(enabled = true)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        val iconImg = when (room.iconName) {
                            "Code" -> Icons.Default.Code
                            "Palette" -> Icons.Default.Palette
                            "MusicNote" -> Icons.Default.MusicNote
                            "Restaurant" -> Icons.Default.Restaurant
                            "Movie" -> Icons.Default.Movie
                            else -> Icons.Default.Group
                        }
                        Icon(
                            imageVector = iconImg,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = room.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Badge(containerColor = MaterialTheme.colorScheme.secondary) {
                                Text(
                                    text = "${room.activeCount} active",
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                        }
                        Text(
                            text = room.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryTabContent(
    peers: List<PeerProfile>,
    onSelectChat: (PeerProfile) -> Unit,
    onDeleteChat: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Your Connection Diary",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        if (peers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ConnectWithoutContact,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No saved connections yet",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Matches you find will appear here, allowing you to resume encrypted conversations anytime.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 4.dp)
                    )
                }
            }
        } else {
            peers.forEach { peer ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = AssistChipDefaults.assistChipBorder(enabled = true)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = peer.name.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = peer.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "📍 ${peer.state} • ${peer.occupation}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Hobbies: ${peer.interests}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { onSelectChat(peer) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Open Chat",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }

                        IconButton(
                            onClick = { onDeleteChat(peer.id) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchmakerRadar() {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseRatio by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarPulse"
    )

    val rotateAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarRotate"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Box(
        modifier = Modifier.size(180.dp),
        contentAlignment = Alignment.Center
    ) {
        // Radar Pulse Ring Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            
            // Pulse wave
            drawCircle(
                color = primaryColor.copy(alpha = 1f - pulseRatio),
                radius = pulseRatio * (size.width / 2),
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Dynamic core dots
            drawCircle(
                color = primaryColor,
                radius = 8.dp.toPx(),
                center = center
            )

            // Ring borders
            drawCircle(
                color = secondaryColor.copy(alpha = 0.2f),
                radius = size.width / 4,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            drawCircle(
                color = primaryColor.copy(alpha = 0.1f),
                radius = size.width / 2,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            // Sweep visual representation
            drawArc(
                color = secondaryColor.copy(alpha = 0.15f),
                startAngle = rotateAngle,
                sweepAngle = 60f,
                useCenter = true,
                size = size
            )
        }
    }
}
