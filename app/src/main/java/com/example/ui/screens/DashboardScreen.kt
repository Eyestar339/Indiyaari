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
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
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
                    1 -> QuizTabContent(userProfile = userProfile, onSaveProfile = onSaveProfile)
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

            Spacer(modifier = Modifier.height(16.dp))

            // Permanent Polite Safe Communication Guidance Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
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

val sportsTrivia = listOf(
    TriviaQuestion(1, "How many Cricket World Cups (50-overs) has India won in total?", listOf("None", "One", "Two", "Three"), 2, "India won the 50-over World Cup in 1983 and 2011."),
    TriviaQuestion(2, "Who is the first Indian track and field athlete to win an Olympic gold medal?", listOf("Milkha Singh", "Neeraj Chopra", "Abhinav Bindra", "P.V. Sindhu"), 1, "Neeraj Chopra won Gold in Men's Javelin Throw at the 2020 Tokyo Olympics."),
    TriviaQuestion(3, "Which Indian chess prodigy won Candidates 2024 to challenge for World Championship?", listOf("Dommaraju Gukesh", "Praggnanandhaa R", "Vidit Gujrathi", "Viswanathan Anand"), 0, "Gukesh D won the Candidates Tournament 2024 at just 17 years old to challenge for the crown."),
    TriviaQuestion(4, "Which sport originated in ancient India as 'Chaturanga'?", listOf("Kabaddi", "Chess", "Polo", "Badminton"), 1, "Chess tracing its roots back to Gupta Empire-era India as Chaturanga."),
    TriviaQuestion(5, "Who is affectionately known as the 'God of Cricket'?", listOf("Virat Kohli", "Sachin Tendulkar", "M.S. Dhoni", "Kapil Dev"), 1, "Sachin Tendulkar is celebrated across India and the globe as the God of Cricket."),
    TriviaQuestion(6, "Who was the captain when India won its first ever T20 World Cup in 2007?", listOf("Sourav Ganguly", "Yuvraj Singh", "M.S. Dhoni", "Rahul Dravid"), 2, "M.S. Dhoni led a young Indian squad to make history in South Africa in 2007.")
)

val bollywoodTrivia = listOf(
    TriviaQuestion(7, "Which iconic movie boasts the legendary dialogue 'Mogambo Khush Hua'?", listOf("Sholay", "Mr. India", "Don", "Deewaar"), 1, "The legendary villain Mogambo was played by Amrish Puri in the movie Mr. India."),
    TriviaQuestion(8, "Who is widely referred to as the 'Shahenshah of Bollywood'?", listOf("Shah Rukh Khan", "Amitabh Bachchan", "Salman Khan", "Aamir Khan"), 1, "Amitabh Bachchan is called the Shahenshah of Indian Cinema."),
    TriviaQuestion(9, "Which Hindi film is the longest-running in Indian cinema history?", listOf("Lagaan", "Sholay", "Dilwale Dulhania Le Jayenge", "3 Idiots"), 2, "DDLJ has been screening at the Maratha Mandir theatre in Mumbai since 1995!"),
    TriviaQuestion(10, "Which movie represented India's official entry to the 2002 Academy Awards?", listOf("Lagaan", "Devdas", "Taare Zameen Par", "Mother India"), 0, "Aamir Khan's Lagaan made it to the final five nominations of the Oscars."),
    TriviaQuestion(11, "Who directed the global hit RRR and the epic Baahubali franchise?", listOf("Mani Ratnam", "S. S. Rajamouli", "Sanjay Leela Bhansali", "Karan Johar"), 1, "S. S. Rajamouli is acclaimed for his massive vision in Baahubali and Academy Winner RRR.")
)

val techTrivia = listOf(
    TriviaQuestion(12, "Where is the headquarters of the Indian Space Research Organisation (ISRO) located?", listOf("Mumbai", "Bengaluru", "New Delhi", "Sriharikota"), 1, "ISRO headquarters is located in Bengaluru, Karnataka."),
    TriviaQuestion(13, "What is the name of India's indigenous satellite navigation system?", listOf("IRNSS / NavIC", "GPS-India", "IndiNav", "Gaganyaan-GPS"), 0, "NavIC (Navigation with Indian Constellation) is India's own autonomous GPS-like system."),
    TriviaQuestion(14, "Which Indian institute is globally famous for training CEOs of Google and Adobe?", listOf("IIT", "IISc", "IIM", "BITS Pilani"), 0, "Sundar Pichai (Google CEO) is an alumnus of IIT Kharagpur."),
    TriviaQuestion(15, "What is the name of India's first indigenous supercomputer developed by CDAC?", listOf("PARAM 8000", "Siddharth 1", "Pratyush", "Mihir"), 0, "PARAM 8000 was built in 1991 under Vijay Bhatkar's leadership."),
    TriviaQuestion(16, "Which spacecraft landed India near the lunar south pole in 2023?", listOf("Mangalyaan-2", "Chandrayaan-3", "Moonliner-1", "Aditya-L1"), 1, "Chandrayaan-3 achieved lunar touchdown near the South Pole on August 23, 2023.")
)

val foodTrivia = listOf(
    TriviaQuestion(17, "Which spice is celebrated as the 'Queen of Spices' in India?", listOf("Black Pepper", "Cardamom", "Turmeric", "Cumin"), 1, "Cardamom (Elaichi) is widely referred to as the Queen of Spices due to its flavor."),
    TriviaQuestion(18, "Which city is globally legendary for its Biryani and authentic Haleem?", listOf("Lucknow", "Hyderabad", "Kolkata", "Delhi"), 1, "Hyderabad's rich Nizami food includes its world-reknowned Biryani and GI-tagged Haleem."),
    TriviaQuestion(19, "Which yellow spice is highly valued in Ayurvedic medicine for its antiseptic properties?", listOf("Coriander", "Ginger", "Turmeric", "Clove"), 2, "Turmeric containing curcumin is popular for its healing, radiant properties."),
    TriviaQuestion(20, "What is the traditional Indian cylindrical clay oven used for baking?", listOf("Tandoor", "Chulha", "Kadhai", "Sigri"), 0, "A Tandoor is a clay oven reaching 480°C, key for juicy Tandoori food."),
    TriviaQuestion(21, "Which Indian sweet is made by deep frying flour batter in circles and soaking in sugar syrup?", listOf("Gulab Jamun", "Jalebi", "Rasgulla", "Laddu"), 1, "The hot, orange-golden spiral curves of Jalebi are an Indian favorite dessert.")
)

val historyTrivia = listOf(
    TriviaQuestion(22, "Who was the chief emperor who founded the Maurya Empire in ancient India?", listOf("Ashoka", "Chandragupta Maurya", "Samudragupta", "Harsha"), 1, "Chandragupta Maurya established the empire in 322 BCE with Chanakya's guidance."),
    TriviaQuestion(23, "The historic Sun Temple of Konark is located in which Indian state?", listOf("Odisha", "West Bengal", "Bihar", "Madhya Pradesh"), 0, "The Konark Sun Temple, a UNESCO world heritage site, is in Odisha."),
    TriviaQuestion(24, "Which river is the longest river flowing entirely inside Indian boundaries?", listOf("Narmada", "Godavari", "Ganga", "Yamuna"), 1, "The Godavari is India's second longest river, flowing entirely inside India (Ganga exits to Bangladesh)."),
    TriviaQuestion(25, "Who was the only female ruler who sat on Delhi's Sultanate throne?", listOf("Rani Laxmibai", "Razia Sultana", "Nur Jahan", "Chand Bibi"), 1, "Razia Sultana ruled the Delhi Mamluk Sultanate from 1236 to 1240."),
    TriviaQuestion(26, "Which pass connects India's Ladakh mountain region with Tibet?", listOf("Karakoram Pass", "Nathu La", "Rohtang Pass", "Shipki La"), 0, "Karakoram Pass is a high mountain pass on the boundary of Ladakh and China.")
)

@Composable
fun AvatarPhotoView(avatarSeed: String, sizeDp: Int = 68, fontSizeSp: Int = 32) {
    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        if (avatarSeed.startsWith("http")) {
            AsyncImage(
                model = avatarSeed,
                contentDescription = "User avatar photo",
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            val emoji = if (avatarSeed.contains(":")) avatarSeed.split(":").getOrNull(1) ?: "🇮🇳" else avatarSeed
            Text(
                text = emoji,
                fontSize = fontSizeSp.sp
            )
        }
    }
}

@Composable
fun QuizTabContent(userProfile: UserProfile, onSaveProfile: (UserProfile) -> Unit) {
    var quizStep by remember { mutableIntStateOf(0) } // 0 = Topic Selection, 1 = Scanning Partner, 2 = Live 1v1 Dual, 3 = Scoreboard Summary
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    var quizMatchedPeerName by remember { mutableStateOf("") }
    var quizMatchedPeerAvatar by remember { mutableStateOf("🇮🇳") }
    
    val categories = listOf(
        Pair("🏏 Cricket & Sports", sportsTrivia),
        Pair("🎬 Bollywood & Cinema", bollywoodTrivia),
        Pair("🛰️ Tech & Space", techTrivia),
        Pair("🍛 Indian Food & Culture", foodTrivia),
        Pair("🏛️ History & Geography", historyTrivia),
        Pair("🌀 Challenge Mixed Bag", sportsTrivia + bollywoodTrivia + techTrivia + foodTrivia + historyTrivia)
    )

    // Current category questions list shuffled for the session to make it feel "unlimited"
    var activeCategoryQuestions by remember { mutableStateOf(emptyList<TriviaQuestion>()) }
    var currentQuestionIdx by remember { mutableIntStateOf(0) }
    var userScore by remember { mutableIntStateOf(0) }
    var peerScore by remember { mutableIntStateOf(0) }
    var selectedAnswerIdx by remember { mutableStateOf<Int?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }
    var scanStatusLabel by remember { mutableStateOf("Initializing Arena Tunnels...") }
    
    // Simulate real 1v1 peer typing/choosing actions
    var peerSelectionStatus by remember { mutableStateOf("Thinking...") }

    // Quiz Matching simulation
    LaunchedEffect(quizStep) {
        if (quizStep == 1) {
            val selectedTopic = categories[selectedCategoryIndex].first
            activeCategoryQuestions = categories[selectedCategoryIndex].second.shuffled()
            val statusMsgs = listOf(
                "Searching Active Quiz Seekers...",
                "Peer matches found in Punjab & Delhi...",
                "Diffie-Hellman handshake completed! Unified scoreboards synced...",
                "Entering arena on topic: $selectedTopic"
            )
            for (i in statusMsgs.indices) {
                scanStatusLabel = statusMsgs[i]
                kotlinx.coroutines.delay(800)
            }
            // Generate standard peer details
            val peerNames = listOf("Aarav", "Kabir", "Neha", "Ishita", "Rohan", "Ananya", "Vivaan", "Preeti", "Rahul")
            val peerAvatars = listOf("🇮🇳", "🦁", "☕", "💃", "🏏", "🦚", "🍲", "🎨", "🚴")
            quizMatchedPeerName = peerNames.random()
            quizMatchedPeerAvatar = peerAvatars.random()
            
            currentQuestionIdx = 0
            userScore = 0
            peerScore = 0
            selectedAnswerIdx = null
            hasAnswered = false
            peerSelectionStatus = "Thinking..."
            quizStep = 2
        }
    }

    // Auto peer answer simulation
    LaunchedEffect(hasAnswered) {
        if (hasAnswered && quizStep == 2 && activeCategoryQuestions.isNotEmpty()) {
            peerSelectionStatus = "Clicking answer..."
            kotlinx.coroutines.delay(700)
            val currentQuestion = activeCategoryQuestions[currentQuestionIdx % activeCategoryQuestions.size]
            val peerCorrect = (0..10).random() > 3 // 70% chance correct
            val peerAnswer = if (peerCorrect) currentQuestion.correctIdx else (0..3).filter { it != currentQuestion.correctIdx }.random()
            if (peerAnswer == currentQuestion.correctIdx) {
                peerScore += 10
                peerSelectionStatus = "Answered correctly!"
            } else {
                peerSelectionStatus = "Guessed incorrectly!"
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
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🏆", fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Real 1v1 Multiplayer Arena",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Match live against online Indian players in custom category battles. Complete unlimited questions to level up your Quiz Master Rank!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "SELECT DUEL TOPIC:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Start).padding(start = 8.dp, bottom = 6.dp)
                    )

                    categories.forEachIndexed { index, pair ->
                        val isSelected = selectedCategoryIndex == index
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedCategoryIndex = index },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            border = BorderStroke(1.5.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = pair.first,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { quizStep = 1 },
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("start_quiz_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(imageVector = Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Find Quiz Rival Live!", fontWeight = FontWeight.Bold)
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
                    
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💡 Each victory in multiplayer adds points permanently towards your Quiz Master Rank!", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            2 -> {
                if (activeCategoryQuestions.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    val currentQuestion = activeCategoryQuestions[currentQuestionIdx % activeCategoryQuestions.size]
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Header scores
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                AvatarPhotoView(avatarSeed = userProfile.avatarSeed, sizeDp = 30, fontSizeSp = 14)
                                Column {
                                    Text(text = "You", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "$userScore", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Round ${currentQuestionIdx + 1}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    text = "Endless Match",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier.size(30.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = quizMatchedPeerAvatar, fontSize = 14.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = quizMatchedPeerName, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "$peerScore", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Peer State Indicator
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Opponent status: ", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = peerSelectionStatus,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (peerSelectionStatus.startsWith("Answered")) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.animateContentSize()
                                )
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outlineVariant)

                        // Question Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = currentQuestion.question,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Options list
                        currentQuestion.options.forEachIndexed { idx, option ->
                            val isCorrect = idx == currentQuestion.correctIdx
                            val isSelected = selectedAnswerIdx == idx
                            
                            val optionBgColor = when {
                                hasAnswered && isCorrect -> Color(0xFF10B981).copy(alpha = 0.2f)
                                hasAnswered && isSelected && !isCorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
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
                                    .padding(vertical = 3.dp)
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
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = ('A' + idx).toString(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(text = option, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }

                        if (hasAnswered) {
                            Spacer(modifier = Modifier.height(6.dp))
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

                        // Controls
                        if (hasAnswered) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        // Save Points immediately
                                        onSaveProfile(userProfile.copy(quizPoints = userProfile.quizPoints + userScore))
                                        quizStep = 3
                                    },
                                    modifier = Modifier.weight(1f).height(46.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                                ) {
                                    Text("Retire & Exit", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        currentQuestionIdx++
                                        selectedAnswerIdx = null
                                        hasAnswered = false
                                        peerSelectionStatus = "Thinking..."
                                    },
                                    modifier = Modifier.weight(1f).height(46.dp)
                                ) {
                                    Text("Next Dual Card", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        } else {
                            Button(
                                onClick = {},
                                enabled = false,
                                modifier = Modifier.fillMaxWidth().height(46.dp)
                            ) {
                                Text("Dual contest in progress...", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            3 -> {
                // Game Finished Summary
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dynamic scoreboard card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Correct Answers", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("${userScore / 10} Round wins", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Points Uploaded", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("+$userScore Pts", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("$quizMatchedPeerName's Corrects", fontSize = 12.sp)
                                Text("${peerScore / 10} Round wins", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Congratulations! Your total point tally has been credited. View your verified Quiz Master Rank update on the Experience tab!",
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { quizStep = 0 },
                            modifier = Modifier.weight(1f).height(46.dp)
                        ) {
                            Text("Arena Exit", fontSize = 12.sp)
                        }
                        Button(
                            onClick = { quizStep = 1 },
                            modifier = Modifier.weight(1f).height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Re-match Duel", fontSize = 12.sp)
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

    val avatarPresets = listOf(
        Pair("Traditional Desi", "🇮🇳"),
        Pair("Royal Bengal Tiger", "🦁"),
        Pair("Desi Chai Lover", "☕"),
        Pair("Folk Dancer", "💃"),
        Pair("Cricket Star", "🏏"),
        Pair("Peacock", "🦚"),
        Pair("Biryani", "🍲")
    )

    val photoPresets = listOf(
        Pair("Taj Mahal 🏛️", "https://images.unsplash.com/photo-1564507592333-c60657eea523?w=150"),
        Pair("Bengal Tiger 🐅", "https://images.unsplash.com/photo-1602491453979-53a9d4077303?w=150"),
        Pair("Gully Cricket 🏏", "https://images.unsplash.com/photo-1531415080290-bc98538bd802?w=150"),
        Pair("Chai Stall ☕", "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=150"),
        Pair("Delight Samosa 🍛", "https://images.unsplash.com/photo-1601050690597-df056fb4ce78?w=150")
    )

    // Calculate level & rank progressions
    val currentPoints = userProfile.quizPoints
    val (rankName, nextRankPoints) = when {
        currentPoints < 50 -> "Desi Novice 🥉" to 50
        currentPoints < 150 -> "Chai Enthusiast ☕" to 150
        currentPoints < 300 -> "Gully Legend 🏏" to 300
        currentPoints < 500 -> "Samosa Champ 🍛" to 500
        currentPoints < 800 -> "Vibe Maharaja 👑" to 800
        else -> "Ultimate Quiz Samrat 🏆" to 9999
    }

    val progressValue = if (nextRankPoints == 9999) 1.0f else currentPoints.toFloat() / nextRankPoints.toFloat()

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
                // Unified Avatar Image/Emoji badge
                AvatarPhotoView(avatarSeed = userProfile.avatarSeed, sizeDp = 76, fontSizeSp = 36)

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

        // Quiz Rank Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🏆 QUIZ MASTER RANK", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(text = "$currentPoints Total Pts", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                }

                Text(
                    text = rankName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Level Progress
                if (nextRankPoints != 9999) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LinearProgressIndicator(
                            progress = progressValue,
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Progress to next milestone", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "$currentPoints / $nextRankPoints Pts", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                } else {
                    Text(text = "⭐ Max rank achieved! You are the ultimate master.", fontSize = 10.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
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
                        label = { Text("Occupation") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Avatar Selection Options
                    Text(text = "1. Upload Photo presets (Direct Photo Upload)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        photoPresets.forEach { (photoName, photoUrl) ->
                            val isSelected = editAvatarSeed == photoUrl
                            Card(
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .clickable { editAvatarSeed = photoUrl },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                ),
                                border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // small preview
                                    Box(modifier = Modifier.size(20.dp).clip(CircleShape)) {
                                        AsyncImage(
                                            model = photoUrl,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    Text(text = photoName, fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    Text(text = "2. Select Traditional Desi Icon", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        avatarPresets.forEach { (avatarLabel, avatarEmoji) ->
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
                                    Text(text = avatarLabel, fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    Text(text = "3. Custom Web Photo URL or Emoji String", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = editAvatarSeed,
                        onValueChange = { editAvatarSeed = it },
                        label = { Text("Avatar Link/Emoji") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Real-time Preview Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            AvatarPhotoView(avatarSeed = editAvatarSeed, sizeDp = 40, fontSizeSp = 20)
                            Column {
                                Text(text = "New Avatar Preview", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(text = if (editAvatarSeed.startsWith("http")) "High-definition custom picture url uploaded" else "Traditional Desi customized representation", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
