package com.example.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
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

data class FloatingBalloon(
    val id: Int,
    val text: String,
    val isTarget: Boolean,
    val color: Color,
    val xBias: Float,
    val initialYBias: Float
)

@Composable
fun BalloonPopScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var mode by remember { mutableStateOf("letters") }
    var userStars by remember { mutableIntStateOf(repository.getStars()) }

    var targetItem by remember { mutableStateOf("A") }
    var poppedCount by remember { mutableIntStateOf(0) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    fun nextRound() {
        targetItem = if (mode == "letters") {
            repository.alphabetList.random().letter.toString()
        } else {
            repository.numberList.random().number.toString()
        }
        audioEngine.speak("Pop balloon $targetItem! 🎈")
    }

    LaunchedEffect(mode) {
        nextRound()
    }

    val balloons = remember(targetItem, mode) {
        val colors = listOf(
            Color(0xFFFF5252), Color(0xFFFFB74D), Color(0xFF42A5F5),
            Color(0xFF66BB6A), Color(0xFFAB47BC), Color(0xFF26C6DA)
        )
        val list = mutableListOf<FloatingBalloon>()

        // 1 target
        list.add(
            FloatingBalloon(
                id = 0, text = targetItem, isTarget = true,
                color = colors[0], xBias = -0.6f, initialYBias = 0.5f
            )
        )

        val distractors = if (mode == "letters") {
            repository.alphabetList.filter { it.letter.toString() != targetItem }.map { it.letter.toString() }.shuffled()
        } else {
            repository.numberList.filter { it.number.toString() != targetItem }.map { it.number.toString() }.shuffled()
        }

        repeat(4) { idx ->
            list.add(
                FloatingBalloon(
                    id = idx + 1,
                    text = distractors.getOrElse(idx) { "Z" },
                    isTarget = false,
                    color = colors[(idx + 1) % colors.size],
                    xBias = (idx - 1.5f) * 0.45f,
                    initialYBias = (idx % 2) * 0.4f - 0.2f
                )
            )
        }
        list.shuffled()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8EAF6))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Balloon Pop! 🎈",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Mode Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { mode = "letters" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (mode == "letters") Color(0xFF3F51B5) else Color(0xFFC5CAE9)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Letters", color = if (mode == "letters") Color.White else Color(0xFF1A237E), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = { mode = "numbers" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (mode == "numbers") Color(0xFF3F51B5) else Color(0xFFC5CAE9)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Numbers", color = if (mode == "numbers") Color.White else Color(0xFF1A237E), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Target Prompt Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Pop Balloon:  $targetItem",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF283593),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sky Area for Floating Balloons
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .weight(1f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFFBBDEFB))
            ) {
                balloons.forEach { balloon ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .align(
                                    BiasAlignment(
                                        horizontalBias = balloon.xBias,
                                        verticalBias = balloon.initialYBias
                                    )
                                )
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(balloon.color)
                                .clickable {
                                    if (balloon.isTarget) {
                                        poppedCount++
                                        audioEngine.speakPraise()
                                        audioEngine.speak("Pop $targetItem!")

                                        if (poppedCount >= 3) {
                                            showConfetti = true
                                            repository.addStars(4)
                                            userStars = repository.getStars()
                                            showRewardDialog = true
                                        } else {
                                            nextRound()
                                        }
                                    } else {
                                        audioEngine.speakTryAgain()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🎈", fontSize = 28.sp)
                                Text(
                                    text = balloon.text,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mascot Guide
            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Pop balloon '$targetItem'!",
                onClick = { audioEngine.speak("Pop the balloon with $targetItem!") }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Balloon Master!",
            message = "You popped all target balloons!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                poppedCount = 0
                nextRound()
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}
