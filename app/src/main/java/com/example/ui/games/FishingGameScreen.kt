package com.example.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.ui.components.*
import kotlin.random.Random

data class SwimmingFish(
    val id: Int,
    val charOrNum: String,
    val isTarget: Boolean,
    var xRatio: Float,
    val yRatio: Float,
    val speed: Float,
    val color: Color
)

@Composable
fun FishingGameScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var mode by remember { mutableStateOf("letters") }
    var userStars by remember { mutableIntStateOf(repository.getStars()) }

    var currentTarget by remember { mutableStateOf("A") }
    var currentWord by remember { mutableStateOf("Apple") }

    var caughtCount by remember { mutableIntStateOf(0) }
    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    fun generateNewRound() {
        if (mode == "letters") {
            val item = repository.alphabetList.random()
            currentTarget = item.letter.toString()
            currentWord = item.word
            audioEngine.speak("Catch the letter $currentTarget for $currentWord! 🎣")
        } else {
            val item = repository.numberList.random()
            currentTarget = item.number.toString()
            currentWord = item.word
            audioEngine.speak("Catch the number $currentTarget! 🎣")
        }
    }

    LaunchedEffect(mode) {
        generateNewRound()
    }

    // Fish objects swimming across
    val fishList = remember(currentTarget, mode) {
        val list = mutableListOf<SwimmingFish>()
        val distractorList = if (mode == "letters") {
            repository.alphabetList.filter { it.letter.toString() != currentTarget }.map { it.letter.toString() }
        } else {
            repository.numberList.filter { it.number.toString() != currentTarget }.map { it.number.toString() }
        }.shuffled()

        val colors = listOf(Color(0xFFFF7043), Color(0xFFFFCA28), Color(0xFF26C6DA), Color(0xFFAB47BC))

        // Add target fish
        list.add(
            SwimmingFish(
                id = 0,
                charOrNum = currentTarget,
                isTarget = true,
                xRatio = Random.nextFloat() * 0.6f + 0.2f,
                yRatio = Random.nextFloat() * 0.5f + 0.25f,
                speed = 0.005f,
                color = colors[0]
            )
        )

        // Add 3 distractor fish
        repeat(3) { idx ->
            list.add(
                SwimmingFish(
                    id = idx + 1,
                    charOrNum = distractorList.getOrElse(idx) { "X" },
                    isTarget = false,
                    xRatio = Random.nextFloat() * 0.7f + 0.1f,
                    yRatio = Random.nextFloat() * 0.5f + 0.25f,
                    speed = 0.004f + Random.nextFloat() * 0.003f,
                    color = colors[(idx + 1) % colors.size]
                )
            )
        }
        list.shuffled()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Fishing Adventure 🎣",
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
                        containerColor = if (mode == "letters") Color(0xFF00ACC1) else Color(0xFFB2EBF2)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Fish Letters", color = if (mode == "letters") Color.White else Color(0xFF006064), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = { mode = "numbers" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (mode == "numbers") Color(0xFF00ACC1) else Color(0xFFB2EBF2)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Fish Numbers", color = if (mode == "numbers") Color.White else Color(0xFF006064), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Current Target Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Catch Target:  $currentTarget  ($currentWord)",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF00838F),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Pond with Fish
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .weight(1f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFF26C6DA))
            ) {
                // Pond Water Surface
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = Color(0xFF00ACC1).copy(alpha = 0.3f))
                }

                // Render Fish
                fishList.forEach { fish ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .align(
                                    BiasAlignment(
                                        horizontalBias = (fish.xRatio * 2f) - 1f,
                                        verticalBias = (fish.yRatio * 2f) - 1f
                                    )
                                )
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(fish.color)
                                .clickable {
                                    if (fish.isTarget) {
                                        // Caught Target Fish!
                                        caughtCount++
                                        audioEngine.speakPraise()
                                        audioEngine.speak("You caught $currentTarget!")

                                        if (caughtCount >= 3) {
                                            showConfetti = true
                                            repository.addStars(4)
                                            userStars = repository.getStars()
                                            showRewardDialog = true
                                        } else {
                                            generateNewRound()
                                        }
                                    } else {
                                        // Caught wrong fish
                                        audioEngine.speakTryAgain()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🐟", fontSize = 28.sp)
                                Text(
                                    text = fish.charOrNum,
                                    fontSize = 20.sp,
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
                speechBubbleText = "Catch fish '$currentTarget'!",
                onClick = { audioEngine.speak("Tap the fish swimming with '$currentTarget'!") }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Master Fisherman!",
            message = "You caught all target fish in KK Lion's bucket!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                caughtCount = 0
                generateNewRound()
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}
