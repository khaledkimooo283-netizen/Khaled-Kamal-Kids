package com.example.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var targetWord by remember { mutableStateOf("A") }
    var score by remember { mutableIntStateOf(0) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    var chests by remember { mutableStateOf(listOf<TreasureChestItem>()) }

    fun setupTreasureMap() {
        val target = repository.alphabetList.random()
        targetWord = target.letter.toString()

        val distractors = repository.alphabetList.filter { it.letter.toString() != targetWord }.shuffled().take(5).map { it.letter.toString() }
        val allContent = (distractors + targetWord).shuffled()

        chests = allContent.mapIndexed { index, item ->
            TreasureChestItem(
                id = index,
                content = item,
                isTarget = item == targetWord,
                isOpen = false
            )
        }

        audioEngine.speak("Treasure Hunt! Find the chest holding the letter $targetWord! 🏴‍☠️")
    }

    LaunchedEffect(Unit) {
        setupTreasureMap()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF3E0)) // Beach Sand Warm Tone
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

            // Mission Clue
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFB8C00))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Find Treasure Chest: '$targetWord' 💎",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    Box(
                        modifier = Modifier
                            .height(120.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (chest.isOpen) Color(0xFFFFE082) else Color(0xFF8D6E63))
                            .clickable {
                                if (!chest.isOpen) {
                                    chests = chests.map { if (it.id == chest.id) it.copy(isOpen = true) else it }
                                    if (chest.isTarget) {
                                        score++
                                        audioEngine.speakPraise()
                                        audioEngine.speak("You found $targetWord! Golden Treasure!")

                                        if (score >= 3) {
                                            showConfetti = true
                                            repository.addStars(4)
                                            userStars = repository.getStars()
                                            showRewardDialog = true
                                        } else {
                                            setupTreasureMap()
                                        }
                                    } else {
                                        audioEngine.speak("Not this chest! Keep hunting!")
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (chest.isOpen) (if (chest.isTarget) "💎" else "🪙") else "🧰",
                                fontSize = 42.sp
                            )
                            if (chest.isOpen) {
                                Text(
                                    text = chest.content,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF3E2723)
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

            Spacer(modifier = Modifier.height(12.dp))

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Find chest '$targetWord'!",
                onClick = { audioEngine.speak("Tap a chest to find letter $targetWord!") }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Pirate Captain!",
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
