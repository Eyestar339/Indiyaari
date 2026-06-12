package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
    onLogout: () -> Unit,
    onSaveProfile: (UserProfile) -> Unit
) {
    var activeTab by remember { mutableIntStateOf(0) } // 0 = Finder, 1 = Desi Quiz, 2 = Thematic Adda, 3 = My Experience

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
                    Triple("Desi Quiz Arena", Icons.Default.Casino, 1),
                    Triple("Thematic Adda", Icons.Default.Forum, 2),
                    Triple("My Experience", Icons.Default.Person, 3)
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
                    1 -> QuizTabContent(userProfile = userProfile)
                    2 -> RoomsTabContent(
                        rooms = interestRooms,
                        onJoinRoom = onJoinRoom
                    )
                    3 -> ProfileAndSettingsTabContent(
                        userProfile = userProfile,
                        matchedPeers = matchedPeers,
                        onSelectChat = onSelectionChat,
                        onDeleteChat = onDeleteChat,
                        onSaveProfile = onSaveProfile,
                        onLogout = onLogout
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

                Spacer(modifier = Modifier.height(16.dp))

                // Polite Safe Communication Guidance Box
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "🇮🇳 Community Rules for Polite Chatting",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        
                        Divider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Text(
                            text = "📌 1. Message Politely: Always greet your match with respect (e.g. Namaste / Hello). Be polite and warm.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "📌 2. Respect Differences: You are matching with peers across different Indian regions and languages. Please celebrate this diversity.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "📌 3. Stay Cyber-Secure: For security goals, do not share personal telephone numbers, bank pins, or addresses.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "📌 4. Gentle Skipper: If the match feels incompatible, simply tap the 'Skip' button politely to find a new connection.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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

// ---------------------- INDIAN MULTIPLAYER QUIZ ARENA ----------------------

data class TriviaQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIdx: Int,
    val explanation: String
)

val IndianTriviaPool = listOf(
    TriviaQuestion(
        id = 1,
        question = "Which Indian space mission made India the first country to land near the lunar south pole?",
        options = listOf("Chandrayaan-2", "Mangalyaan", "Chandrayaan-3", "Gaganyaan"),
        correctIdx = 2,
        explanation = "Chandrayaan-3 landed successfully near the South Pole on August 23, 2023!"
    ),
    TriviaQuestion(
        id = 2,
        question = "Which Indian city is known as the 'Silicon Valley of India'?",
        options = listOf("Hyderabad", "Bengaluru", "Mumbai", "Pune"),
        correctIdx = 1,
        explanation = "Bengaluru is named Silicon Valley due to its leading role as the nation's information technology exporter!"
    ),
    TriviaQuestion(
        id = 3,
        question = "Which spice is known as the 'Queen of Spices' in India?",
        options = listOf("Black Pepper", "Cardamom", "Turmeric", "Cinnamon"),
        correctIdx = 1,
        explanation = "Cardamom (Elaichi) is widely hailed as the Queen of Spices for its rich aroma and culinary values."
    ),
    TriviaQuestion(
        id = 4,
        question = "Who directed the Oscar-winning song 'Naatu Naatu' movie 'RRR'?",
        options = listOf("S. S. Rajamouli", "Sanjay Leela Bhansali", "Mani Ratnam", "Prashanth Neel"),
        correctIdx = 0,
        explanation = "S. S. Rajamouli directed RRR, featuring the global hit track 'Naatu Naatu'!"
    ),
    TriviaQuestion(
        id = 5,
        question = "In which Indian state can you find the famous Tea Hills of Munnar?",
        options = listOf("Tamil Nadu", "Assam", "Kerala", "Karnataka"),
        correctIdx = 2,
        explanation = "Munnar tea gardens are situated in the Western Ghats mountain range of Kerala."
    ),
    TriviaQuestion(
        id = 6,
        question = "Which legendary Indian cricketer is affectionately known as the 'God of Cricket'?",
        options = listOf("M.S. Dhoni", "Sachin Tendulkar", "Virat Kohli", "Kapil Dev"),
        correctIdx = 1,
        explanation = "Sachin Tendulkar is celebrated across the world as the God of Cricket."
    )
)

@Composable
fun QuizTabContent(userProfile: UserProfile) {
    var quizStep by remember { mutableIntStateOf(0) } // 0 = Ready Screen, 1 = Scanning Partner, 2 = Live Quiz, 3 = Scoreboard Summary
    var quizMatchedPeerName by remember { mutableStateOf("") }
    var quizMatchedPeerAvatar by remember { mutableStateOf("🇮🇳") }
    
    var currentQuestionIdx by remember { mutableIntStateOf(0) }
    var userScore by remember { mutableIntStateOf(0) }
    var peerScore by remember { mutableIntStateOf(0) }
    var selectedAnswerIdx by remember { mutableStateOf<Int?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }
    var scanStatusLabel by remember { mutableStateOf("Initializing Arena Tunnels...") }

    // Quiz Matching simulation
    LaunchedEffect(quizStep) {
        if (quizStep == 1) {
            val statusMsgs = listOf(
                "Searching Active Quiz Seekers...",
                "Peer found in Maharashtra! Requesting secure socket sync...",
                "Diffie-Hellman handshake completed! Syncing Quiz Engine...",
                "Ready to Dual!"
            )
            for (i in statusMsgs.indices) {
                scanStatusLabel = statusMsgs[i]
                kotlinx.coroutines.delay(800)
            }
            // Generate standard peer details
            val peerNames = listOf("Aarav", "Kabir", "Neha", "Ishita", "Rohan", "Ananya", "Vivaan")
            val peerAvatars = listOf("🇮🇳", "🦁", "☕", "💃", "🏏", "🦚", "🍲")
            quizMatchedPeerName = peerNames.random()
            quizMatchedPeerAvatar = peerAvatars.random()
            currentQuestionIdx = 0
            userScore = 0
            peerScore = 0
            selectedAnswerIdx = null
            hasAnswered = false
            quizStep = 2
        }
    }

    // Auto peer answer simulation
    LaunchedEffect(hasAnswered) {
        if (hasAnswered && quizStep == 2) {
            kotlinx.coroutines.delay(600)
            val currentQuestion = IndianTriviaPool[currentQuestionIdx % IndianTriviaPool.size]
            val peerCorrect = (0..10).random() > 3 // 70% chance correct
            val peerAnswer = if (peerCorrect) currentQuestion.correctIdx else (0..3).filter { it != currentQuestion.correctIdx }.random()
            if (peerAnswer == currentQuestion.correctIdx) {
                peerScore += 10
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        when (quizStep) {
            0 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🏆", fontSize = 36.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "1v1 Desi Quiz Arena",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Match live with random Indian peers & test your knowledge about Cricket, Cinema, Spices & History!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { quizStep = 1 },
                        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("start_quiz_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(imageVector = Icons.Default.Casino, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Find Quiz Partner!", fontWeight = FontWeight.Bold)
                    }
                }
            }
            1 -> {
                // Matching scanner UI
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    MatchmakerRadar()

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = scanStatusLabel,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Display radar rule as helpful scan suggestion block
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💡 Polite Rule: Greet your partner nicely when the quiz begins or dual wraps!", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            2 -> {
                val currentQuestion = IndianTriviaPool[currentQuestionIdx % IndianTriviaPool.size]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // Quiz Match Header Space with Scores
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User Profile summary
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = userProfile.avatarSeed.split(":").getOrNull(1) ?: "🇮🇳", fontSize = 18.sp)
                            Column {
                                Text(text = "You", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(text = "$userScore Pts", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }

                        Text(
                            text = "Round ${ (currentQuestionIdx % IndianTriviaPool.size) + 1 }",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )

                        // Peer summary
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = quizMatchedPeerAvatar, fontSize = 18.sp)
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = quizMatchedPeerName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(text = "$peerScore Pts", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    // Question Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = currentQuestion.question,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Options list
                    currentQuestion.options.forEachIndexed { idx, option ->
                        val isCorrect = idx == currentQuestion.correctIdx
                        val isSelected = selectedAnswerIdx == idx
                        
                        val optionBgColor = when {
                            hasAnswered && isCorrect -> Color(0xFF10B981).copy(alpha = 0.2f) // Emerald green
                            hasAnswered && isSelected && !isCorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                            isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.surface
                        }

                        val optionBorderColor = when {
                            hasAnswered && isCorrect -> Color(0xFF10B981)
                            hasAnswered && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
                            isSelected -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable(enabled = !hasAnswered) {
                                    selectedAnswerIdx = idx
                                    hasAnswered = true
                                    if (idx == currentQuestion.correctIdx) {
                                        userScore += 10
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = optionBgColor),
                            border = BorderStroke(1.dp, optionBorderColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = ('A' + idx).toString(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(text = option, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    if (hasAnswered) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                        ) {
                            Text(
                                text = "💡 Explanation: ${currentQuestion.explanation}",
                                fontSize = 11.sp,
                                modifier = Modifier.padding(8.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Next question trigger or wrap
                    if (hasAnswered) {
                        val isLast = currentQuestionIdx == 2
                        Button(
                            onClick = {
                                if (isLast) {
                                    quizStep = 3
                                } else {
                                    currentQuestionIdx++
                                    selectedAnswerIdx = null
                                    hasAnswered = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text(if (isLast) "View Dual Report!" else "Next Duel Question", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = {},
                            enabled = false,
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Waiting for your answer...", fontSize = 13.sp)
                        }
                    }
                }
            }
            3 -> {
                // Game Finished Summary
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val didWin = userScore > peerScore
                    val isDraw = userScore == peerScore
                    
                    Text(
                        text = if (isDraw) "🤝 It is a Desi Draw!" else if (didWin) "🎉 Vijayee Bhava! You Won!" else "😔 Better luck next time!",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (isDraw) MaterialTheme.colorScheme.tertiary else if (didWin) Color(0xFF10B981) else MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dynamic scoreboard card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Your Point Tally", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text("$userScore Pts", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("$quizMatchedPeerName's Point Tally", fontSize = 13.sp)
                                Text("$peerScore Pts", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Matched successfully into local yaari history! You can start a new dual session anytime.",
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { quizStep = 0 },
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Text("Arena Exit")
                        }
                        Button(
                            onClick = { quizStep = 1 },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Re-match Duel")
                        }
                    }
                }
            }
        }
    }
}

// ---------------------- MY EXPERIENCE & PROFILE SETUP TAB ----------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileAndSettingsTabContent(
    userProfile: UserProfile,
    matchedPeers: List<PeerProfile>,
    onSelectChat: (PeerProfile) -> Unit,
    onDeleteChat: (String) -> Unit,
    onSaveProfile: (UserProfile) -> Unit,
    onLogout: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(userProfile.name) }
    var editState by remember { mutableStateOf(userProfile.state) }
    var editLanguage by remember { mutableStateOf(userProfile.language) }
    var editOccupation by remember { mutableStateOf(userProfile.occupation.ifEmpty { "Student / Campus Scholar" }) }
    var editAvatarSeed by remember { mutableStateOf(userProfile.avatarSeed) }

    val avatarList = listOf(
        Pair("Traditional Desi Avatar", "🇮🇳"),
        Pair("Royal Bengal Tiger", "🦁"),
        Pair("Desi Chai Lover", "☕"),
        Pair("Folk Dancer", "💃"),
        Pair("Cricket Star", "🏏"),
        Pair("National Peacock Bird", "🦚"),
        Pair("Delicious Biryani Plate", "🍲")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Upper Profile Summary section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Avatar badge
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = editAvatarSeed.split(":").getOrNull(1) ?: "🇮🇳",
                        fontSize = 32.sp
                    )
                }

                Text(
                    text = userProfile.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = "📍 ${userProfile.state} • ${userProfile.language} • ${userProfile.occupation}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = "🛡️ Cyber-Verified", fontSize = 10.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = "Standing: 98% Safe", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (isEditing) {
            // Profile setup editor layout
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "Edit Indiyaari Profile Details", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editState,
                        onValueChange = { editState = it },
                        label = { Text("Indian Region / State") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editLanguage,
                        onValueChange = { editLanguage = it },
                        label = { Text("Primary Language") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editOccupation,
                        onValueChange = { editOccupation = it },
                        label = { Text("Occupation (Student / Unemployed / Professional etc)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(text = "Select Indian Avatar Icon", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        avatarList.forEach { (avatarLabel, avatarEmoji) ->
                            val currentSeed = "$avatarLabel:$avatarEmoji"
                            val isSelected = editAvatarSeed == currentSeed
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                    .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { editAvatarSeed = currentSeed }
                                    .padding(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(text = avatarEmoji, fontSize = 14.sp)
                                    Text(text = avatarLabel.split(" ").last(), fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { isEditing = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Discard")
                        }
                        Button(
                            onClick = {
                                isEditing = false
                                val updated = userProfile.copy(
                                    name = editName,
                                    state = editState,
                                    language = editLanguage,
                                    occupation = editOccupation,
                                    avatarSeed = editAvatarSeed
                                )
                                onSaveProfile(updated)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save Changes")
                        }
                    }
                }
            }
        } else {
            // Edit trigger button
            Button(
                onClick = { isEditing = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Profile Identity", fontWeight = FontWeight.Bold)
            }
        }

        // Historic matched Peers logs
        Text(
            text = "📬 Encrypted Chat Handshake History",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 8.dp)
        )

        HistoryTabContent(
            peers = matchedPeers,
            onSelectChat = onSelectChat,
            onDeleteChat = onDeleteChat
        )

        // Safety parameters card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🛡️ Indiyaari Secure Session Protocol", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(text = "This session uses client-side Diffie-Hellman handshake exchanging. Local keys self-sign and clear safely on logout.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp).height(44.dp)
                ) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Secure Purge & Logout", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
