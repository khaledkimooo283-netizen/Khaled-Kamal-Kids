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
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.ui.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LetterMatchPair(
    val id: Int,
    val uppercase: String,
    val lowercase: String,
    val emoji: String,
    val word: String,
    var isMatched: Boolean = false
)

@Composable
fun CapitalSmallMatchScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var userStars by remember { mutableIntStateOf(repository.getStars()) }

    var selectedUppercase by remember { mutableStateOf<LetterMatchPair?>(null) }
    var selectedLowercase by remember { mutableStateOf<String?>(null) }

    var score by remember { mutableIntStateOf(0) }
    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    var uppercaseCards by remember { mutableStateOf(listOf<LetterMatchPair>()) }
    var lowercaseChoices by remember { mutableStateOf(listOf<String>()) }

    val allLetters = remember { HandwritingData.uppercaseLetters }

    fun setupNewRound() {
        selectedUppercase = null
        selectedLowercase = null

        val roundItems = allLetters.shuffled().take(4).mapIndexed { idx, item ->
            LetterMatchPair(
                id = idx,
                uppercase = item.character,
                lowercase = item.character.lowercase(),
                emoji = item.emoji,
                word = item.word,
                isMatched = false
            )
        }

        uppercaseCards = roundItems
        lowercaseChoices = roundItems.map { it.lowercase }.shuffled()

        audioEngine.speak("Match Capital letters with Small letters! 🅰️ ↔️ a")
    }

    LaunchedEffect(Unit) {
        setupNewRound()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFF6FF)) // Clean Soft Sky Blue
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Capital ↔ Small Letters 🅰️a",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Prompt Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF2563EB))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tap a Capital letter, then tap its Small letter!",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Capital Letters Column
            Text(
                text = "Capital Letters (A - Z)",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E3A8A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                uppercaseCards.forEach { item ->
                    val isSelected = selectedUppercase?.id == item.id
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(85.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                when {
                                    item.isMatched -> Color(0xFF86EFAC)
                                    isSelected -> Color(0xFF93C5FD)
                                    else -> Color.White
                                }
                            )
                            .border(
                                width = if (isSelected || item.isMatched) 3.dp else 1.5.dp,
                                color = if (item.isMatched) Color(0xFF16A34A) else if (isSelected) Color(0xFF2563EB) else Color(0xFFCBD5E1),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable(enabled = !item.isMatched) {
                                selectedUppercase = item
                                audioEngine.speak("Capital ${item.uppercase}!")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = item.uppercase,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = if (item.isMatched) Color(0xFF14532D) else Color(0xFF1E293B)
                            )
                            if (item.isMatched) {
                                Text(text = item.emoji, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Small Letters Options
            Text(
                text = "Small Letters (a - z)",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E3A8A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                lowercaseChoices.forEach { smallLetter ->
                    val isMatchedInList = uppercaseCards.any { it.lowercase == smallLetter && it.isMatched }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(85.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isMatchedInList) Color(0xFF86EFAC) else Color.White)
                            .border(
                                width = if (isMatchedInList) 3.dp else 1.5.dp,
                                color = if (isMatchedInList) Color(0xFF16A34A) else Color(0xFFCBD5E1),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable(enabled = selectedUppercase != null && !isMatchedInList) {
                                selectedUppercase?.let { upper ->
                                    if (upper.lowercase == smallLetter) {
                                        // Match Correct!
                                        uppercaseCards = uppercaseCards.map {
                                            if (it.id == upper.id) it.copy(isMatched = true) else it
                                        }
                                        selectedUppercase = null
                                        audioEngine.speakPraise()
                                        audioEngine.speak("${upper.uppercase} is for ${upper.word} ${upper.emoji}!")

                                        if (uppercaseCards.all { it.isMatched }) {
                                            score++
                                            coroutineScope.launch {
                                                delay(1200)
                                                if (score >= 3) {
                                                    repository.addStars(5)
                                                    userStars = repository.getStars()
                                                    showConfetti = true
                                                    showRewardDialog = true
                                                } else {
                                                    setupNewRound()
                                                }
                                            }
                                        }
                                    } else {
                                        // Match Incorrect
                                        audioEngine.speak("Try again! Capital ${upper.uppercase} matches small ${upper.lowercase}!")
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = smallLetter,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isMatchedInList) Color(0xFF14532D) else Color(0xFF2563EB)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = selectedUppercase?.let { "Find small '${it.lowercase}'!" } ?: "Match Capital & Small letters!",
                onClick = {
                    audioEngine.speak("Match Capital letters like A with Small letters like a!")
                }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Letter Master! 🅰️a⭐",
            message = "You matched all capital and small letters perfectly!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                score = 0
                setupNewRound()
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}
