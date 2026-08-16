package com.example.ui.games.learningworld

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.*
import com.example.ui.components.Character3DFigurine
import com.example.ui.components.ConfettiOverlay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class CharacterTab {
    EXPLORE,
    VOCABULARY,
    MISSION,
    SPEAKING
}

@Composable
fun LetterCharacterAdventureScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    initialLetter: Char = 'A',
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val allCharacters = remember { LetterCharacterData.characters }
    var selectedLetter by remember { mutableStateOf(initialLetter) }
    val currentCharacter: LetterCharacter = remember(selectedLetter) {
        LetterCharacterData.getCharacterByLetter(selectedLetter)
    }

    var activeTab by remember { mutableStateOf(CharacterTab.EXPLORE) }
    var actionState by remember { mutableStateOf(CharacterActionState.IDLE) }
    var speechBubbleText by remember { mutableStateOf(currentCharacter.greetingSpeech) }
    var showSpeechBubble by remember { mutableStateOf(true) }

    // Mission Game States
    var currentMissionIndex by remember { mutableIntStateOf(0) }
    var missionCompleted by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    // Speaking state
    var isSpeakingEvaluated by remember { mutableStateOf(false) }

    // Greeting on letter switch
    LaunchedEffect(selectedLetter) {
        actionState = CharacterActionState.WAVE
        speechBubbleText = currentCharacter.greetingSpeech
        showSpeechBubble = true
        currentMissionIndex = 0
        missionCompleted = false
        isSpeakingEvaluated = false
        audioEngine.speak(currentCharacter.greetingSpeech)
        delay(2500)
        actionState = CharacterActionState.IDLE
    }

    val primaryColor = Color(currentCharacter.themeColorHex)
    val secondaryColor = Color(currentCharacter.secondaryColorHex)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.18f),
                        Color(0xFFF8FAFC),
                        secondaryColor.copy(alpha = 0.12f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        audioEngine.playClickSound()
                        onBackClick()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.5.dp, Color(0xFFCBD5E1), CircleShape)
                        .testTag("char_adv_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF1E293B)
                    )
                }

                // Title Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(currentCharacter.characterEmoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${currentCharacter.name} Land",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryColor
                        )
                    }
                }

                // Stars & Coins
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFEF3C7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⭐", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${repository.getStars()}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )
                        }
                    }
                }
            }

            // A-Z Character Horizontal Ribbon Selector
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allCharacters) { charItem ->
                    val isSelected = charItem.letter == selectedLetter
                    val itemColor = Color(charItem.themeColorHex)

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) itemColor else Color.White,
                        shadowElevation = if (isSelected) 4.dp else 1.dp,
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) itemColor else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier
                            .clickable {
                                if (selectedLetter != charItem.letter) {
                                    audioEngine.playClickSound()
                                    selectedLetter = charItem.letter
                                }
                            }
                            .testTag("char_ribbon_${charItem.letter}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${charItem.letter}${charItem.letter.lowercaseChar()}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) Color.White else Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(charItem.characterEmoji, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Mode Navigation Tabs (Explore, Vocabulary, Mission, Speaking)
            TabRow(
                selectedTabIndex = activeTab.ordinal,
                containerColor = Color.Transparent,
                contentColor = primaryColor,
                divider = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                CharacterTab.entries.forEach { tab ->
                    val isSelected = activeTab == tab
                    val tabTitle = when (tab) {
                        CharacterTab.EXPLORE -> "🌟 Meet"
                        CharacterTab.VOCABULARY -> "📖 Words"
                        CharacterTab.MISSION -> "🎯 Quest"
                        CharacterTab.SPEAKING -> "🗣️ Speak"
                    }

                    Tab(
                        selected = isSelected,
                        onClick = {
                            audioEngine.playClickSound()
                            activeTab = tab
                        },
                        text = {
                            Text(
                                text = tabTitle,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) primaryColor else Color(0xFF64748B)
                            )
                        }
                    )
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeTab) {
                    CharacterTab.EXPLORE -> {
                        CharacterExploreTab(
                            character = currentCharacter,
                            actionState = actionState,
                            showSpeechBubble = showSpeechBubble,
                            speechBubbleText = speechBubbleText,
                            onTriggerAction = { action, text ->
                                coroutineScope.launch {
                                    actionState = action
                                    speechBubbleText = text
                                    showSpeechBubble = true
                                    audioEngine.speak(text)
                                    delay(2200)
                                    actionState = CharacterActionState.IDLE
                                }
                            },
                            audioEngine = audioEngine
                        )
                    }
                    CharacterTab.VOCABULARY -> {
                        CharacterVocabularyTab(
                            character = currentCharacter,
                            audioEngine = audioEngine
                        )
                    }
                    CharacterTab.MISSION -> {
                        CharacterMissionTab(
                            character = currentCharacter,
                            currentMissionIndex = currentMissionIndex,
                            missionCompleted = missionCompleted,
                            onMissionAnswer = { isCorrect ->
                                if (isCorrect) {
                                    audioEngine.playCorrectSound()
                                    repository.addStars(1)
                                    repository.addCoins(5)
                                    val missions = currentCharacter.missions
                                    if (currentMissionIndex + 1 < missions.size) {
                                        currentMissionIndex++
                                    } else {
                                        missionCompleted = true
                                        showConfetti = true
                                        repository.addStars(2)
                                        repository.addCoins(10)
                                        audioEngine.playVictorySound()
                                        audioEngine.speak("Hooray! You completed all quests for ${currentCharacter.name}! You unlocked the ${currentCharacter.unlockBadgeName} badge!")
                                    }
                                } else {
                                    audioEngine.playWrongSound()
                                }
                            },
                            onReset = {
                                currentMissionIndex = 0
                                missionCompleted = false
                            },
                            audioEngine = audioEngine
                        )
                    }
                    CharacterTab.SPEAKING -> {
                        CharacterSpeakingTab(
                            character = currentCharacter,
                            isEvaluated = isSpeakingEvaluated,
                            onSpeakEvaluated = {
                                isSpeakingEvaluated = true
                                repository.addStars(1)
                                repository.addCoins(5)
                                audioEngine.playCorrectSound()
                                audioEngine.speak("Fantastic pronunciation! You said ${currentCharacter.name} so clearly!")
                            },
                            audioEngine = audioEngine
                        )
                    }
                }
            }
        }

        // Confetti Celebration Overlay
        ConfettiOverlay(
            isVisible = showConfetti,
            modifier = Modifier.fillMaxSize()
        )
    }
}

// ----------------------------------------------------
// 1. EXPLORE & INTERACT WITH 3D CHARACTER FIGURINE
// ----------------------------------------------------
@Composable
private fun CharacterExploreTab(
    character: LetterCharacter,
    actionState: CharacterActionState,
    showSpeechBubble: Boolean,
    speechBubbleText: String,
    onTriggerAction: (CharacterActionState, String) -> Unit,
    audioEngine: SpeechAndSoundEngine
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 3D Figurine Center Stage
        Character3DFigurine(
            character = character,
            actionState = actionState,
            size = 180.dp,
            showSpeechBubble = showSpeechBubble,
            speechText = speechBubbleText,
            onClick = {
                onTriggerAction(
                    CharacterActionState.REACT,
                    "Hi friend! I'm ${character.name}! Letter ${character.letter} says ${character.phonicsSound}!"
                )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Phonics Badge & Pronunciation Hero Card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 3.dp,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(character.themeColorHex).copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${character.letter} ${character.letter.lowercaseChar()}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(character.themeColorHex)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(character.themeColorHex).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Sound: ${character.phonicsSound}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(character.themeColorHex),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "\"${character.personality}\"",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF475569),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Phonics Sound Listen Button
                Button(
                    onClick = {
                        audioEngine.playClickSound()
                        audioEngine.speak("Letter ${character.letter} says ${character.phonicsSound}. ${character.phonicsExample}")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(character.themeColorHex)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Listen Phonics & Letter",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive Character Action Triggers (Wave, Jump, Walk, Celebrate)
        Text(
            text = "Play with ${character.name}!",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionPillButton(
                label = "Wave 👋",
                modifier = Modifier.weight(1f),
                onClick = {
                    onTriggerAction(CharacterActionState.WAVE, "Hello! Let's be best friends!")
                }
            )
            ActionPillButton(
                label = "Jump 🦘",
                modifier = Modifier.weight(1f),
                onClick = {
                    onTriggerAction(CharacterActionState.JUMP, "Boing! Look how high I jump!")
                }
            )
            ActionPillButton(
                label = "Walk 🚶",
                modifier = Modifier.weight(1f),
                onClick = {
                    onTriggerAction(CharacterActionState.WALK, "Step, step, let's explore together!")
                }
            )
            ActionPillButton(
                label = "Cheer 🏆",
                modifier = Modifier.weight(1f),
                onClick = {
                    onTriggerAction(CharacterActionState.CELEBRATE, "Yay! You are amazing and super smart!")
                }
            )
        }
    }
}

@Composable
private fun ActionPillButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
        }
    }
}

// ----------------------------------------------------
// 2. VOCABULARY TAB (4 to 5 Words per letter)
// ----------------------------------------------------
@Composable
private fun CharacterVocabularyTab(
    character: LetterCharacter,
    audioEngine: SpeechAndSoundEngine
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${character.name}'s Vocabulary Words",
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(character.themeColorHex)
        )
        Text(
            text = "Tap any card to hear pronunciation and example phrase!",
            fontSize = 13.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        character.vocabulary.forEach { vocab ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(character.themeColorHex).copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable {
                        audioEngine.playClickSound()
                        audioEngine.speak("${vocab.word}. ${vocab.sentence}")
                    }
                    .testTag("vocab_card_${vocab.word}")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Big Emoji Bubble
                    Surface(
                        shape = CircleShape,
                        color = Color(character.themeColorHex).copy(alpha = 0.12f),
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(vocab.emoji, fontSize = 28.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = vocab.word,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            if (vocab.phoneticSpelling.isNotBlank()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${vocab.phoneticSpelling})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(character.themeColorHex)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = vocab.sentence,
                            fontSize = 13.sp,
                            color = Color(0xFF475569)
                        )
                    }

                    IconButton(
                        onClick = {
                            audioEngine.playClickSound()
                            audioEngine.speak("${vocab.word}. ${vocab.sentence}")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Speak",
                            tint = Color(character.themeColorHex)
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 3. INTERACTIVE CHARACTER MISSION TAB
// ----------------------------------------------------
@Composable
private fun CharacterMissionTab(
    character: LetterCharacter,
    currentMissionIndex: Int,
    missionCompleted: Boolean,
    onMissionAnswer: (Boolean) -> Unit,
    onReset: () -> Unit,
    audioEngine: SpeechAndSoundEngine
) {
    val missions = character.missions
    val mission = missions.getOrElse(currentMissionIndex) { missions.first() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Mission Instructions Card
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            shadowElevation = 3.dp,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(character.themeColorHex).copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎯 ${mission.title}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(character.themeColorHex)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mission.description,
                    fontSize = 14.sp,
                    color = Color(0xFF334155),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { ((currentMissionIndex + 1).toFloat() / missions.size.toFloat()).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = Color(character.themeColorHex),
                    trackColor = Color(0xFFE2E8F0)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Quest ${currentMissionIndex + 1} of ${missions.size}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }
        }

        // Interactive Options
        if (!missionCompleted) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Choose the correct answer:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(16.dp))

                mission.options.forEachIndexed { index, option ->
                    val optionEmoji = mission.optionEmojis.getOrElse(index) { "⭐" }
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 3.dp,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(character.themeColorHex).copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(vertical = 6.dp)
                            .clickable {
                                onMissionAnswer(index == mission.correctIndex)
                            }
                            .testTag("mission_option_$index")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(optionEmoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = option,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }
                }
            }
        } else {
            // Victory State
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFFEF3C7),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFF59E0B)),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("🏆", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Quests Completed!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFB45309)
                    )
                    Text(
                        text = "Unlocked Badge: ${character.unlockBadgeName}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD97706),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onReset,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Play Quests Again", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 4. SPEAKING & PRONUNCIATION TAB
// ----------------------------------------------------
@Composable
private fun CharacterSpeakingTab(
    character: LetterCharacter,
    isEvaluated: Boolean,
    onSpeakEvaluated: () -> Unit,
    audioEngine: SpeechAndSoundEngine
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Target Phonics Card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 3.dp,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(character.themeColorHex).copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Say the word out loud:",
                    fontSize = 15.sp,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = character.name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(character.themeColorHex)
                )
                Text(
                    text = "Sound: ${character.phonicsSound}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        audioEngine.playClickSound()
                        audioEngine.speak(character.name)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(character.themeColorHex).copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Listen Target",
                        tint = Color(character.themeColorHex)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Listen First",
                        color = Color(character.themeColorHex),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Speaking Action / Mic Simulation button
        Surface(
            shape = CircleShape,
            color = Color(character.themeColorHex),
            shadowElevation = 6.dp,
            modifier = Modifier
                .size(100.dp)
                .clickable {
                    audioEngine.playClickSound()
                    onSpeakEvaluated()
                }
                .testTag("char_speaking_mic_btn")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Speak Now",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        // Result Card
        if (isEvaluated) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFECFDF5),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF10B981)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🌟 Excellent pronunciation! 100% Match!", fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                    Text("You earned +1 Star ⭐ +5 Coins 🪙", fontSize = 13.sp, color = Color(0xFF065F46))
                }
            }
        } else {
            Text(
                text = "Tap the microphone and say '${character.name}' clearly!",
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )
        }
    }
}
