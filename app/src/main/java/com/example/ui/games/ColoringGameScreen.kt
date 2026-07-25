package com.example.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

data class ColorBucket(
    val id: String,
    val name: String,
    val color: Color
)

@Composable
fun ColoringGameScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var selectedColor by remember { mutableStateOf(Color(0xFFFF5252)) }

    var section1Color by remember { mutableStateOf(Color.White) }
    var section2Color by remember { mutableStateOf(Color.White) }
    var section3Color by remember { mutableStateOf(Color.White) }
    var section4Color by remember { mutableStateOf(Color.White) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    val buckets = remember {
        listOf(
            ColorBucket("A", "Red", Color(0xFFFF5252)),
            ColorBucket("B", "Blue", Color(0xFF448AFF)),
            ColorBucket("C", "Yellow", Color(0xFFFFD54F)),
            ColorBucket("D", "Green", Color(0xFF66BB6A))
        )
    }

    LaunchedEffect(Unit) {
        audioEngine.speak("Color by Letter! Pick a paint bucket and tap canvas sections! 🎨")
    }

    fun checkCompletion() {
        if (section1Color != Color.White && section2Color != Color.White &&
            section3Color != Color.White && section4Color != Color.White
        ) {
            showConfetti = true
            repository.addStars(5)
            userStars = repository.getStars()
            audioEngine.speakPraise()
            showRewardDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8E1))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Coloring Book 🎨",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Palette Paint Buckets
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                buckets.forEach { bucket ->
                    val isSelected = selectedColor == bucket.color
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(bucket.color)
                            .border(
                                width = if (isSelected) 4.dp else 2.dp,
                                color = if (isSelected) Color.Black else Color.White,
                                shape = CircleShape
                            )
                            .clickable {
                                selectedColor = bucket.color
                                audioEngine.speak("Selected ${bucket.name} paint!")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = bucket.id, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Interactive Artwork Canvas
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White)
                    .border(4.dp, Color(0xFFFFB74D), RoundedCornerShape(32.dp))
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.weight(1f)) {
                        // Section A
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(section1Color)
                                .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
                                .clickable {
                                    section1Color = selectedColor
                                    audioEngine.speak("Colored section A!")
                                    checkCompletion()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "A", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                        }

                        // Section B
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(section2Color)
                                .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
                                .clickable {
                                    section2Color = selectedColor
                                    audioEngine.speak("Colored section B!")
                                    checkCompletion()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "B", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    Row(modifier = Modifier.weight(1f)) {
                        // Section C
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(section3Color)
                                .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
                                .clickable {
                                    section3Color = selectedColor
                                    audioEngine.speak("Colored section C!")
                                    checkCompletion()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "C", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                        }

                        // Section D
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(section4Color)
                                .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
                                .clickable {
                                    section4Color = selectedColor
                                    audioEngine.speak("Colored section D!")
                                    checkCompletion()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "D", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Color all sections!",
                onClick = { audioEngine.speak("Pick a paint bucket and tap canvas sections!") }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Master Artist!",
            message = "You created a vibrant masterpiece!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                section1Color = Color.White
                section2Color = Color.White
                section3Color = Color.White
                section4Color = Color.White
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}
