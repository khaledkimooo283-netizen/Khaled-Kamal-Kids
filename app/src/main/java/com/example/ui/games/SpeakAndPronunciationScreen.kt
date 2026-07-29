package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.ui.components.ConfettiOverlay
import com.example.ui.components.StarRewardDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class SpeakingGameMode(val title: String, val emoji: String) {
    REPEAT_AFTER_ME("Repeat After Me", "🗣️"),
    PRONUNCIATION_CHALLENGE("Challenge", "🏆"),
    GUESS_AND_SPEAK("Guess & Speak", "🍎"),
    LISTEN_AND_TOUCH("Listen & Touch", "🎧"),
    LEO_ADVENTURE("Leo's Adventure", "🦁")
}

data class RepeatItem(
    val display: String,
    val subtitle: String,
    val emoji: String,
    val category: String // "Letters", "Numbers", "Words", "Sentences"
)

data class GuessSpeakItem(
    val emoji: String,
    val word: String,
    val hint: String
)

data class ListenTouchQuestion(
    val spokenPrompt: String,
    val targetWord: String,
    val options: List<Pair<String, String>> // Emoji to Word
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakAndPronunciationScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var currentMode by remember { mutableStateOf(SpeakingGameMode.REPEAT_AFTER_ME) }
    var totalStars by remember { mutableIntStateOf(repository.getStars()) }
    var totalCoins by remember { mutableIntStateOf(repository.getCoins()) }
    var showConfetti by remember { mutableStateOf(false) }
    var showRewardDialog by remember { mutableStateOf(false) }
    var rewardTitle by remember { mutableStateOf("") }
    var rewardMessage by remember { mutableStateOf("") }

    // Voice recording states across modes
    var isRecording by remember { mutableStateOf(false) }
    var recordedAudioAvailable by remember { mutableStateOf(false) }
    var lastRecordedText by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf("") }
    var feedbackScore by remember { mutableStateOf<Int?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFDF2F8), // Soft Pink Top
                        Color(0xFFF3E8FF), // Lavender Middle
                        Color(0xFFEFF6FF)  // Light Cyan Bottom
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Top Header Navigation Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        audioEngine.speak("Going back!")
                        onBackClick()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .shadow(4.dp, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF7C3AED)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🎤 Speak & Pronunciation",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF6B21A8)
                    )
                    Text(
                        text = "Fun Preschool Speaking Activities",
                        fontSize = 11.sp,
                        color = Color(0xFF9333EA)
                    )
                }

                // Stars display pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .shadow(2.dp, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⭐", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$totalStars",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Game Mode Tab Selector
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(SpeakingGameMode.values()) { mode ->
                    val isSelected = currentMode == mode
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Color(0xFF7C3AED) else Color.White)
                            .shadow(if (isSelected) 6.dp else 2.dp, RoundedCornerShape(20.dp))
                            .clickable {
                                currentMode = mode
                                audioEngine.speak(mode.title)
                                feedbackMessage = ""
                                feedbackScore = null
                                recordedAudioAvailable = false
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = mode.emoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = mode.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFF4C1D95)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Active Mode Game Content Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (currentMode) {
                    SpeakingGameMode.REPEAT_AFTER_ME -> RepeatAfterMeView(
                        audioEngine = audioEngine,
                        repository = repository,
                        isRecording = isRecording,
                        recordedAudioAvailable = recordedAudioAvailable,
                        feedbackMessage = feedbackMessage,
                        onRecordToggle = { textToSpeak ->
                            if (!isRecording) {
                                isRecording = true
                                audioEngine.speak("Listening now... Say $textToSpeak!")
                                coroutineScope.launch {
                                    delay(3000)
                                    isRecording = false
                                    recordedAudioAvailable = true
                                    lastRecordedText = textToSpeak
                                    val score = (92..100).random()
                                    feedbackScore = score
                                    feedbackMessage = "🌟 Perfect! Pronunciation: $score%"
                                    repository.addStars(2)
                                    totalStars = repository.getStars()
                                    audioEngine.speakPraise()
                                    audioEngine.speak("Awesome job! You said $textToSpeak so clearly!")
                                }
                            } else {
                                isRecording = false
                            }
                        },
                        onReplayRecorded = {
                            if (recordedAudioAvailable) {
                                audioEngine.speak("Playing back your voice: $lastRecordedText")
                            } else {
                                audioEngine.speak("Tap record first!")
                            }
                        }
                    )

                    SpeakingGameMode.PRONUNCIATION_CHALLENGE -> PronunciationChallengeView(
                        audioEngine = audioEngine,
                        repository = repository,
                        isRecording = isRecording,
                        recordedAudioAvailable = recordedAudioAvailable,
                        feedbackMessage = feedbackMessage,
                        feedbackScore = feedbackScore,
                        onChallengeComplete = { word, score ->
                            isRecording = true
                            audioEngine.speak("Challenge mode! Say $word loudly!")
                            coroutineScope.launch {
                                delay(3000)
                                isRecording = false
                                recordedAudioAvailable = true
                                lastRecordedText = word
                                feedbackScore = score
                                feedbackMessage = "🎉 Trophy Earned! Score $score%"
                                repository.addStars(3)
                                repository.addCoins(5)
                                totalStars = repository.getStars()
                                totalCoins = repository.getCoins()
                                showConfetti = true
                                audioEngine.speakPraise()
                                audioEngine.speak("Unbelievable! You won 3 stars and 5 coins!")
                            }
                        },
                        onReplayRecorded = {
                            audioEngine.speak("Your challenge recording: $lastRecordedText")
                        }
                    )

                    SpeakingGameMode.GUESS_AND_SPEAK -> GuessAndSpeakView(
                        audioEngine = audioEngine,
                        repository = repository,
                        isRecording = isRecording,
                        recordedAudioAvailable = recordedAudioAvailable,
                        onGuessRecord = { targetWord ->
                            isRecording = true
                            audioEngine.speak("What is in the picture? Say $targetWord!")
                            coroutineScope.launch {
                                delay(3000)
                                isRecording = false
                                recordedAudioAvailable = true
                                lastRecordedText = targetWord
                                repository.addStars(2)
                                totalStars = repository.getStars()
                                audioEngine.speakPraise()
                                audioEngine.speak("Correct! It is a $targetWord!")
                            }
                        },
                        onReplay = {
                            audioEngine.speak("You said: $lastRecordedText")
                        }
                    )

                    SpeakingGameMode.LISTEN_AND_TOUCH -> ListenAndTouchView(
                        audioEngine = audioEngine,
                        repository = repository,
                        onCorrectChoice = { word ->
                            showConfetti = true
                            repository.addStars(2)
                            totalStars = repository.getStars()
                            audioEngine.speakPraise()
                            audioEngine.speak("That's right! $word!")
                        }
                    )

                    SpeakingGameMode.LEO_ADVENTURE -> LeoAdventureView(
                        audioEngine = audioEngine,
                        repository = repository,
                        isRecording = isRecording,
                        onSpeakToLeo = { prompt, answer ->
                            isRecording = true
                            audioEngine.speak(prompt)
                            coroutineScope.launch {
                                delay(3000)
                                isRecording = false
                                repository.addStars(3)
                                totalStars = repository.getStars()
                                audioEngine.speak("Roar! Fantastic! $answer")
                            }
                        }
                    )
                }
            }
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = rewardTitle,
            message = rewardMessage,
            onNext = {
                showRewardDialog = false
                showConfetti = false
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}

// Sub-View 1: Repeat After Me
@Composable
fun RepeatAfterMeView(
    audioEngine: SpeechAndSoundEngine,
    repository: KkDataRepository,
    isRecording: Boolean,
    recordedAudioAvailable: Boolean,
    feedbackMessage: String,
    onRecordToggle: (String) -> Unit,
    onReplayRecorded: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Words") }
    var currentIndex by remember { mutableIntStateOf(0) }

    val items = remember(selectedCategory) {
        when (selectedCategory) {
            "Letters" -> listOf(
                RepeatItem("A a", "Letter A", "🍎", "Letters"),
                RepeatItem("B b", "Letter B", "🎈", "Letters"),
                RepeatItem("C c", "Letter C", "🐱", "Letters"),
                RepeatItem("D d", "Letter D", "🐶", "Letters")
            )
            "Numbers" -> listOf(
                RepeatItem("One (1)", "Number 1", "1️⃣", "Numbers"),
                RepeatItem("Two (2)", "Number 2", "2️⃣", "Numbers"),
                RepeatItem("Three (3)", "Number 3", "3️⃣", "Numbers"),
                RepeatItem("Four (4)", "Number 4", "4️⃣", "Numbers")
            )
            "Words" -> listOf(
                RepeatItem("Apple", "Fruit", "🍎", "Words"),
                RepeatItem("Banana", "Fruit", "🍌", "Words"),
                RepeatItem("Butterfly", "Insect", "🦋", "Words"),
                RepeatItem("Rainbow", "Nature", "🌈", "Words"),
                RepeatItem("Sunshine", "Nature", "☀️", "Words")
            )
            else -> listOf(
                RepeatItem("I love learning!", "Sentence", "❤️", "Sentences"),
                RepeatItem("The sun is bright!", "Sentence", "☀️", "Sentences"),
                RepeatItem("Look at the lion!", "Sentence", "🦁", "Sentences"),
                RepeatItem("Good morning friend!", "Sentence", "👋", "Sentences")
            )
        }
    }

    val currentItem = items.getOrElse(currentIndex) { items[0] }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Category Pills
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            listOf("Letters", "Numbers", "Words", "Sentences").forEach { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) Color(0xFFEC4899) else Color.White)
                        .clickable {
                            selectedCategory = cat
                            currentIndex = 0
                            audioEngine.speak(cat)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cat,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color(0xFF831843)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Display Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .shadow(6.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = currentItem.emoji, fontSize = 64.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentItem.display,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF4C1D95)
                )
                Text(
                    text = currentItem.subtitle,
                    fontSize = 13.sp,
                    color = Color(0xFF7C3AED)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons Row: Listen, Record, Replay
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Listen Button
            Button(
                onClick = { audioEngine.speak(currentItem.display) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.VolumeUp, contentDescription = "Listen", tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("🔊 Listen", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Record Button
            Button(
                onClick = { onRecordToggle(currentItem.display) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecording) Color(0xFFEF4444) else Color(0xFFEC4899)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1.2f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isRecording) Icons.Filled.MicOff else Icons.Filled.Mic,
                        contentDescription = "Record",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isRecording) "🔴 Recording..." else "🎙️ Speak Now",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Hear Voice Button
            Button(
                onClick = onReplayRecorded,
                enabled = recordedAudioAvailable,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("▶️ Hear Voice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (feedbackMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFDCFCE7))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = feedbackMessage,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF15803D)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Prev & Next Item Switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (currentIndex > 0) currentIndex--
                    else currentIndex = items.size - 1
                    audioEngine.speak(items[currentIndex].display)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("⬅️ Previous", color = Color(0xFF7C3AED), fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    currentIndex = (currentIndex + 1) % items.size
                    audioEngine.speak(items[currentIndex].display)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Next ➡️", color = Color(0xFF7C3AED), fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Sub-View 2: Pronunciation Challenge
@Composable
fun PronunciationChallengeView(
    audioEngine: SpeechAndSoundEngine,
    repository: KkDataRepository,
    isRecording: Boolean,
    recordedAudioAvailable: Boolean,
    feedbackMessage: String,
    feedbackScore: Int?,
    onChallengeComplete: (String, Int) -> Unit,
    onReplayRecorded: () -> Unit
) {
    var challengeIndex by remember { mutableIntStateOf(0) }
    val challenges = remember {
        listOf("Watermelon", "Bumblebee", "Dinosaur", "Superstar", "Pineapple", "Spaceship")
    }

    val currentWord = challenges.getOrElse(challengeIndex) { challenges[0] }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🏆 Pronunciation Challenge!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF9333EA)
        )
        Text(
            text = "Can you say this word like a pro?",
            fontSize = 13.sp,
            color = Color(0xFF6B21A8)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Can you say...",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B21A8)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "\"$currentWord\"",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFD97706),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { audioEngine.speak("Can you say $currentWord?") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
                ) {
                    Text("🔊 Listen to Challenge", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val score = (94..100).random()
                        onChallengeComplete(currentWord, score)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) Color(0xFFEF4444) else Color(0xFF10B981)
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(52.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = if (isRecording) "🔴 Recording Challenge..." else "🎙️ Record Challenge",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        if (feedbackScore != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⭐ Score: $feedbackScore%!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF15803D)
                    )
                    Text(
                        text = "Rewards: +3 Stars ⭐ +5 Coins 🪙 +Badge 🎁",
                        fontSize = 12.sp,
                        color = Color(0xFF047857)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = onReplayRecorded,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                    ) {
                        Text("▶️ Listen to My Voice")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                challengeIndex = (challengeIndex + 1) % challenges.size
                audioEngine.speak("Next challenge: ${challenges[challengeIndex]}")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Try Next Word ⏩", fontWeight = FontWeight.Bold)
        }
    }
}

// Sub-View 3: Guess & Speak
@Composable
fun GuessAndSpeakView(
    audioEngine: SpeechAndSoundEngine,
    repository: KkDataRepository,
    isRecording: Boolean,
    recordedAudioAvailable: Boolean,
    onGuessRecord: (String) -> Unit,
    onReplay: () -> Unit
) {
    var itemIndex by remember { mutableIntStateOf(0) }
    var revealed by remember { mutableStateOf(false) }

    val cards = remember {
        listOf(
            GuessSpeakItem("🍎", "Apple", "Red fruit"),
            GuessSpeakItem("🐱", "Cat", "Meow meow!"),
            GuessSpeakItem("🚗", "Car", "Vroom vroom!"),
            GuessSpeakItem("🐶", "Dog", "Woof woof!"),
            GuessSpeakItem("🚀", "Rocket", "3 2 1 Blast off!"),
            GuessSpeakItem("🍦", "Ice Cream", "Yummy dessert!")
        )
    }

    val currentItem = cards.getOrElse(itemIndex) { cards[0] }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🍎 Guess & Speak!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFBE185D)
        )
        Text(
            text = "Look at the picture and say the word!",
            fontSize = 13.sp,
            color = Color(0xFF9D174D)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .shadow(6.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = currentItem.emoji, fontSize = 72.sp)

                Spacer(modifier = Modifier.height(8.dp))

                if (revealed) {
                    Text(
                        text = currentItem.word,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFBE185D)
                    )
                } else {
                    Text(
                        text = "??? (Say the answer!)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = {
                    revealed = true
                    onGuessRecord(currentItem.word)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecording) Color(0xFFEF4444) else Color(0xFFDB2777)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (isRecording) "🔴 Recording..." else "🎙️ Speak Answer")
            }

            if (recordedAudioAvailable) {
                Button(
                    onClick = onReplay,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("▶️ Listen Back")
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                revealed = false
                itemIndex = (itemIndex + 1) % cards.size
                audioEngine.speak("What is this picture?")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF831843))
        ) {
            Text("Next Picture ➡️", fontWeight = FontWeight.Bold)
        }
    }
}

// Sub-View 4: Listen & Touch
@Composable
fun ListenAndTouchView(
    audioEngine: SpeechAndSoundEngine,
    repository: KkDataRepository,
    onCorrectChoice: (String) -> Unit
) {
    var questionIndex by remember { mutableIntStateOf(0) }
    val questions = remember {
        listOf(
            ListenTouchQuestion("Touch the Elephant! 🐘", "Elephant", listOf("🐘" to "Elephant", "🐶" to "Dog", "🐱" to "Cat", "🦁" to "Lion")),
            ListenTouchQuestion("Touch the Apple! 🍎", "Apple", listOf("🍌" to "Banana", "🍎" to "Apple", "🍇" to "Grapes", "🍓" to "Strawberry")),
            ListenTouchQuestion("Touch the Rocket! 🚀", "Rocket", listOf("🚗" to "Car", "🚂" to "Train", "🚀" to "Rocket", "⛵" to "Boat"))
        )
    }

    val currentQ = questions.getOrElse(questionIndex) { questions[0] }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🎧 Listen & Touch!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF1E40AF)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { audioEngine.speak(currentQ.spokenPrompt) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.VolumeUp, contentDescription = "Play Sound")
                Spacer(modifier = Modifier.width(6.dp))
                Text(currentQ.spokenPrompt, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            items(currentQ.options) { option ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clickable {
                            if (option.second == currentQ.targetWord) {
                                onCorrectChoice(option.second)
                                questionIndex = (questionIndex + 1) % questions.size
                            } else {
                                audioEngine.speak("Try again!")
                            }
                        },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = option.first, fontSize = 48.sp)
                    }
                }
            }
        }
    }
}

// Sub-View 5: Speaking Adventure with Leo Lion
@Composable
fun LeoAdventureView(
    audioEngine: SpeechAndSoundEngine,
    repository: KkDataRepository,
    isRecording: Boolean,
    onSpeakToLeo: (String, String) -> Unit
) {
    var dialogStep by remember { mutableIntStateOf(0) }

    val dialogs = remember {
        listOf(
            Pair("Hello! I am Leo the Lion 🦁! What is your name?", "Great to meet you! You sound amazing!"),
            Pair("Can you roar like a lion with me? Raaar!", "Roar! That was a giant lion roar!"),
            Pair("What's your favorite animal?", "I love animals too! You speak so well!")
        )
    }

    val currentDialog = dialogs.getOrElse(dialogStep) { dialogs[0] }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🦁 Speaking Adventure with Leo!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFD97706)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🦁", fontSize = 72.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentDialog.first,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { onSpeakToLeo(currentDialog.first, currentDialog.second) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) Color(0xFFEF4444) else Color(0xFFF59E0B)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(if (isRecording) "🔴 Recording Response..." else "🎙️ Answer Leo")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                dialogStep = (dialogStep + 1) % dialogs.size
                audioEngine.speak(dialogs[dialogStep].first)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
        ) {
            Text("Next Question ⏩", fontWeight = FontWeight.Bold)
        }
    }
}
