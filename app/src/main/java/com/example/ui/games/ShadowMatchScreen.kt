package com.example.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
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
    var isRevealed by remember { mutableStateOf(false) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    val allSourceItems = remember {
        HandwritingData.uppercaseLetters + HandwritingData.lowercaseLetters
    }

    fun nextQuestion() {
        isRevealed = false
        val target = allSourceItems.random()
        targetItem = target

        val distractors = allSourceItems
            .filter { it.character != target.character && it.word != target.word }
            .shuffled()
            .take(3)

        optionList = (distractors + target).shuffled()

        audioEngine.speak("Shadow Match! Which real picture matches this dark shadow silhouette? 👤")
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
                title = "Shadow Silhouette Match 👤",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dark Silhouette Card
            targetItem?.let { target ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(130.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isRevealed) Color(0xFFE1BEE7) else Color(0xFF37474F))
                        .border(4.dp, if (isRevealed) Color(0xFFAB47BC) else Color(0xFF1F2937), RoundedCornerShape(24.dp))
                        .clickable {
                            audioEngine.speak("Can you guess the shadow for ${target.word}?")
                        }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isRevealed) {
                            Text(text = target.emoji, fontSize = 54.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${target.character} - ${target.word}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF4A148C)
                            )
                        } else {
                            // Dark Silhouette Representation
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF101827)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = target.emoji,
                                    fontSize = 42.sp,
                                    modifier = Modifier.background(Color.Transparent)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Mystery Shadow 👤",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tap the colorful picture that matches the shadow above:",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF4A148C),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                                    .height(105.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White)
                                    .border(2.5.dp, Color(0xFFCE93D8), RoundedCornerShape(20.dp))
                                    .clickable(enabled = !isRevealed) {
                                        if (option.character == targetItem?.character && option.word == targetItem?.word) {
                                            isRevealed = true
                                            score++
                                            audioEngine.speakPraise()
                                            audioEngine.speak("Matched! That shadow belongs to ${option.word} ${option.emoji}!")

                                            if (score >= 4) {
                                                showConfetti = true
                                                repository.addStars(5)
                                                userStars = repository.getStars()
                                                showRewardDialog = true
                                            } else {
                                                // Next question after delay
                                            }
                                        } else {
                                            audioEngine.speakTryAgain()
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = option.emoji, fontSize = 38.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${option.character} - ${option.word}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4A148C)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (isRevealed && !showRewardDialog) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { nextQuestion() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E24AA)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Next Shadow ➡️", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Match the shadow shape to the real object!",
                onClick = {
                    targetItem?.let {
                        audioEngine.speak("Find the shadow of ${it.word}!")
                    }
                }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Shadow Master! 👤⭐",
            message = "You identified all shadow silhouettes!",
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

