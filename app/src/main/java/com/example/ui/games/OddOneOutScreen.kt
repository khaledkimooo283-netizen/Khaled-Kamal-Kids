package com.example.ui.games

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
import com.example.ui.components.*

data class OddCardItem(
    val id: Int,
    val text: String,
    val isOdd: Boolean
)

@Composable
fun OddOneOutScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var currentOddText by remember { mutableStateOf("") }
    var cardOptions by remember { mutableStateOf(listOf<OddCardItem>()) }
    var streak by remember { mutableIntStateOf(0) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    fun setupRound() {
        val mainLetter = repository.alphabetList.random().letter.toString()
        val oddLetter = repository.alphabetList.filter { it.letter.toString() != mainLetter }.random().letter.toString()
        currentOddText = oddLetter

        val list = mutableListOf(
            OddCardItem(1, mainLetter, false),
            OddCardItem(2, mainLetter, false),
            OddCardItem(3, mainLetter, false),
            OddCardItem(4, oddLetter, true)
        ).shuffled()

        cardOptions = list
        audioEngine.speak("Find the different letter that does not match! 🔍")
    }

    LaunchedEffect(Unit) {
        setupRound()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE1F5FE))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Spot the Difference 🔍",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Clue Card
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0288D1))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Which letter is DIFFERENT from the rest?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2x2 Grid of Cards
            Column(
                modifier = Modifier.fillMaxWidth(0.88f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    cardOptions.take(2).forEach { card ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(110.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.White)
                                .border(3.dp, Color(0xFF81D4FA), RoundedCornerShape(24.dp))
                                .clickable {
                                    if (card.isOdd) {
                                        streak++
                                        audioEngine.speakPraise()
                                        audioEngine.speak("Great eagle eye! Letter $currentOddText is different!")

                                        if (streak >= 3) {
                                            showConfetti = true
                                            repository.addStars(4)
                                            userStars = repository.getStars()
                                            showRewardDialog = true
                                        } else {
                                            setupRound()
                                        }
                                    } else {
                                        audioEngine.speakTryAgain()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = card.text,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF01579B)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    cardOptions.drop(2).take(2).forEach { card ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(110.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.White)
                                .border(3.dp, Color(0xFF81D4FA), RoundedCornerShape(24.dp))
                                .clickable {
                                    if (card.isOdd) {
                                        streak++
                                        audioEngine.speakPraise()
                                        audioEngine.speak("Great eagle eye! Letter $currentOddText is different!")

                                        if (streak >= 3) {
                                            showConfetti = true
                                            repository.addStars(4)
                                            userStars = repository.getStars()
                                            showRewardDialog = true
                                        } else {
                                            setupRound()
                                        }
                                    } else {
                                        audioEngine.speakTryAgain()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = card.text,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF01579B)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Spot the odd letter!",
                onClick = { audioEngine.speak("Find the letter that does not match!") }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Eagle Eye Detective!",
            message = "You spotted all the different letters!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                streak = 0
                setupRound()
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}
