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
import androidx.compose.material.icons.filled.VolumeUp
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
                title = "Listen & Choose 🎧",
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
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.VolumeUp,
                            contentDescription = "Listen Audio",
                            tint = Color(0xFFA855F7),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Tap Speaker to Hear:",
                            fontSize = 12.sp,
                            color = Color(0xFFE9D5FF)
                        )
                        Text(
                            text = "Find '${currentTarget?.word ?: ""}'",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Choice Picture Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .weight(1f)
            ) {
                items(choices.size) { idx ->
                    val item = choices[idx]
                    val isSelected = selectedItem == item
                    val isCorrect = item.character == currentTarget?.character

                    Box(
                        modifier = Modifier
                            .height(130.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                when {
                                    isSelected && isCorrect -> Color(0xFF86EFAC)
                                    isSelected && !isCorrect -> Color(0xFFFCA5A5)
                                    else -> Color.White
                                }
                            )
                            .border(
                                width = if (isSelected) 3.dp else 1.5.dp,
                                color = if (isSelected && isCorrect) Color(0xFF16A34A) else if (isSelected) Color(0xFFDC2626) else Color(0xFFE9D5FF),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .clickable {
                                selectedItem = item

                                if (isCorrect) {
                                    score++
                                    audioEngine.speakPraise()
                                    audioEngine.speak("Yes! ${item.character} is for ${item.word}!")

                                    coroutineScope.launch {
                                        delay(1400)
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
                                    audioEngine.speak("That is ${item.word}! Listen again for ${currentTarget?.word}!")
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = item.emoji, fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.word,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF581C87)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = currentTarget?.let { "Find '${it.word}'!" } ?: "Listen to the word!",
                onClick = {
                    currentTarget?.let {
                        audioEngine.speak("Find ${it.word}!")
                    }
                }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Listening Star! 🎧⭐",
            message = "You identified all spoken vocabulary correctly!",
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
