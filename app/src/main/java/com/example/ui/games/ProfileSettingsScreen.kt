package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.ui.components.*

@Composable
fun ProfileSettingsScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var childName by remember { mutableStateOf(repository.getChildName()) }
    var childAge by remember { mutableIntStateOf(repository.getChildAge()) }
    var avatarEmoji by remember { mutableStateOf(repository.getAvatarEmoji()) }
    var userCoins by remember { mutableIntStateOf(repository.getCoins()) }
    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var userStreak by remember { mutableIntStateOf(repository.getStreak()) }

    var voiceVolume by remember { mutableFloatStateOf(repository.getVoiceVolume()) }
    var musicVolume by remember { mutableFloatStateOf(repository.getMusicVolume()) }
    var isMusicEnabled by remember { mutableStateOf(repository.isMusicEnabled()) }
    var isSoundFxEnabled by remember { mutableStateOf(repository.isSoundFxEnabled()) }
    var voiceType by remember { mutableStateOf(repository.getVoiceType()) }

    var gameDifficulty by remember { mutableStateOf(repository.getGameDifficulty()) }
    var tracingSensitivity by remember { mutableStateOf(repository.getTracingSensitivity()) }
    var isLargeTextMode by remember { mutableStateOf(repository.isLargeTextMode()) }
    var appLanguage by remember { mutableStateOf(repository.getLanguage()) }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showParentGatePinDialog by remember { mutableStateOf(false) }
    var pendingParentAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    val avatars = remember { listOf("🦁", "🐘", "🐵", "🐶", "🐼", "🐧", "🦒", "🦊") }

    val tracingAcc = repository.getTracingAccuracy()
    val listeningAcc = repository.getListeningAccuracy()
    val typingAcc = repository.getTypingAccuracy()
    val readingAcc = repository.getReadingAccuracy()
    val matchingAcc = repository.getMatchingAccuracy()

    // Weakness Detection Logic
    val lowestSkill = remember(tracingAcc, listeningAcc, typingAcc, readingAcc, matchingAcc) {
        val skills = listOf(
            "Writing & Tracing" to tracingAcc,
            "Listening" to listeningAcc,
            "Spelling & Typing" to typingAcc,
            "Reading" to readingAcc,
            "Matching" to matchingAcc
        )
        skills.minByOrNull { it.second } ?: ("Reading" to 88)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)) // Modern Warm Neutral Light
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            KkHeader(
                title = "Profile & Settings 👤⚙️",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Child Profile Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFEF3C7))
                                    .border(2.dp, Color(0xFFF59E0B), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = avatarEmoji, fontSize = 38.sp)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = childName,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "Age $childAge • Level 3 Explorer",
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            IconButton(
                                onClick = { showEditProfileDialog = true },
                                modifier = Modifier
                                    .background(Color(0xFFEFF6FF), CircleShape)
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit Profile", tint = Color(0xFF2563EB))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Summary Pill Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatChip("🪙 Coins", "$userCoins", Color(0xFFFEF08A), Color(0xFF854D0E))
                            StatChip("⭐ Stars", "$userStars", Color(0xFFDBEAFE), Color(0xFF1E3A8A))
                            StatChip("📅 Streak", "$userStreak Days", Color(0xFFDCFCE7), Color(0xFF14532D))
                        }
                    }
                }

                // Adaptive Weakness Detection & Recommendation Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFCA5A5))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💡", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Adaptive Recommendation",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B)
                            )
                            Text(
                                text = "Needs practice in ${lowestSkill.first} (${lowestSkill.second}% accuracy). Let's practice today!",
                                fontSize = 13.sp,
                                color = Color(0xFF7F1D1D)
                            )
                        }
                    }
                }

                // Learning Dashboard & Skill Analytics
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Learning Analytics 📊",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        SkillProgressBar("Listening", listeningAcc, Color(0xFFA855F7))
                        SkillProgressBar("Writing & Tracing", tracingAcc, Color(0xFF3B82F6))
                        SkillProgressBar("Spelling & Typing", typingAcc, Color(0xFFF59E0B))
                        SkillProgressBar("Reading", readingAcc, Color(0xFFEC4899))
                        SkillProgressBar("Matching", matchingAcc, Color(0xFF10B981))
                    }
                }

                // App Settings Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Settings ⚙️",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Audio & Voice Settings 🔊",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Voice Volume
                        Text(text = "Voice Volume", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        Slider(
                            value = voiceVolume,
                            onValueChange = {
                                voiceVolume = it
                                repository.setVoiceVolume(it)
                            },
                            colors = SliderDefaults.colors(thumbColor = Color(0xFF2563EB), activeTrackColor = Color(0xFF3B82F6))
                        )

                        // Voice Type Selector
                        Text(text = "Voice Style", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Female Teacher", "Child Voice").forEach { type ->
                                FilterChip(
                                    selected = voiceType == type,
                                    onClick = {
                                        voiceType = type
                                        repository.setVoiceType(type)
                                    },
                                    label = { Text(type) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Background Music Toggle + Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Background Music 🎵", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                            Switch(
                                checked = isMusicEnabled,
                                onCheckedChange = {
                                    isMusicEnabled = it
                                    repository.setMusicEnabled(it)
                                }
                            )
                        }

                        if (isMusicEnabled) {
                            Slider(
                                value = musicVolume,
                                onValueChange = {
                                    musicVolume = it
                                    repository.setMusicVolume(it)
                                },
                                colors = SliderDefaults.colors(thumbColor = Color(0xFFEC4899), activeTrackColor = Color(0xFFF43F5E))
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Sound Effects 🔔", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                            Switch(
                                checked = isSoundFxEnabled,
                                onCheckedChange = {
                                    isSoundFxEnabled = it
                                    repository.setSoundFxEnabled(it)
                                }
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))

                        // Gameplay & Tracing Settings
                        Text(
                            text = "Gameplay & Difficulty 🎮",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(text = "Difficulty Level", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Easy", "Medium", "Hard").forEach { level ->
                                FilterChip(
                                    selected = gameDifficulty == level,
                                    onClick = {
                                        gameDifficulty = level
                                        repository.setGameDifficulty(level)
                                    },
                                    label = { Text(level) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(text = "Tracing Sensitivity ✏️", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Low", "Medium", "High").forEach { sens ->
                                FilterChip(
                                    selected = tracingSensitivity == sens,
                                    onClick = {
                                        tracingSensitivity = sens
                                        repository.setTracingSensitivity(sens)
                                    },
                                    label = { Text(sens) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))

                        // Accessibility & Language
                        Text(
                            text = "Accessibility & Language 🌐",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Large Text Mode 🔤", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                            Switch(
                                checked = isLargeTextMode,
                                onCheckedChange = {
                                    isLargeTextMode = it
                                    repository.setLargeTextMode(it)
                                }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Language", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = appLanguage == "English",
                                    onClick = {
                                        appLanguage = "English"
                                        repository.setLanguage("English")
                                    },
                                    label = { Text("🇬🇧 English") }
                                )
                                FilterChip(
                                    selected = appLanguage == "Arabic",
                                    onClick = {
                                        appLanguage = "Arabic"
                                        repository.setLanguage("Arabic")
                                    },
                                    label = { Text("🇪🇬 العربية") }
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))

                        // Parent Protected Reset Button
                        Button(
                            onClick = {
                                pendingParentAction = {
                                    repository.resetAllProgress()
                                    userStars = repository.getStars()
                                    userCoins = repository.getCoins()
                                    audioEngine.speak("Progress reset successfully!")
                                }
                                showParentGatePinDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Lock, contentDescription = "Parent Gate", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reset Progress (Parents Only 🔒)", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // Edit Profile Dialog
        if (showEditProfileDialog) {
            AlertDialog(
                onDismissRequest = { showEditProfileDialog = false },
                title = { Text("Edit Child Profile 👤", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = childName,
                            onValueChange = { childName = it },
                            label = { Text("Child Name") },
                            shape = RoundedCornerShape(12.dp)
                        )

                        Text("Choose Avatar:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(avatars) { emoji ->
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (avatarEmoji == emoji) Color(0xFFDBEAFE) else Color(0xFFF1F5F9))
                                        .clickable { avatarEmoji = emoji },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = emoji, fontSize = 24.sp)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            repository.setChildName(childName)
                            repository.setAvatarEmoji(avatarEmoji)
                            showEditProfileDialog = false
                            audioEngine.speak("Profile updated for $childName!")
                        }
                    ) {
                        Text("Save")
                    }
                }
            )
        }

        // Parent Gate PIN Lock Dialog (Default PIN: 1234)
        if (showParentGatePinDialog) {
            var enteredPin by remember { mutableStateOf("") }
            var pinError by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { showParentGatePinDialog = false },
                title = { Text("Parental Gate 🔒", fontWeight = FontWeight.Bold) },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Please enter Parent PIN (Default: 1234):", fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = enteredPin,
                            onValueChange = { enteredPin = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        if (pinError) {
                            Text("Incorrect PIN! Try 1234.", color = Color.Red, fontSize = 12.sp)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (enteredPin == repository.getParentPin()) {
                                showParentGatePinDialog = false
                                pendingParentAction?.invoke()
                            } else {
                                pinError = true
                            }
                        }
                    ) {
                        Text("Unlock")
                    }
                }
            )
        }
    }
}

@Composable
fun StatChip(label: String, value: String, bgColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, fontSize = 11.sp, color = textColor)
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = textColor)
        }
    }
}

@Composable
fun SkillProgressBar(skillName: String, accuracy: Int, barColor: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = skillName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
            Text(text = "$accuracy%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = barColor)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = accuracy / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = barColor,
            trackColor = Color(0xFFE2E8F0)
        )
    }
}
