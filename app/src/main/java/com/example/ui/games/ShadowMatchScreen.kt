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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.ui.components.*

data class ShadowMatchItem(
    val id: String,
    val name: String,
    val emoji: String
)

@Composable
fun ShadowMatchScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var currentItem by remember { mutableStateOf<ShadowMatchItem?>(null) }
    var candidateOptions by remember { mutableStateOf(listOf<ShadowMatchItem>()) }
    var matchedCount by remember { mutableIntStateOf(0) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    val itemList = remember {
        listOf(
            ShadowMatchItem("lion", "Lion", "🦁"),
            ShadowMatchItem("cat", "Cat", "🐱"),
            ShadowMatchItem("dog", "Dog", "🐶"),
            ShadowMatchItem("duck", "Duck", "🦆"),
            ShadowMatchItem("apple", "Apple", "🍎"),
            ShadowMatchItem("car", "Car", "🚗"),
            ShadowMatchItem("rocket", "Rocket", "🚀"),
            ShadowMatchItem("star", "Star", "⭐")
        )
    }

    fun newRound() {
        val target = itemList.random()
        currentItem = target
        val distractors = itemList.filter { it.id != target.id }.shuffled().take(2)
        candidateOptions = (distractors + target).shuffled()

        audioEngine.speak("Can you find which character matches this dark shadow? 🕵️")
    }

    LaunchedEffect(Unit) {
        newRound()
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
                title = "Shadow Matching 👤",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Shadow Display Box
            currentItem?.let { target ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(180.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .border(3.dp, Color(0xFFAB47BC), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Dark Shadow Emoji
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF263238)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "❓", fontSize = 44.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Who is this shadow?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF6A1B9A)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tap the matching picture below!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A148C)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Options Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                candidateOptions.forEach { option ->
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .border(2.dp, Color(0xFFCE93D8), RoundedCornerShape(20.dp))
                            .clickable {
                                if (option.id == currentItem?.id) {
                                    matchedCount++
                                    audioEngine.speakPraise()
                                    audioEngine.speak("Yes! It is the ${option.name}!")

                                    if (matchedCount >= 3) {
                                        showConfetti = true
                                        repository.addStars(4)
                                        userStars = repository.getStars()
                                        showRewardDialog = true
                                    } else {
                                        newRound()
                                    }
                                } else {
                                    audioEngine.speakTryAgain()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = option.emoji, fontSize = 36.sp)
                            Text(text = option.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4A148C))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Match the shadow!",
                onClick = { audioEngine.speak("Look at the dark shadow and tap the matching item!") }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Shadow Detective!",
            message = "You matched all shadow silhouettes perfectly!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                matchedCount = 0
                newRound()
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}
