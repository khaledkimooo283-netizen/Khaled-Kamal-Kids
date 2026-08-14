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
import com.example.util.Localization

@Composable
fun ProfileSettingsScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var childName by remember { mutableStateOf(repository.getChildName()) }
    var childAge by remember { mutableIntStateOf(repository.getChildAge()) }
    var avatarEmoji by remember { mutableStateOf(repository.getAvatarEmoji()) }
    val userCoins = repository.coinsState.intValue
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
    var showChangePinDialog by remember { mutableStateOf(false) }
    var newPinValue by remember { mutableStateOf("") }
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
                        Text(text = Localization.tr("voice_volume", appLanguage), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        Slider(
                            value = voiceVolume,
                            onValueChange = {
                                voiceVolume = it
                                repository.setVoiceVolume(it)
                                audioEngine.applyVoiceConfig(voiceType, it, appLanguage)
                            },
                            colors = SliderDefaults.colors(thumbColor = Color(0xFF2563EB), activeTrackColor = Color(0xFF3B82F6))
                        )

                        // Voice Type Selector (Child Voice, Female Teacher, Male Teacher)
                        Text(text = Localization.tr("voice_style", appLanguage), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val voiceOptions: List<Pair<String, String>> = listOf(
                                Pair("Child Voice", Localization.tr("voice_child", appLanguage)),
                                Pair("Female Teacher", Localization.tr("voice_female", appLanguage)),
                                Pair("Male Teacher", Localization.tr("voice_male", appLanguage))
                            )
                            voiceOptions.forEach { option ->
                                val typeKey = option.first
                                val label = option.second
                                FilterChip(
                                    selected = voiceType == typeKey,
                                    onClick = {
                                        voiceType = typeKey
                                        repository.setVoiceType(typeKey)
                                        audioEngine.applyVoiceConfig(typeKey, voiceVolume, appLanguage)
                                        val confirmMsg = if (appLanguage == "Arabic") {
                                            "تم اختيار $label"
                                        } else {
                                            "$typeKey selected!"
                                        }
                                        audioEngine.speak(confirmMsg)
                                    },
                                    label = { Text(label, fontSize = 12.sp) },
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
                            Text(text = Localization.tr("bg_music", appLanguage), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                            Switch(
                                checked = isMusicEnabled,
                                onCheckedChange = {
                                    isMusicEnabled = it
                                    repository.setMusicEnabled(it)
                                    audioEngine.setBgmEnabled(it)
                                }
                            )
                        }

                        if (isMusicEnabled) {
                            Slider(
                                value = musicVolume,
                                onValueChange = {
                                    musicVolume = it
                                    repository.setMusicVolume(it)
                                    audioEngine.setBgmVolume(it)
                                },
                                colors = SliderDefaults.colors(thumbColor = Color(0xFFEC4899), activeTrackColor = Color(0xFFF43F5E))
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = Localization.tr("sound_fx", appLanguage), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                            Switch(
                                checked = isSoundFxEnabled,
                                onCheckedChange = {
                                    isSoundFxEnabled = it
                                    repository.setSoundFxEnabled(it)
                                    audioEngine.setSoundFxEnabled(it)
                                }
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))

                        // Gameplay & Tracing Settings
                        Text(
                            text = Localization.tr("gameplay_difficulty", appLanguage),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(text = Localization.tr("difficulty_level", appLanguage), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val diffList: List<Pair<String, String>> = listOf(
                                Pair("Easy", Localization.tr("easy", appLanguage)),
                                Pair("Medium", Localization.tr("medium", appLanguage)),
                                Pair("Hard", Localization.tr("hard", appLanguage))
                            )
                            diffList.forEach { item ->
                                val levelKey = item.first
                                val label = item.second
                                FilterChip(
                                    selected = gameDifficulty == levelKey,
                                    onClick = {
                                        gameDifficulty = levelKey
                                        repository.setGameDifficulty(levelKey)
                                    },
                                    label = { Text(label) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(text = Localization.tr("tracing_sensitivity", appLanguage), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val sensList: List<Pair<String, String>> = listOf(
                                Pair("Low", Localization.tr("low", appLanguage)),
                                Pair("Medium", Localization.tr("medium", appLanguage)),
                                Pair("High", Localization.tr("high", appLanguage))
                            )
                            sensList.forEach { item ->
                                val sensKey = item.first
                                val label = item.second
                                FilterChip(
                                    selected = tracingSensitivity == sensKey,
                                    onClick = {
                                        tracingSensitivity = sensKey
                                        repository.setTracingSensitivity(sensKey)
                                    },
                                    label = { Text(label) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))

                        // Accessibility & Language
                        Text(
                            text = Localization.tr("accessibility_lang", appLanguage),
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
                            Text(text = Localization.tr("large_text_mode", appLanguage), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
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
                            Text(text = Localization.tr("language", appLanguage), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = appLanguage == "English",
                                    onClick = {
                                        appLanguage = "English"
                                        repository.setLanguage("English")
                                        audioEngine.applyVoiceConfig(voiceType, voiceVolume, "English")
                                        audioEngine.speak("Language changed to English!")
                                    },
                                    label = { Text(Localization.tr("english", appLanguage)) }
                                )
                                FilterChip(
                                    selected = appLanguage == "Arabic",
                                    onClick = {
                                        appLanguage = "Arabic"
                                        repository.setLanguage("Arabic")
                                        audioEngine.applyVoiceConfig(voiceType, voiceVolume, "Arabic")
                                        audioEngine.speak("تم تغيير اللغة إلى العربية!")
                                    },
                                    label = { Text(Localization.tr("arabic", appLanguage)) }
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))

                        // Change Parent PIN Button
        OutlinedButton(
            onClick = {
                pendingParentAction = {
                    newPinValue = ""
                    showChangePinDialog = true
                }
                showParentGatePinDialog = true
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Lock, contentDescription = "Parent Gate", tint = Color(0xFF2563EB))
            Spacer(modifier = Modifier.width(8.dp))
            Text(Localization.tr("change_pin_btn", appLanguage), fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE2E8F0))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Application Version 1.0.23 (Build 23) ✅",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Parent Protected Reset Button
        Button(
            onClick = {
                pendingParentAction = {
                    repository.resetAllProgress()
                    userStars = repository.getStars()
                    audioEngine.speak(if (appLanguage == "Arabic") "تم إعادة ضبط التقدم بنجاح" else "Progress reset successfully!")
                }
                showParentGatePinDialog = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Lock, contentDescription = "Parent Gate", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(Localization.tr("reset_progress", appLanguage), fontWeight = FontWeight.Bold, color = Color.White)
        }
                    }
                }

                // Help & Support Section Card (Rule 16)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Help & Support ❓",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )

                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEFF6FF))
                                .clickable { audioEngine.speak("Contact Support email: support@kkkids.app") }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📧", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Contact Support", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                                Text("support@kkkids.app • 24/7 Parent Help", fontSize = 12.sp, color = Color(0xFF64748B))
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF0FDF4))
                                .clickable { audioEngine.speak("FAQ: KK Kids is an offline-safe educational environment for kids.") }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💡", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Frequently Asked Questions (FAQ)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                                Text("100% Safe, No Ads, Offline Compatible", fontSize = 12.sp, color = Color(0xFF166534))
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFAF5FF))
                                .clickable { audioEngine.speak("Privacy Policy and Terms: 100% KidSafe Certified") }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🛡️", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Privacy Policy & Terms", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                                Text("COPPA & KidSafe Certified Privacy Standard", fontSize = 12.sp, color = Color(0xFF6B21A8))
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFEF3C7))
                                .clickable { audioEngine.speak("Thank you for rating KK Kids 5 stars!") }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⭐", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Rate KK Kids", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                                Text("Love the app? Leave us a 5-star review!", fontSize = 12.sp, color = Color(0xFF92400E))
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Khaled Kamal Kids (KK Kids) v1.0 Final Release • Production Build",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Edit Profile Dialog
        if (showEditProfileDialog) {
            var dobDayText by remember { mutableStateOf(repository.getDobDay().toString()) }
            var dobMonthText by remember { mutableStateOf(repository.getDobMonth().toString()) }
            var dobYearText by remember { mutableStateOf(repository.getDobYear().toString()) }

            AlertDialog(
                onDismissRequest = { showEditProfileDialog = false },
                title = { Text("Edit Child Profile 👤", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = childName,
                            onValueChange = { childName = it },
                            label = { Text("Child Name") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Date of Birth (DD / MM / YYYY):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = dobDayText,
                                onValueChange = { if (it.length <= 2) dobDayText = it },
                                label = { Text("Day") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = dobMonthText,
                                onValueChange = { if (it.length <= 2) dobMonthText = it },
                                label = { Text("Month") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = dobYearText,
                                onValueChange = { if (it.length <= 4) dobYearText = it },
                                label = { Text("Year") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        val calculatedAge = remember(dobDayText, dobMonthText, dobYearText) {
                            val d = dobDayText.toIntOrNull() ?: 15
                            val m = dobMonthText.toIntOrNull() ?: 6
                            val y = dobYearText.toIntOrNull() ?: 2021
                            val cal = java.util.Calendar.getInstance()
                            var a = cal.get(java.util.Calendar.YEAR) - y
                            if (cal.get(java.util.Calendar.MONTH) + 1 < m || (cal.get(java.util.Calendar.MONTH) + 1 == m && cal.get(java.util.Calendar.DAY_OF_MONTH) < d)) {
                                a--
                            }
                            a.coerceAtLeast(1)
                        }

                        Text("Calculated Age: $calculatedAge years old (Auto)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))

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
                            val d = dobDayText.toIntOrNull() ?: 15
                            val m = dobMonthText.toIntOrNull() ?: 6
                            val y = dobYearText.toIntOrNull() ?: 2021
                            repository.setChildName(childName)
                            repository.setAvatarEmoji(avatarEmoji)
                            repository.setDob(d, m, y)
                            childAge = repository.getChildAge()
                            showEditProfileDialog = false
                            audioEngine.speak("Profile updated for $childName!")
                        }
                    ) {
                        Text("Save Profile")
                    }
                }
            )
        }

        // Parent Gate PIN Lock Dialog
        if (showParentGatePinDialog) {
            var enteredPin by remember { mutableStateOf("") }
            var pinError by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { showParentGatePinDialog = false },
                title = { Text(if (appLanguage == "Arabic") "بوابة الوالدين 🔒" else "Parental Gate 🔒", fontWeight = FontWeight.Bold) },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            if (appLanguage == "Arabic") "الرجاء إدخال رمز الأمان (الافتراضي: 1234):" else "Please enter Parent PIN (Default: 1234):",
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = enteredPin,
                            onValueChange = { enteredPin = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        if (pinError) {
                            Text(
                                if (appLanguage == "Arabic") "رمز الأمان غير صحيح!" else "Incorrect PIN! Try default 1234 or your new PIN.",
                                color = Color.Red,
                                fontSize = 12.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (enteredPin == repository.getParentPin()) {
                                showParentGatePinDialog = false
                                pinError = false
                                pendingParentAction?.invoke()
                            } else {
                                pinError = true
                            }
                        }
                    ) {
                        Text(if (appLanguage == "Arabic") "فتح" else "Unlock")
                    }
                }
            )
        }

        // Change PIN Dialog
        if (showChangePinDialog) {
            AlertDialog(
                onDismissRequest = { showChangePinDialog = false },
                title = { Text(if (appLanguage == "Arabic") "تغيير رمز الأمان 🔒" else "Change Parent PIN 🔒", fontWeight = FontWeight.Bold) },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(if (appLanguage == "Arabic") "أدخل رمز الأمان الجديد (4 أرقام):" else "Enter new 4-digit Parent PIN:", fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = newPinValue,
                            onValueChange = { if (it.length <= 4) newPinValue = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        enabled = newPinValue.length == 4,
                        onClick = {
                            repository.setParentPin(newPinValue)
                            showChangePinDialog = false
                            audioEngine.speak(if (appLanguage == "Arabic") "تم حفظ رمز الأمان الجديد بنجاح" else "New Parent PIN saved successfully!")
                        }
                    ) {
                        Text(if (appLanguage == "Arabic") "حفظ" else "Save")
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
            progress = { accuracy / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = barColor,
            trackColor = Color(0xFFE2E8F0)
        )
    }
}
