package com.example.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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

@Composable
fun IceCreamShopScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var targetScoopCount by remember { mutableIntStateOf(3) }
    var currentScoops by remember { mutableStateOf(listOf<String>()) }
    var userStars by remember { mutableIntStateOf(repository.getStars()) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    fun nextOrder() {
        targetScoopCount = (1..5).random()
        currentScoops = emptyList()
        audioEngine.speak("Welcome to KK Ice Cream Shop! Please serve $targetScoopCount scoops! 🍦")
    }

    LaunchedEffect(Unit) {
        nextOrder()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCE4EC))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Ice Cream Shop 🍦",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Order Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Order: Add $targetScoopCount Ice Cream Scoops!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFC2185B),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ice Cream Stacking Area
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(200.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    // Scoops Stacked
                    currentScoops.forEach { flavor ->
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF8BBD0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = flavor, fontSize = 28.sp)
                        }
                    }

                    // Cone
                    Text(text = "🍦", fontSize = 54.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Tap flavor scoops to stack: (${currentScoops.size} / $targetScoopCount)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF880E4F)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Flavor Options
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("🍓" to "Strawberry", "🍫" to "Chocolate", "🌿" to "Mint", "🍌" to "Banana").forEach { (emoji, flavorName) ->
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable {
                                if (currentScoops.size < targetScoopCount) {
                                    currentScoops = currentScoops + emoji
                                    audioEngine.speak("${currentScoops.size}!")

                                    if (currentScoops.size == targetScoopCount) {
                                        showConfetti = true
                                        repository.addStars(4)
                                        userStars = repository.getStars()
                                        audioEngine.speakPraise()
                                        audioEngine.speak("Yummy $targetScoopCount scoops ice cream!")
                                        showRewardDialog = true
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 32.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Mascot Guide
            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Yummy ice cream!",
                onClick = { audioEngine.speak("Tap the flavors to make $targetScoopCount scoops!") }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Master Chef!",
            message = "You made a delicious $targetScoopCount scoop ice cream cone!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                nextOrder()
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}
