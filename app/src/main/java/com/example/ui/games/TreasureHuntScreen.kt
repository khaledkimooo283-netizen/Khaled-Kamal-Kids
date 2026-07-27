package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
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
import com.example.data.TracingGuideItem
import com.example.ui.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class TreasureChestItem(
    val id: Int,
    val content: String,
    val isTarget: Boolean,
    var isOpen: Boolean = false
)

@Composable
fun TreasureHuntScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var userStars by remember { mutableIntStateOf(repository.getStars()) }

    var currentTargetItem by remember { mutableStateOf<TracingGuideItem?>(null) }
    var score by remember { mutableIntStateOf(0) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }
    var showVocabBanner by remember { mutableStateOf(false) }
    var celebrationMessage by remember { mutableStateOf("") }

    var chests by remember { mutableStateOf(listOf<TreasureChestItem>()) }
    var isRoundProcessing by remember { mutableStateOf(false) }

    val encouragements = remember {
        listOf("Excellent!", "Great Job!", "Super Finding!", "Wonderful!", "You Found It!")
    }

    val sourceLetters = remember { HandwritingData.uppercaseLetters }

    fun setupTreasureMap() {
        showVocabBanner = false
        showConfetti = false
        isRoundProcessing = false

        val target = sourceLetters.random()
        currentTargetItem = target

        val distractors = sourceLetters
            .filter { it.character != target.character }
            .shuffled()
            .take(5)
            .map { it.character }

        val allContent = (distractors + target.character).shuffled()

        chests = allContent.mapIndexed { index, letterStr ->
            TreasureChestItem(
                id = index,
                content = letterStr,
                isTarget = letterStr == target.character,
                isOpen = false
            )
        }

        audioEngine.speak("Treasure Hunt! Find the chest holding letter ${target.character}! 🏴‍☠️")
    }

    LaunchedEffect(Unit) {
        setupTreasureMap()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF3E0)) // Warm Beach Sand Tone
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Treasure Hunt 💎",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Mission Clue Banner
            currentTargetItem?.let { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFB8C00))
                        .border(3.dp, Color(0xFFE65100), RoundedCornerShape(20.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Find Treasure Chest: '${item.character}'",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "💎", fontSize = 24.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vocabulary Reinforcement Overlay Card during Correct Celebration
            AnimatedVisibility(
                visible = showVocabBanner && currentTargetItem != null,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                currentTargetItem?.let { target ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.88f)
                            .padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = target.emoji,
                                fontSize = 42.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = celebrationMessage,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFD97706)
                                )
                                Text(
                                    text = "${target.character} is for ${target.word}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }
                    }
                }
            }

            // Grid of Chests
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .weight(1f)
            ) {
                items(chests.size) { idx ->
                    val chest = chests[idx]
                    val isTargetChest = chest.isTarget && chest.isOpen

                    Box(
                        modifier = Modifier
                            .height(115.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                when {
                                    isTargetChest -> Color(0xFFFFD54F) // Golden Shine
                                    chest.isOpen -> Color(0xFFE0E0E0) // Opened wrong chest
                                    else -> Color(0xFF8D6E63) // Closed Wooden Chest
                                }
                            )
                            .border(
                                width = if (isTargetChest) 4.dp else 2.dp,
                                color = if (isTargetChest) Color(0xFFF59E0B) else Color(0xFF5D4037),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .clickable(enabled = !chest.isOpen && !isRoundProcessing) {
                                isRoundProcessing = true
                                chests = chests.map { if (it.id == chest.id) it.copy(isOpen = true) else it }

                                if (chest.isTarget) {
                                    // Correct Treasure Chest!
                                    score++
                                    showConfetti = true
                                    showVocabBanner = true
                                    celebrationMessage = encouragements.random()

                                    currentTargetItem?.let { target ->
                                        audioEngine.speakPraise()
                                        audioEngine.speak("${target.character}... ${target.word}! ${celebrationMessage}")
                                    }

                                    coroutineScope.launch {
                                        // Keep chest open & show celebration for 3 seconds
                                        delay(3200)

                                        if (score >= 4) {
                                            repository.addStars(5)
                                            userStars = repository.getStars()
                                            showRewardDialog = true
                                        } else {
                                            setupTreasureMap()
                                        }
                                    }
                                } else {
                                    // Wrong Treasure Chest!
                                    audioEngine.speak("Not this chest! Keep looking!")

                                    coroutineScope.launch {
                                        // Show wrong chest for 1.5 seconds then close
                                        delay(1500)
                                        chests = chests.map { if (it.id == chest.id) it.copy(isOpen = false) else it }
                                        isRoundProcessing = false
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = when {
                                    isTargetChest -> "💎✨⭐"
                                    chest.isOpen -> "🪙"
                                    else -> "🧰"
                                },
                                fontSize = 36.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (chest.isOpen) {
                                Text(
                                    text = chest.content,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (chest.isTarget) Color(0xFFB45309) else Color(0xFF424242)
                                )
                            } else {
                                Text(
                                    text = "Tap Chest",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            KkLionMascot(
                state = if (showRewardDialog || showVocabBanner) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = currentTargetItem?.let { "Find chest '${it.character}'!" } ?: "Find the treasure!",
                onClick = {
                    currentTargetItem?.let {
                        audioEngine.speak("Find treasure chest with letter ${it.character}!")
                    }
                }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Pirate Captain! 🏴‍☠️💎",
            message = "You found all hidden letter treasures!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                score = 0
                setupTreasureMap()
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}
