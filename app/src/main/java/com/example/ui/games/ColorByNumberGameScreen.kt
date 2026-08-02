package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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

data class NumberedRegion(
    val regionId: Int,
    val number: Int,
    val name: String,
    val targetColor: Color,
    val emoji: String,
    var isColored: Boolean = false
)

@Composable
fun ColorByNumberGameScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    // Game 8: Numbers Notebook & Color by Numbers (0 to 20)
    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var pictureIndex by remember { mutableIntStateOf(0) }

    val palette = remember {
        listOf(
            Triple(1, "Red 🔴", Color(0xFFEF4444)),
            Triple(2, "Blue 🔵", Color(0xFF3B82F6)),
            Triple(3, "Yellow 🟡", Color(0xFFEAB308)),
            Triple(4, "Green 🟢", Color(0xFF22C55E)),
            Triple(5, "Purple 🟣", Color(0xFFA855F7))
        )
    }

    var selectedNumber by remember { mutableIntStateOf(1) }

    // Region templates
    var regions by remember(pictureIndex) {
        mutableStateOf(
            listOf(
                NumberedRegion(0, 1, "Crown", Color(0xFFEF4444), "👑"),
                NumberedRegion(1, 2, "Body", Color(0xFF3B82F6), "👕"),
                NumberedRegion(2, 3, "Wings / Stars", Color(0xFFEAB308), "⭐"),
                NumberedRegion(3, 4, "Grass / Shoes", Color(0xFF22C55E), "👟"),
                NumberedRegion(4, 5, "Hat / Magic", Color(0xFFA855F7), "🎩")
            )
        )
    }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    val currentSelectedTriple = palette.find { it.first == selectedNumber } ?: palette[0]

    LaunchedEffect(selectedNumber) {
        audioEngine.speak("Color number $selectedNumber ${currentSelectedTriple.second}!")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF5FF))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Color By Number 🎨",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Instructions Bar
            Text(
                text = "Select Number & Color below, then tap matching region!",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B21A8)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Numbered Color Palette Bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(palette) { _, (num, label, color) ->
                    val isSelected = (selectedNumber == num)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) color else color.copy(alpha = 0.2f),
                        modifier = Modifier
                            .clickable {
                                selectedNumber = num
                                audioEngine.speak("Number $num $label")
                            }
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color(0xFF4C1D95) else color,
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color.White else color),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$num",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) color else Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = label.substringBefore(" "),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFF1E293B)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Art Canvas with 5 Numbered Regions
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .weight(1f),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = androidx.compose.foundation.BorderStroke(3.dp, Color(0xFFC084FC))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "Tap regions marked with #${selectedNumber} 🖌️",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7E22CE)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        regions.take(3).forEach { reg ->
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (reg.isColored) reg.targetColor else Color(0xFFF3E8FF))
                                    .border(2.dp, Color(0xFFA855F7), RoundedCornerShape(20.dp))
                                    .clickable {
                                        if (selectedNumber == reg.number) {
                                            audioEngine.playCorrectSound()
                                            audioEngine.speak("Colored ${reg.name}!")
                                            regions = regions.map {
                                                if (it.regionId == reg.regionId) it.copy(isColored = true) else it
                                            }
                                            repository.addStars(2)
                                            repository.rewardColoring()
                                            userStars = repository.getStars()

                                            if (regions.all { it.isColored }) {
                                                repository.rewardFinishGame()
                                                showRewardDialog = true
                                                showConfetti = true
                                            }
                                        } else {
                                            audioEngine.playWrongSound()
                                            audioEngine.speak("This region needs number ${reg.number}!")
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = reg.emoji, fontSize = 32.sp)
                                    if (!reg.isColored) {
                                        Text(text = "#${reg.number}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF6B21A8))
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        regions.drop(3).forEach { reg ->
                            Box(
                                modifier = Modifier
                                    .size(95.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (reg.isColored) reg.targetColor else Color(0xFFF3E8FF))
                                    .border(2.dp, Color(0xFFA855F7), RoundedCornerShape(20.dp))
                                    .clickable {
                                        if (selectedNumber == reg.number) {
                                            audioEngine.playCorrectSound()
                                            audioEngine.speak("Colored ${reg.name}!")
                                            regions = regions.map {
                                                if (it.regionId == reg.regionId) it.copy(isColored = true) else it
                                            }
                                            repository.addStars(2)
                                            repository.rewardColoring()
                                            userStars = repository.getStars()

                                            if (regions.all { it.isColored }) {
                                                repository.rewardFinishGame()
                                                showRewardDialog = true
                                                showConfetti = true
                                            }
                                        } else {
                                            audioEngine.playWrongSound()
                                            audioEngine.speak("This region needs number ${reg.number}!")
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = reg.emoji, fontSize = 32.sp)
                                    if (!reg.isColored) {
                                        Text(text = "#${reg.number}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF6B21A8))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Color by number!",
                onClick = { audioEngine.speak("Select number $selectedNumber and tap the matching shape!") }
            )
        }

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Artist Masterpiece! 🎨",
            message = "You colored the entire picture by number!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                pictureIndex++
                regions = regions.map { it.copy(isColored = false) }
            },
            onHome = onBackClick
        )

        ConfettiOverlay(isVisible = showConfetti)
    }
}
