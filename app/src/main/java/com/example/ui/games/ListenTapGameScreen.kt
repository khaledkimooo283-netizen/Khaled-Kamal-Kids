package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.ui.components.*

data class ListenTapOption(
    val word: String,
    val emoji: String,
    val category: String,
    val color: Color
)

@Composable
fun ListenTapGameScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var currentRound by remember { mutableIntStateOf(1) }

    val pool = remember {
        listOf(
            ListenTapOption("Apple", "🍎", "Fruit", Color(0xFFEF4444)),
            ListenTapOption("Elephant", "🐘", "Animal", Color(0xFF8B5CF6)),
            ListenTapOption("Cat", "🐱", "Animal", Color(0xFFF59E0B)),
            ListenTapOption("Dog", "🐶", "Animal", Color(0xFF10B981)),
            ListenTapOption("Rocket", "🚀", "Space", Color(0xFF3B82F6)),
            ListenTapOption("Car", "🚗", "Vehicle", Color(0xFFEC4899)),
            ListenTapOption("Sun", "☀️", "Nature", Color(0xFFEAB308)),
            ListenTapOption("Dinosaur", "🦖", "Dino", Color(0xFF84CC16)),
            ListenTapOption("Banana", "🍌", "Fruit", Color(0xFFFDE047)),
            ListenTapOption("Lion", "🦁", "Animal", Color(0xFFD97706)),
            ListenTapOption("Fish", "🐟", "Water", Color(0xFF06B6D4)),
            ListenTapOption("Star", "⭐", "Shape", Color(0xFFF59E0B))
        )
    }

    var targetItem by remember { mutableStateOf(pool.random()) }
    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    val options = remember(targetItem, currentRound) {
        val distractors = pool.filter { it.word != targetItem.word }.shuffled().take(3)
        (distractors + targetItem).shuffled()
    }

    fun playPrompt() {
        audioEngine.speak("Listen carefully! Tap the ${targetItem.word}! 🎧")
    }

    LaunchedEffect(targetItem, currentRound) {
        playPrompt()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0FDF4))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Listen & Tap 🎧",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sound Speaker Button Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clickable { playPrompt() },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF22C55E))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Replay Sound",
                        tint = Color(0xFF15803D),
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Tap Speaker to Replay Sound 🔊",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534)
                        )
                        Text(
                            text = "Which picture matches the sound?",
                            fontSize = 13.sp,
                            color = Color(0xFF15803D)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4 Picture Cards Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(options) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clickable {
                                audioEngine.speak(item.word)
                                if (item.word == targetItem.word) {
                                    audioEngine.playCorrectSound()
                                    repository.addStars(3)
                                    userStars = repository.getStars()

                                    if (currentRound >= 5) {
                                        showRewardDialog = true
                                        showConfetti = true
                                    } else {
                                        currentRound++
                                        targetItem = pool.filter { it.word != targetItem.word }.random()
                                    }
                                } else {
                                    audioEngine.playWrongSound()
                                    audioEngine.speak("That's ${item.word}! Try finding ${targetItem.word}!")
                                }
                            },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        border = androidx.compose.foundation.BorderStroke(3.dp, item.color)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = item.emoji, fontSize = 56.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = item.word, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
                        }
                    }
                }
            }

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Listen to Leo's voice!",
                onClick = { playPrompt() }
            )
        }

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Listening Master! 🎧",
            message = "You identified 5 words correctly!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                currentRound = 1
                targetItem = pool.random()
            },
            onHome = onBackClick
        )

        ConfettiOverlay(isVisible = showConfetti)
    }
}
