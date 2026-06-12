package com.example.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.WarmWhiteText

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    onProfileSaved: (UserProfile) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Male") }
    var selectedAvatar by remember { mutableStateOf("avatar_tea") }
    var termsAccepted by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var selectedState by remember { mutableStateOf("Maharashtra") }
    var selectedLanguage by remember { mutableStateOf("Hindi") }
    var selectedOccupation by remember { mutableStateOf("Prefer not to say (Optional)") }
    val chosenInterests = remember { mutableStateListOf<String>() }

    val avatarChoices = listOf(
        Pair("avatar_tea", "☕"),
        Pair("avatar_tiger", "🐅"),
        Pair("avatar_biryani", "🍲"),
        Pair("avatar_bat", "🏏"),
        Pair("avatar_dance", "💃"),
        Pair("avatar_sitar", "🎸"),
        Pair("avatar_star", "🎬"),
        Pair("avatar_peacock", "🦚")
    )

    var captchaInput by remember { mutableStateOf("") }
    var captchaText by remember { mutableStateOf("") }
    var captchaAnswer by remember { mutableStateOf("") }
    val isCaptchaCorrect = captchaInput.trim().equals(captchaAnswer, ignoreCase = true)

    val randomGenerator = remember { java.util.Random() }

    fun generateNewCaptcha() {
        val challengeType = if (randomGenerator.nextBoolean()) "math" else "code"
        if (challengeType == "math") {
            val term1 = (6..17).random()
            val term2 = (2..9).random()
            val isAdd = randomGenerator.nextBoolean()
            if (isAdd) {
                captchaText = "$term1 + $term2 = ?"
                captchaAnswer = (term1 + term2).toString()
            } else {
                captchaText = "${term1 + term2} - $term2 = ?"
                captchaAnswer = term1.toString()
            }
        } else {
            val symbols = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
            val code = (1..5).map { symbols[randomGenerator.nextInt(symbols.length)] }.joinToString("")
            captchaText = code
            captchaAnswer = code
        }
        captchaInput = ""
    }

    LaunchedEffect(Unit) {
        generateNewCaptcha()
    }

    val stateList = listOf(
        "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat",
        "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh",
        "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab",
        "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh",
        "Uttarakhand", "West Bengal", "Andaman and Nicobar Islands", "Chandigarh",
        "Dadra and Nagar Haveli and Daman and Diu", "Delhi", "Jammu and Kashmir",
        "Ladakh", "Lakshadweep", "Puducherry"
    )
    val languageList = listOf(
        "Hindi", "English", "Bengali", "Marathi", "Telugu", "Tamil", "Gujarati", "Urdu",
        "Kannada", "Odia", "Malayalam", "Punjabi", "Assamese", "Maithili", "Santali",
        "Kashmiri", "Nepali", "Konkani", "Sindhi", "Dogri", "Manipuri", "Bodo", "Sanskrit"
    )
    val occupationList = listOf(
        "Prefer not to say (Optional)", "Student / Campus Scholar", "Unemployed / Gap Year", "Software Engineer", "Teacher / Educator", 
        "Farmer / Agriculturist", "Shopkeeper / Merchant", "Government Employee", "Indian Classical Dancer", "UI/UX Designer", 
        "Culinary Chef", "Sitar Player", "Startup Founder", "Street Art Painter", "Cricket Coach", "Medical Student", "Indie Photographer"
    )
    val interestsPool = listOf("Art", "Coding", "Sitar Music", "Cricket", "Cooking", "Photography", "Bollywood", "Writing", "Dance")

    var stateExpanded by remember { mutableStateOf(false) }
    var langExpanded by remember { mutableStateOf(false) }
    var occExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Branding logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ConnectWithoutContact,
                    contentDescription = "Logo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(42.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Indiyaari",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                )
            }

            Text(
                text = "Uniting Indian Souls via Interests, States, and Safe Connections",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            // Setup Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = AssistChipDefaults.assistChipBorder(enabled = true)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Build Your Yaari Profile",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.tertiary
                    )

                    // Name Input
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Display Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("name_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors()
                    )

                    // Gender Selection
                    Text(
                        text = "Your Gender:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val genders = listOf("Male", "Female")
                        genders.forEach { genderOption ->
                            val isSelected = selectedGender == genderOption
                            val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            val emoji = if (genderOption == "Male") "👨" else "👩"

                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedGender = genderOption }
                                    .testTag("gender_${genderOption.lowercase()}"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = containerColor),
                                border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp, horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$emoji $genderOption",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }

                    // Avatar Selection
                    Text(
                        text = "Choose Desi Avatar Badge:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        avatarChoices.forEach { (avatarSeedVal, unicodeEmoji) ->
                            val isSelected = selectedAvatar == avatarSeedVal
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                                    .clickable { selectedAvatar = avatarSeedVal }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = unicodeEmoji, fontSize = 22.sp)
                            }
                        }
                    }

                    // Indian State Dropdown
                    ExposedDropdownMenuBox(
                        expanded = stateExpanded,
                        onExpandedChange = { stateExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedState,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Your Indian State") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = stateExpanded,
                            onDismissRequest = { stateExpanded = false }
                        ) {
                            stateList.forEach { stateItem ->
                                DropdownMenuItem(
                                    text = { Text(stateItem) },
                                    onClick = {
                                        selectedState = stateItem
                                        stateExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Speaking Language Dropdown
                    ExposedDropdownMenuBox(
                        expanded = langExpanded,
                        onExpandedChange = { langExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedLanguage,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Mother Tongue / Language") },
                            leadingIcon = { Icon(Icons.Default.Translate, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = langExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = langExpanded,
                            onDismissRequest = { langExpanded = false }
                        ) {
                            languageList.forEach { langItem ->
                                DropdownMenuItem(
                                    text = { Text(langItem) },
                                    onClick = {
                                        selectedLanguage = langItem
                                        langExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Occupation Dropdown
                    ExposedDropdownMenuBox(
                        expanded = occExpanded,
                        onExpandedChange = { occExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedOccupation,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Occupation / Core Skill") },
                            leadingIcon = { Icon(Icons.Default.Work, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = occExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = occExpanded,
                            onDismissRequest = { occExpanded = false }
                        ) {
                            occupationList.forEach { occItem ->
                                DropdownMenuItem(
                                    text = { Text(occItem) },
                                    onClick = {
                                        selectedOccupation = occItem
                                        occExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Interests Multi-Select
                    Text(
                        text = "Your Passion Areas (Optional):",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        interestsPool.forEach { interest ->
                            val isSelected = chosenInterests.contains(interest)
                            val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            val textColor = if (isSelected) WarmWhiteText else MaterialTheme.colorScheme.onSurfaceVariant
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(containerColor)
                                    .clickable {
                                        if (isSelected) {
                                            chosenInterests.remove(interest)
                                        } else {
                                            chosenInterests.add(interest)
                                        }
                                    }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = textColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = interest,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Terms and Conditions checkbox row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { termsAccepted = !termsAccepted }
                    .padding(vertical = 4.dp)
                    .testTag("terms_checkbox_row"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("terms_checkbox")
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Agree to ",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Terms & Safe-Space Policy",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { showTermsDialog = true }
                        )
                    }
                    Text(
                        text = "I respect safe connections and verify the terms.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CAPTCHA Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Anti-Bot Verification",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        AssistChip(
                            onClick = {},
                            label = { 
                                Text(
                                    text = if (isCaptchaCorrect) "Human Verified" else "Awaiting Solution",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (isCaptchaCorrect) Color(0xFF14B8A6).copy(alpha = 0.1f) else Color(0xFFEF4444).copy(alpha = 0.1f),
                                labelColor = if (isCaptchaCorrect) Color(0xFF14B8A6) else Color(0xFFEF4444)
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (isCaptchaCorrect) Color(0xFF14B8A6).copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.height(24.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F0E17))
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        key(captchaText) {
                            androidx.compose.foundation.Canvas(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                val canvasWidth = size.width
                                val canvasHeight = size.height

                                // Draw security noise lines
                                for (i in 0..canvasWidth.toInt() step 45) {
                                    drawLine(
                                        color = Color(0xFF1F1A2E),
                                        start = Offset(i.toFloat(), 0f),
                                        end = Offset(i.toFloat(), canvasHeight),
                                        strokeWidth = 1f
                                    )
                                }
                                for (j in 0..canvasHeight.toInt() step 25) {
                                    drawLine(
                                        color = Color(0xFF1F1A2E),
                                        start = Offset(0f, j.toFloat()),
                                        end = Offset(canvasWidth, j.toFloat()),
                                        strokeWidth = 1f
                                    )
                                }

                                // Bezier wavy curves for anti-OCR protection
                                val random = java.util.Random(captchaText.hashCode().toLong())
                                val wavePath1 = androidx.compose.ui.graphics.Path().apply {
                                    moveTo(10f, canvasHeight * (0.3f + random.nextFloat() * 0.4f))
                                    cubicTo(
                                        canvasWidth * 0.33f, canvasHeight * random.nextFloat(),
                                        canvasWidth * 0.66f, canvasHeight * random.nextFloat(),
                                        canvasWidth - 10f, canvasHeight * (0.3f + random.nextFloat() * 0.4f)
                                    )
                                }
                                drawPath(
                                    path = wavePath1,
                                    color = Color(0xFFA78BFA).copy(alpha = 0.4f),
                                    style = Stroke(width = 4f)
                                )

                                val wavePath2 = androidx.compose.ui.graphics.Path().apply {
                                    moveTo(15f, canvasHeight * (0.2f + random.nextFloat() * 0.5f))
                                    cubicTo(
                                        canvasWidth * 0.25f, canvasHeight * random.nextFloat(),
                                        canvasWidth * 0.75f, canvasHeight * random.nextFloat(),
                                        canvasWidth - 15f, canvasHeight * (0.2f + random.nextFloat() * 0.5f)
                                    )
                                }
                                drawPath(
                                    path = wavePath2,
                                    color = Color(0xFFF472B6).copy(alpha = 0.3f),
                                    style = Stroke(width = 3f)
                                )

                                // Draw skewed characters
                                val textLength = captchaText.length
                                if (textLength > 0) {
                                    val sectorWidth = (canvasWidth - 40f) / textLength
                                    drawIntoCanvas { composeCanvas ->
                                        for (i in 0 until textLength) {
                                            val char = captchaText[i].toString()
                                            val charRandom = java.util.Random((captchaText.hashCode() + i).toLong())
                                            
                                            val charX = 20f + (i * sectorWidth) + (charRandom.nextFloat() * 10f - 5f)
                                            val charY = (canvasHeight / 2f) + (charRandom.nextFloat() * 16f - 8f)
                                            val angle = (charRandom.nextFloat() * 30f - 15f)

                                            val isPurple = i % 2 == 0
                                            val colorHex = if (isPurple) "#A78BFA" else "#F472B6"

                                            val textPaint = android.graphics.Paint().apply {
                                                color = android.graphics.Color.parseColor(colorHex)
                                                textSize = (55 + charRandom.nextInt(15)).toFloat()
                                                typeface = android.graphics.Typeface.DEFAULT_BOLD
                                                textAlign = android.graphics.Paint.Align.CENTER
                                                isAntiAlias = true
                                            }

                                            composeCanvas.nativeCanvas.save()
                                            composeCanvas.nativeCanvas.rotate(angle, charX, charY)
                                            composeCanvas.nativeCanvas.drawText(char, charX, charY + 18f, textPaint)
                                            composeCanvas.nativeCanvas.restore()
                                        }
                                    }
                                }
                            }
                        }

                        // Regenerant click button
                        IconButton(
                            onClick = { generateNewCaptcha() },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(36.dp)
                                .background(Color(0xFF1F1A2E), shape = RoundedCornerShape(8.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Regenerate Captcha",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = captchaInput,
                        onValueChange = { captchaInput = it },
                        label = { Text("Solve Verification Answer") },
                        placeholder = { Text("Type sum or letters code") },
                        leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("captcha_input"),
                        singleLine = true,
                        textStyle = TextStyle.Default.copy(
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            letterSpacing = 2.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        colors = OutlinedTextFieldDefaults.colors()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && termsAccepted && isCaptchaCorrect) {
                        onProfileSaved(
                            UserProfile(
                                name = name,
                                state = selectedState,
                                language = selectedLanguage,
                                occupation = selectedOccupation,
                                interests = chosenInterests.toList(),
                                gender = selectedGender,
                                avatarSeed = selectedAvatar
                            )
                        )
                    }
                },
                enabled = name.isNotBlank() && termsAccepted && isCaptchaCorrect,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("submit_profile_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            ) {
                Icon(Icons.Default.FlashOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Enter Indiyaari Adda",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Terms Dialog
        if (showTermsDialog) {
            AlertDialog(
                onDismissRequest = { showTermsDialog = false },
                confirmButton = {
                    TextButton(onClick = { 
                        termsAccepted = true
                        showTermsDialog = false 
                    }) {
                        Text("Accept Terms")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTermsDialog = false }) {
                        Text("Close")
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Terms & Safe-Space Policy")
                    }
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Welcome to Indiyaari, India's premier safe space for interest compatibility. To ensure high-integrity interaction, we enforce the following rules:",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "1. Respectful Moderation: All messaging feeds are monitored by sandboxed, real-time filters. Abusive or unsolicited text results in immediate blocking.\n" +
                                    "2. Safe-Space Video Calling: Real-time video frame scanning filters unauthorized behavior dynamically to maintain connection integrity.\n" +
                                    "3. Secure Diffie-Hellman Handshakes: Chats use peer-to-peer tunnels with local symmetric encryption keys which are never stored on central servers.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    }
}
