package com.example.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

@Composable
fun ShadowMatchScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var targetItem by remember { mutableStateOf<TracingGuideItem?>(null) }
    var optionList by remember { mutableStateOf<List<TracingGuideItem>>(emptyList()) }
    var score by remember { mutableIntStateOf(0) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    val allSourceItems = remember {
        HandwritingData.uppercaseLetters + HandwritingData.lowercaseLetters
    }

    fun nextQuestion() {
        val target = allSourceItems.random()
        targetItem = target

        val distractors = allSourceItems
            .filter { it.character != target.character && it.word != target.word }
            .shuffled()
            .take(3)

        optionList = (distractors + target).shuffled()

        audioEngine.speak("Listen carefully! Touch ${target.word}! ${target.emoji}")
    }

    LaunchedEffect(Unit) {
        nextQuestion()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3E5F5))
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

            Spacer(modifier = Modifier.height(16.dp))

            // Big Audio Prompt Replay Card
            targetItem?.let { target ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .border(3.dp, Color(0xFFAB47BC), RoundedCornerShape(24.dp))
                        .clickable {
                            audioEngine.speak("Touch ${target.word}! ${target.emoji}")
                        }
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                audioEngine.speak("Touch ${target.word}! ${target.emoji}")
                            },
                            modifier = Modifier
                                .size(72.dp)
                                .background(Color(0xFF8E24AA), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.VolumeUp,
                                contentDescription = "Play Sound",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Tap Speaker to Hear Again 🔊",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF6A1B9A),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Which picture did you hear?",
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF4A148C)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2x2 Picture Choices
            Column(
                modifier = Modifier.fillMaxWidth(0.9f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val chunks = optionList.chunked(2)
                chunks.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { option ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White)
                                    .border(2.5.dp, Color(0xFFCE93D8), RoundedCornerShape(20.dp))
                                    .clickable {
                                        if (option.character == targetItem?.character && option.word == targetItem?.word) {
                                            score++
                                            audioEngine.speakPraise()
                                            audioEngine.speak("Correct! ${option.word}!")

                                            if (score >= 4) {
                                                showConfetti = true
                                                repository.addStars(5)
                                                userStars = repository.getStars()
                                                showRewardDialog = true
                                            } else {
                                                nextQuestion()
                                            }
                                        } else {
                                            audioEngine.speakTryAgain()
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = option.emoji, fontSize = 42.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${option.character} - ${option.word}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4A148C)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Listen & tap correct picture!",
                onClick = {
                    targetItem?.let {
                        audioEngine.speak("Touch ${it.word}!")
                    }
                }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Super Listener! 🎧",
            message = "You identified all spoken letters and words!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                score = 0
                nextQuestion()
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}
