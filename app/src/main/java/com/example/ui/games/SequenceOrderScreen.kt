package com.example.ui.games

import androidx.compose.animation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.ui.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SequencePuzzle(
    val items: List<String>, // e.g. ["A", "B", "_", "D"]
    val targetAnswer: String, // e.g. "C"
    val choices: List<String>,
    val isNumberMode: Boolean
)

@Composable
fun SequenceOrderScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var userStars by remember { mutableIntStateOf(repository.getStars()) }

    var isNumberSequenceMode by remember { mutableStateOf(false) } // False: Alphabet, True: Numbers
    var currentPuzzle by remember { mutableStateOf<SequencePuzzle?>(null) }
    var selectedChoice by remember { mutableStateOf<String?>(null) }
    var score by remember { mutableIntStateOf(0) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    val alphabet = remember { ('A'..'Z').map { it.toString() } }
    val numbers = remember { (1..20).map { it.toString() } }

    fun setupSequencePuzzle() {
        selectedChoice = null

        val source = if (isNumberSequenceMode) numbers else alphabet
        val startIndex = (0..(source.size - 4)).random()
        val seqFour = source.subList(startIndex, startIndex + 4)

        val missingIndex = (1..2).random()
        val targetChar = seqFour[missingIndex]

        val puzzleDisplay = seqFour.mapIndexed { idx, item ->
            if (idx == missingIndex) "_" else item
        }

        val distractors = source
            .filter { it != targetChar }
            .shuffled()
            .take(3)

        val choices = (distractors + targetChar).shuffled()

        currentPuzzle = SequencePuzzle(
            items = puzzleDisplay,
            targetAnswer = targetChar,
            choices = choices,
            isNumberMode = isNumberSequenceMode
        )

        audioEngine.speak(if (isNumberSequenceMode) "What number comes next in sequence?" else "What letter comes next in ABC order?")
    }

    LaunchedEffect(isNumberSequenceMode) {
        setupSequencePuzzle()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0FDF4)) // Soft Mint Green
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Sequence Order 🔢🔤",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Sequence Mode Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { isNumberSequenceMode = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isNumberSequenceMode) Color(0xFF16A34A) else Color(0xFFDCFCE7)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🔤 ABC Order", fontWeight = FontWeight.Bold, color = if (!isNumberSequenceMode) Color.White else Color(0xFF14532D))
                }

                Button(
                    onClick = { isNumberSequenceMode = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isNumberSequenceMode) Color(0xFF2563EB) else Color(0xFFDBEAFE)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🔢 123 Order", fontWeight = FontWeight.Bold, color = if (isNumberSequenceMode) Color.White else Color(0xFF1E3A8A))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            currentPuzzle?.let { puzzle ->
                // Sequence Display Trail
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    puzzle.items.forEach { item ->
                        val isBlank = item == "_"
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isBlank) Color(0xFFFEF08A) else Color.White)
                                .border(
                                    width = if (isBlank) 3.dp else 1.5.dp,
                                    color = if (isBlank) Color(0xFFCA8A04) else Color(0xFF86EFAC),
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isBlank && selectedChoice != null) selectedChoice!! else item,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isBlank) Color(0xFF854D0E) else Color(0xFF14532D),
                                maxLines = 1,
                                softWrap = false,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Tap the missing item:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF166534)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Choice Options
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    puzzle.choices.forEach { choice ->
                        val isSelected = selectedChoice == choice
                        val isCorrect = choice == puzzle.targetAnswer

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(75.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    when {
                                        isSelected && isCorrect -> Color(0xFF86EFAC)
                                        isSelected && !isCorrect -> Color(0xFFFCA5A5)
                                        else -> Color.White
                                    }
                                )
                                .border(2.dp, Color(0xFF16A34A), RoundedCornerShape(20.dp))
                                .clickable {
                                    selectedChoice = choice

                                    if (isCorrect) {
                                        score++
                                        audioEngine.speakPraise()
                                        audioEngine.speak("Correct! $choice comes next!")

                                        coroutineScope.launch {
                                            delay(1400)
                                            if (score >= 4) {
                                                repository.addStars(5)
                                                userStars = repository.getStars()
                                                showConfetti = true
                                                showRewardDialog = true
                                            } else {
                                                setupSequencePuzzle()
                                            }
                                        }
                                    } else {
                                        audioEngine.speak("Try again! What comes between in order?")
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = choice,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF14532D),
                                maxLines = 1,
                                softWrap = false,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = currentPuzzle?.let { "Find missing item in order!" } ?: "Complete the sequence!",
                onClick = {
                    audioEngine.speak("Complete the ABC or 123 sequence in order!")
                }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Sequence Master! 🔢⭐",
            message = "You ordered all numbers and letters correctly!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                score = 0
                setupSequencePuzzle()
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}
