package com.example.ui.games

import androidx.compose.foundation.background
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

data class DinoEgg(
    val id: Int,
    val text: String,
    val isTarget: Boolean,
    var isHatched: Boolean = false
)

@Composable
fun DinoHatchScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var targetText by remember { mutableStateOf("1") }
    var eggs by remember { mutableStateOf(listOf<DinoEgg>()) }
    var hatchedCount by remember { mutableIntStateOf(0) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    fun setupNests() {
        val target = repository.numberList.random().number.toString()
        targetText = target

        val distractors = repository.numberList.filter { it.number.toString() != targetText }.shuffled().take(2).map { it.number.toString() }
        val allNumbers = (distractors + targetText).shuffled()

        eggs = allNumbers.mapIndexed { idx, num ->
            DinoEgg(idx, num, num == targetText, false)
        }

        audioEngine.speak("Dino Adventure! Tap the dinosaur egg with number $targetText to hatch a baby dino! 🦖🥚")
    }

    LaunchedEffect(Unit) {
        setupNests()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEBE9))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Dino Hatch 🦖",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF6D4C41))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Hatch Dino Egg with Number '$targetText' 🦖",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Egg Nest Row
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                eggs.forEach { egg ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(if (egg.isHatched) Color(0xFFA1887F) else Color(0xFFD7CCC8))
                            .clickable {
                                if (!egg.isHatched) {
                                    eggs = eggs.map { if (it.id == egg.id) it.copy(isHatched = true) else it }
                                    if (egg.isTarget) {
                                        hatchedCount++
                                        audioEngine.speakPraise()
                                        audioEngine.speak("Roar! You hatched baby dino $targetText!")

                                        if (hatchedCount >= 3) {
                                            showConfetti = true
                                            repository.addStars(5)
                                            userStars = repository.getStars()
                                            showRewardDialog = true
                                        } else {
                                            setupNests()
                                        }
                                    } else {
                                        audioEngine.speak("Oops, wrong egg! Keep looking!")
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (egg.isHatched) (if (egg.isTarget) "🦕" else "💥") else "🥚",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = egg.text,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF3E2723),
                                maxLines = 1,
                                softWrap = false,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Hatch egg '$targetText'!",
                onClick = { audioEngine.speak("Tap egg with number $targetText!") }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Dino Explorer!",
            message = "You hatched all cute baby dinosaurs!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                hatchedCount = 0
                setupNests()
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}
