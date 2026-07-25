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

data class SpaceStar(
    val id: Int,
    val charOrNum: String,
    val isTarget: Boolean,
    val xBias: Float,
    val yBias: Float,
    val color: Color
)

@Composable
fun SpaceAdventureScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var targetChar by remember { mutableStateOf("S") }
    var collectedCount by remember { mutableIntStateOf(0) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    fun nextMission() {
        val letter = repository.alphabetList.random().letter.toString()
        targetChar = letter
        audioEngine.speak("Space Mission! Fly your rocket to collect star $targetChar! 🚀")
    }

    LaunchedEffect(Unit) {
        nextMission()
    }

    val stars = remember(targetChar) {
        val colors = listOf(Color(0xFFFFD54F), Color(0xFF4FC3F7), Color(0xFFE040FB), Color(0xFF69F0AE))
        val list = mutableListOf<SpaceStar>()

        // Target Star
        list.add(
            SpaceStar(
                id = 0, charOrNum = targetChar, isTarget = true,
                xBias = -0.5f, yBias = -0.4f, color = colors[0]
            )
        )

        val distractors = repository.alphabetList.filter { it.letter.toString() != targetChar }.map { it.letter.toString() }.shuffled()

        repeat(3) { idx ->
            list.add(
                SpaceStar(
                    id = idx + 1,
                    charOrNum = distractors.getOrElse(idx) { "Z" },
                    isTarget = false,
                    xBias = (idx - 1) * 0.5f,
                    yBias = 0.2f + (idx % 2) * 0.3f,
                    color = colors[(idx + 1) % colors.size]
                )
            )
        }
        list.shuffled()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D47A1)) // Deep Space Blue
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Space Adventure 🚀",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Mission Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1E88E5))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Space Mission: Collect Star '$targetChar' 🌟",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Space Galaxy Area
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .weight(1f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFF1565C0))
            ) {
                // Rocket at bottom
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                ) {
                    Text(text = "🚀", fontSize = 56.sp)
                }

                // Space Stars Floating
                stars.forEach { star ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .align(BiasAlignment(star.xBias, star.yBias))
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(star.color)
                                .clickable {
                                    if (star.isTarget) {
                                        collectedCount++
                                        audioEngine.speakPraise()
                                        audioEngine.speak("Star $targetChar collected!")

                                        if (collectedCount >= 3) {
                                            showConfetti = true
                                            repository.addStars(5)
                                            userStars = repository.getStars()
                                            showRewardDialog = true
                                        } else {
                                            nextMission()
                                        }
                                    } else {
                                        audioEngine.speakTryAgain()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "⭐", fontSize = 24.sp)
                                Text(
                                    text = star.charOrNum,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0D47A1)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Collect star '$targetChar'!",
                onClick = { audioEngine.speak("Tap the cosmic star with letter $targetChar!") }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Space Astronaut!",
            message = "You collected all target stars in space!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                collectedCount = 0
                nextMission()
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}
