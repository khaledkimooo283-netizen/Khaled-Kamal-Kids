package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.HandwritingData
import com.example.data.KkDataRepository
import com.example.data.TracingGuideItem
import com.example.ui.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ListenAndChooseScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var selectedPhonicsTab by remember { mutableStateOf("listen_choose") } // "listen_choose", "missing_letter", "typing"

    Column(modifier = Modifier.fillMaxSize()) {
        // Hub Top Tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF581C87))
                .padding(vertical = 6.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                Triple("listen_choose", "Listen & Pick", "🎧"),
                Triple("missing_letter", "Missing Letter", "🔍"),
                Triple("typing", "Word Builder", "⌨️")
            ).forEach { (modeId, modeTitle, emoji) ->
                val isSelected = selectedPhonicsTab == modeId
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) Color(0xFFA855F7) else Color(0xFF6B21A8))
                        .clickable {
                            selectedPhonicsTab = modeId
                            audioEngine.speak("$modeTitle!")
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$emoji $modeTitle",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (selectedPhonicsTab) {
                "listen_choose" -> ListenAndChooseContent(repository, audioEngine, onBackClick)
                "missing_letter" -> MissingLetterScreen(repository, audioEngine, onBackClick)
                "typing" -> TypingGameScreen(repository, audioEngine, onBackClick)
            }
        }
    }
}

@Composable
fun ListenAndChooseContent(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var userStars by remember { mutableIntStateOf(repository.getStars()) }

    var currentTarget by remember { mutableStateOf<TracingGuideItem?>(null) }
    var choices by remember { mutableStateOf(listOf<TracingGuideItem>()) }
    var selectedItem by remember { mutableStateOf<TracingGuideItem?>(null) }
    var score by remember { mutableIntStateOf(0) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    val alphabet = remember { HandwritingData.uppercaseLetters }

    fun setupNewAudioQuestion() {
        selectedItem = null
        val target = alphabet.random()
        currentTarget = target

        val distractors = alphabet.filter { it.character != target.character }.shuffled().take(3)
        choices = (distractors + target).shuffled()

        audioEngine.speak("Listen! Can you find ${target.word}? Tap the picture!")
    }

    LaunchedEffect(Unit) {
        setupNewAudioQuestion()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF5FF)) // Clean Light Purple
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Phonics & Listening Hub 🎧",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Audio Speaker Prompt Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(vertical = 12.dp)
                    .clickable {
                        currentTarget?.let {
                            audioEngine.speak("Find ${it.word}!")
                        }
                    },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFA855F7)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Speaker",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Tap Speaker to Replay Sound 🔊",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Choices Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .weight(1f)
            ) {
                items(choices.size) { index ->
                    val item = choices[index]
                    val isSelected = selectedItem == item
                    val isCorrect = item.character == currentTarget?.character

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(115.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                when {
                                    isSelected && isCorrect -> Color(0xFF86EFAC)
                                    isSelected && !isCorrect -> Color(0xFFFCA5A5)
                                    else -> Color.White
                                }
                            )
                            .border(
                                width = 2.dp,
                                color = when {
                                    isSelected && isCorrect -> Color(0xFF16A34A)
                                    isSelected && !isCorrect -> Color(0xFFDC2626)
                                    else -> Color(0xFFE9D5FF)
                                },
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                selectedItem = item
                                if (isCorrect) {
                                    audioEngine.speakPraise()
                                    audioEngine.speak("${item.word}! Great job!")
                                    score++
                                    coroutineScope.launch {
                                        delay(1000)
                                        if (score >= 4) {
                                            repository.addStars(5)
                                            userStars = repository.getStars()
                                            showConfetti = true
                                            showRewardDialog = true
                                        } else {
                                            setupNewAudioQuestion()
                                        }
                                    }
                                } else {
                                    audioEngine.speak("Try again! Listen closely for ${currentTarget?.word}!")
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = item.emoji, fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.word,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B21A8)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Listen to the word & tap the picture!",
                onClick = {
                    currentTarget?.let {
                        audioEngine.speak("Tap the picture for ${it.word}!")
                    }
                }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Listening Master! 🎧⭐",
            message = "You identified all spoken vocabulary words!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                score = 0
                setupNewAudioQuestion()
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}

