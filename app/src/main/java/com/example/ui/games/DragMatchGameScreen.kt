package com.example.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.KkDataRepository
import com.example.data.MatchPair
import com.example.ui.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DragMatchGameScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var category by remember { mutableStateOf("letters") } // "letters", "case", or "numbers"
    var roundSeed by remember { mutableIntStateOf(0) }

    var selectedPromptPair by remember { mutableStateOf<MatchPair?>(null) }
    var wrongMatchLeftId by remember { mutableStateOf<String?>(null) }
    var wrongMatchRightId by remember { mutableStateOf<String?>(null) }
    var matchedPairIds by remember { mutableStateOf(setOf<String>()) }

    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    // Generate round dynamically with full validation
    val currentRound = remember(category, roundSeed) {
        repository.generateMatchRound(category, count = 4)
    }

    val leftColumnPairs = currentRound.leftPairs
    val rightColumnPairs = currentRound.rightPairs

    LaunchedEffect(category, roundSeed) {
        matchedPairIds = emptySet()
        selectedPromptPair = null
        wrongMatchLeftId = null
        wrongMatchRightId = null
        when (category) {
            "letters" -> audioEngine.speak("Match the letters to the right picture! 🍎")
            "case" -> audioEngine.speak("Match big capital letters to small letters! 🔤")
            else -> audioEngine.speak("Match the numbers to the right count! 🔢")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8E1))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Match & Learn 🎯",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Category Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = {
                        category = "letters"
                        roundSeed++
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (category == "letters") Color(0xFFFF9800) else Color(0xFFFFE0B2)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                ) {
                    Text("Letters", color = if (category == "letters") Color.White else Color(0xFFE65100), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        category = "case"
                        roundSeed++
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (category == "case") Color(0xFFFF9800) else Color(0xFFFFE0B2)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1.1f),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                ) {
                    Text("Big ↔ Small", color = if (category == "case") Color.White else Color(0xFFE65100), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        category = "numbers"
                        roundSeed++
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (category == "numbers") Color(0xFFFF9800) else Color(0xFFFFE0B2)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                ) {
                    Text("Numbers", color = if (category == "numbers") Color.White else Color(0xFFE65100), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tap a item on the left, then tap its match on the right!",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF795548),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Two Matching Columns
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Column (Prompts)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    leftColumnPairs.forEach { pair ->
                        val isMatched = matchedPairIds.contains(pair.id)
                        val isSelected = selectedPromptPair?.id == pair.id
                        val isWrong = wrongMatchLeftId == pair.id

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .border(
                                    width = if (isMatched || isSelected || isWrong) 3.dp else 1.5.dp,
                                    color = when {
                                        isMatched -> Color(0xFF16A34A)
                                        isWrong -> Color(0xFFDC2626)
                                        isSelected -> Color(0xFFF59E0B)
                                        else -> Color(0xFFCBD5E1)
                                    },
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .clickable(enabled = !isMatched && wrongMatchLeftId == null) {
                                    selectedPromptPair = pair
                                    audioEngine.speak(pair.promptText)
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isMatched -> Color(0xFFDCFCE7)
                                    isWrong -> Color(0xFFFEE2E2)
                                    isSelected -> Color(0xFFFEF3C7)
                                    else -> Color.White
                                }
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(text = pair.promptEmoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = pair.promptText,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = when {
                                        isMatched -> Color(0xFF15803D)
                                        isWrong -> Color(0xFFB91C1C)
                                        else -> Color(0xFF37474F)
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Right Column (Matches)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    rightColumnPairs.forEach { pair ->
                        val isMatched = matchedPairIds.contains(pair.id)
                        val isWrong = wrongMatchRightId == pair.id

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .border(
                                    width = if (isMatched || isWrong) 3.dp else 1.5.dp,
                                    color = when {
                                        isMatched -> Color(0xFF16A34A)
                                        isWrong -> Color(0xFFDC2626)
                                        else -> Color(0xFFCBD5E1)
                                    },
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .clickable(enabled = !isMatched && wrongMatchRightId == null) {
                                    if (selectedPromptPair != null) {
                                        if (selectedPromptPair?.id == pair.id) {
                                            // Correct Match by unique ID!
                                            matchedPairIds = matchedPairIds + pair.id
                                            selectedPromptPair = null
                                            audioEngine.speakPraise()
                                            audioEngine.speak("${pair.matchText}!")

                                            if (matchedPairIds.size == currentRound.pairs.size) {
                                                showConfetti = true
                                                repository.addStars(5)
                                                userStars = repository.getStars()
                                                showRewardDialog = true
                                            }
                                        } else {
                                            // Wrong Match! Show red animation and wrong sound, keep cards available!
                                            wrongMatchLeftId = selectedPromptPair?.id
                                            wrongMatchRightId = pair.id
                                            audioEngine.speakTryAgain()
                                            coroutineScope.launch {
                                                delay(600)
                                                selectedPromptPair = null
                                                wrongMatchLeftId = null
                                                wrongMatchRightId = null
                                            }
                                        }
                                    } else {
                                        audioEngine.speak(pair.matchText)
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isMatched -> Color(0xFFDCFCE7)
                                    isWrong -> Color(0xFFFEE2E2)
                                    else -> Color.White
                                }
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(text = pair.matchEmoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = pair.matchText,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isMatched -> Color(0xFF15803D)
                                        isWrong -> Color(0xFFB91C1C)
                                        else -> Color(0xFF37474F)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mascot Guide
            KkLionMascot(
                state = if (matchedPairIds.size == currentRound.pairs.size) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = if (matchedPairIds.size == currentRound.pairs.size) "You matched them all!" else "Tap pair to match! 🧩",
                onClick = { audioEngine.speak("Match the pictures with the correct letters or numbers!") }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Master Matcher!",
            message = "You matched all ${category.replaceFirstChar { it.uppercase() }} correctly!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                roundSeed++
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}

