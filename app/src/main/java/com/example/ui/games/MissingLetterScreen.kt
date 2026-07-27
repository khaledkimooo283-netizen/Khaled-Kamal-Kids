package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.data.HandwritingData
import com.example.data.KkDataRepository
import com.example.ui.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MissingLetterPuzzle(
    val fullWord: String,
    val missingLetter: String,
    val wordWithBlank: String,
    val emoji: String,
    val choices: List<String>
)

@Composable
fun MissingLetterScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var userStars by remember { mutableIntStateOf(repository.getStars()) }

    var currentPuzzle by remember { mutableStateOf<MissingLetterPuzzle?>(null) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var score by remember { mutableIntStateOf(0) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    val vocabularyList = remember { HandwritingData.uppercaseLetters }

    fun setupNewPuzzle() {
        selectedAnswer = null
        val target = vocabularyList.random()
        val wordUpper = target.word.uppercase()
        val charTarget = target.character.uppercase()

        // Create missing blank for first character or vowel
        val blankIndex = 0
        val missingCharStr = charTarget
        val wordBlank = "_" + wordUpper.substring(1)

        val distractors = vocabularyList
            .filter { it.character != target.character }
            .shuffled()
            .take(3)
            .map { it.character }

        val allChoices = (distractors + missingCharStr).shuffled()

        currentPuzzle = MissingLetterPuzzle(
            fullWord = wordUpper,
            missingLetter = missingCharStr,
            wordWithBlank = wordBlank,
            emoji = target.emoji,
            choices = allChoices
        )

        audioEngine.speak("Find the missing letter for ${target.word}!")
    }

    LaunchedEffect(Unit) {
        setupNewPuzzle()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF3C7)) // Warm Yellow Sunshine
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Find Missing Letter 🔍",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            currentPuzzle?.let { puzzle ->
                // Image Hero Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(top = 12.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = puzzle.emoji, fontSize = 72.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Display Word with Blank Slot
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            puzzle.wordWithBlank.forEach { ch ->
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (ch == '_') Color(0xFFFDE047) else Color(0xFFF1F5F9)
                                        )
                                        .border(
                                            width = if (ch == '_') 2.5.dp else 1.dp,
                                            color = if (ch == '_') Color(0xFFCA8A04) else Color(0xFF94A3B8),
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (ch == '_' && selectedAnswer != null) selectedAnswer!! else ch.toString(),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (ch == '_') Color(0xFFB45309) else Color(0xFF1E293B)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Tap the correct missing letter:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF92400E)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Choice Buttons
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    items(puzzle.choices.size) { idx ->
                        val letterChoice = puzzle.choices[idx]
                        val isSelected = selectedAnswer == letterChoice
                        val isCorrect = letterChoice == puzzle.missingLetter

                        Box(
                            modifier = Modifier
                                .height(75.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    when {
                                        isSelected && isCorrect -> Color(0xFF86EFAC)
                                        isSelected && !isCorrect -> Color(0xFFFCA5A5)
                                        else -> Color.White
                                    }
                                )
                                .border(
                                    width = 2.dp,
                                    color = Color(0xFFD97706),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    selectedAnswer = letterChoice

                                    if (isCorrect) {
                                        score++
                                        audioEngine.speakPraise()
                                        audioEngine.speak("Excellent! ${puzzle.missingLetter}... ${puzzle.fullWord}!")

                                        coroutineScope.launch {
                                            delay(1400)
                                            if (score >= 4) {
                                                repository.addStars(5)
                                                userStars = repository.getStars()
                                                showConfetti = true
                                                showRewardDialog = true
                                            } else {
                                                setupNewPuzzle()
                                            }
                                        }
                                    } else {
                                        audioEngine.speak("Not $letterChoice, try another letter!")
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letterChoice,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF78350F)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = currentPuzzle?.let { "Which letter starts ${it.fullWord}?" } ?: "Find the missing letter!",
                onClick = {
                    currentPuzzle?.let {
                        audioEngine.speak("Select the missing letter for ${it.fullWord}!")
                    }
                }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Spelling Champ! 🔍⭐",
            message = "You found all the missing letters!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                score = 0
                setupNewPuzzle()
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}
