package com.example.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
fun TypingGameScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var keyMode by remember { mutableStateOf("UPPERCASE") } // "UPPERCASE", "LOWERCASE", "NUMBERS"
    var currentItemIndex by remember { mutableIntStateOf(0) }
    var typedInput by remember { mutableStateOf("") }
    var userStars by remember { mutableIntStateOf(repository.getStars()) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    val currentWordItem = remember(currentItemIndex, keyMode) {
        if (keyMode == "NUMBERS") {
            val numItem = repository.numberList.getOrNull(currentItemIndex % repository.numberList.size) ?: repository.numberList.first()
            Pair(numItem.word.uppercase(), numItem.emoji)
        } else {
            val letItem = repository.alphabetList.getOrNull(currentItemIndex % repository.alphabetList.size) ?: repository.alphabetList.first()
            Pair(letItem.word.uppercase(), letItem.emoji)
        }
    }
    val targetWord = currentWordItem.first
    val currentEmoji = currentWordItem.second

    LaunchedEffect(currentItemIndex, keyMode) {
        typedInput = ""
        audioEngine.speak("Spell ${currentWordItem.first}! $currentEmoji")
    }

    val keysList = remember(keyMode) {
        when (keyMode) {
            "UPPERCASE" -> ('A'..'Z').map { it.toString() }
            "LOWERCASE" -> ('a'..'z').map { it.toString() }
            else -> (0..9).map { it.toString() }
        }
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
                title = "Typing & Spelling ⌨️",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Keyboard Mode Selector (Uppercase, Lowercase, Numbers)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("UPPERCASE" to "ABC", "LOWERCASE" to "abc", "NUMBERS" to "123").forEach { (mode, label) ->
                    Button(
                        onClick = { keyMode = mode },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (keyMode == mode) Color(0xFF8E24AA) else Color(0xFFE1BEE7)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(label, color = if (keyMode == mode) Color.White else Color(0xFF4A148C), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Target Emoji & Prompt Word
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(3.dp, Color(0xFFBA68C8), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = currentEmoji, fontSize = 54.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Target Slots
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                targetWord.forEachIndexed { idx, char ->
                    val userChar = typedInput.getOrNull(idx)?.uppercase() ?: ""
                    val isCorrect = userChar == char.toString()

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .padding(3.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when {
                                    userChar.isNotEmpty() && isCorrect -> Color(0xFFC8E6C9)
                                    userChar.isNotEmpty() -> Color(0xFFFFCDD2)
                                    else -> Color.White
                                }
                            )
                            .border(2.dp, Color(0xFF8E24AA), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userChar,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF4A148C)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Child Friendly Keyboard Grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(8.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(keysList) { keyChar ->
                        val expectedChar = targetWord.getOrNull(typedInput.length)?.toString() ?: ""

                        Box(
                            modifier = Modifier
                                .height(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFAB47BC))
                                .clickable {
                                    audioEngine.speak(keyChar)
                                    // Check if correct key for current slot
                                    if (expectedChar.isNotEmpty() && keyChar.equals(expectedChar, ignoreCase = true)) {
                                        typedInput += expectedChar
                                        if (typedInput.equals(targetWord, ignoreCase = true)) {
                                            // Word completed!
                                            showConfetti = true
                                            repository.addStars(3)
                                            userStars = repository.getStars()
                                            audioEngine.speakPraise()
                                            audioEngine.speak("$targetWord!")
                                            showRewardDialog = true
                                        }
                                    } else {
                                        audioEngine.speakTryAgain()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = keyChar,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mascot Guide
            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Type '$targetWord'!",
                onClick = { audioEngine.speak("Tap the correct key to spell $targetWord!") }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Spelling Champion!",
            message = "You spelled $targetWord $currentEmoji!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                currentItemIndex = currentItemIndex + 1
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}
