package com.example.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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

data class MemoryCard(
    val id: Int,
    val pairId: String,
    val displayContent: String,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false
)

@Composable
fun MemoryCardsScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var cards by remember { mutableStateOf(listOf<MemoryCard>()) }
    var firstFlippedIndex by remember { mutableStateOf<Int?>(null) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    fun initCards() {
        val selectedPairs = repository.matchPairsList.shuffled().take(4)
        val cardList = mutableListOf<MemoryCard>()
        var idCounter = 0

        selectedPairs.forEach { pair ->
            cardList.add(MemoryCard(id = idCounter++, pairId = pair.id, displayContent = "${pair.promptText}\n${pair.promptEmoji}"))
            cardList.add(MemoryCard(id = idCounter++, pairId = pair.id, displayContent = "${pair.matchText}\n${pair.matchEmoji}"))
        }

        cards = cardList.shuffled()
        firstFlippedIndex = null
        audioEngine.speak("Flip cards to find matching pairs! 🧠")
    }

    LaunchedEffect(Unit) {
        initCards()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDE7F6))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Memory Cards 🧠",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4x2 Grid of Cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp)
            ) {
                items(cards.size) { index ->
                    val card = cards[index]

                    Card(
                        modifier = Modifier
                            .height(100.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable(enabled = !card.isFlipped && !card.isMatched) {
                                // Flip card
                                cards = cards.mapIndexed { idx, c ->
                                    if (idx == index) c.copy(isFlipped = true) else c
                                }

                                if (firstFlippedIndex == null) {
                                    firstFlippedIndex = index
                                } else {
                                    val firstIdx = firstFlippedIndex!!
                                    val firstCard = cards[firstIdx]

                                    if (firstCard.pairId == card.pairId) {
                                        // Match!
                                        cards = cards.map {
                                            if (it.pairId == card.pairId) it.copy(isMatched = true) else it
                                        }
                                        firstFlippedIndex = null
                                        audioEngine.speakPraise()

                                        if (cards.all { it.isMatched }) {
                                            showConfetti = true
                                            repository.addStars(5)
                                            userStars = repository.getStars()
                                            showRewardDialog = true
                                        }
                                    } else {
                                        // Not match - flip back
                                        audioEngine.speakTryAgain()
                                        cards = cards.mapIndexed { idx, c ->
                                            if (idx == firstIdx || idx == index) c.copy(isFlipped = false) else c
                                        }
                                        firstFlippedIndex = null
                                    }
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                card.isMatched -> Color(0xFFC8E6C9)
                                card.isFlipped -> Color.White
                                else -> Color(0xFF7E57C2)
                            }
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (card.isFlipped || card.isMatched) {
                                Text(
                                    text = card.displayContent,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF311B92)
                                )
                            } else {
                                Text(text = "❓", fontSize = 32.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mascot Guide
            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Find all matching cards!",
                onClick = { audioEngine.speak("Tap two cards to reveal their pictures and match them!") }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Memory Genius!",
            message = "You matched all cards perfectly!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                initCards()
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}
