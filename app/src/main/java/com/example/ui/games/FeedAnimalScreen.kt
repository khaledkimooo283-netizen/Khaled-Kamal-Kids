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

data class FoodCookie(
    val id: Int,
    val text: String,
    val emoji: String,
    val isTarget: Boolean
)

@Composable
fun FeedAnimalScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var targetFood by remember { mutableStateOf<FoodCookie?>(null) }
    var options by remember { mutableStateOf(listOf<FoodCookie>()) }
    var fedCount by remember { mutableIntStateOf(0) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    fun nextMeal() {
        val targetItem = repository.alphabetList.random()
        val correctCookie = FoodCookie(0, targetItem.letter.toString(), targetItem.emoji, true)

        val distractors = repository.alphabetList.filter { it.letter != targetItem.letter }.shuffled().take(2)
        val otherCookies = distractors.mapIndexed { idx, item ->
            FoodCookie(idx + 1, item.letter.toString(), item.emoji, false)
        }

        options = (otherCookies + correctCookie).shuffled()
        targetFood = correctCookie

        audioEngine.speak("Hungry Lion! Feed me cookie with letter ${correctCookie.text}! 🦁🍪")
    }

    LaunchedEffect(Unit) {
        nextMeal()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F5E9)) // Fresh Garden Green
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Feed the Animal 🦁",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Lion Eating Box
            targetFood?.let { target ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🦁", fontSize = 72.sp)
                        Text(
                            text = "Feed me cookie: '${target.text}' (${target.emoji})!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF2E7D32),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tap the correct cookie to feed the lion:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Food Options
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                options.forEach { cookie ->
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFFFFB74D))
                            .clickable {
                                if (cookie.isTarget) {
                                    fedCount++
                                    audioEngine.speakPraise()
                                    audioEngine.speak("Yum yum! Lion loved cookie ${cookie.text}!")

                                    if (fedCount >= 3) {
                                        showConfetti = true
                                        repository.addStars(4)
                                        userStars = repository.getStars()
                                        showRewardDialog = true
                                    } else {
                                        nextMeal()
                                    }
                                } else {
                                    audioEngine.speakTryAgain()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = cookie.emoji, fontSize = 32.sp)
                            Text(
                                text = cookie.text,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Yum yum, so delicious!",
                onClick = { audioEngine.speak("Feed the lion cookie ${targetFood?.text}!") }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Master Chef!",
            message = "You fed the hungry lion all delicious cookies!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                fedCount = 0
                nextMeal()
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}
