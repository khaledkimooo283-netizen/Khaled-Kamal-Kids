package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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

data class SentencePuzzle(
    val fullSentence: String,
    val words: List<String>,
    val emoji: String,
    val difficulty: String // "Easy", "Medium", "Hard"
)

@Composable
fun BuildSentenceGameScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var selectedDifficulty by remember { mutableStateOf("Easy") }

    val allSentences = remember {
        listOf(
            // Easy (3 words)
            SentencePuzzle("I see cat", listOf("I", "see", "cat"), "🐱", "Easy"),
            SentencePuzzle("Sun is hot", listOf("Sun", "is", "hot"), "☀️", "Easy"),
            SentencePuzzle("Birds can fly", listOf("Birds", "can", "fly"), "🐦", "Easy"),
            SentencePuzzle("I love apples", listOf("I", "love", "apples"), "🍎", "Easy"),

            // Medium (4 words)
            SentencePuzzle("The red car goes", listOf("The", "red", "car", "goes"), "🚗", "Medium"),
            SentencePuzzle("Dinos play all day", listOf("Dinos", "play", "all", "day"), "🦖", "Medium"),
            SentencePuzzle("She likes big ice cream", listOf("She", "likes", "big", "ice cream"), "🍦", "Medium"),

            // Hard (5 words)
            SentencePuzzle("The cute puppy jumps happy", listOf("The", "cute", "puppy", "jumps", "happy"), "🐶", "Hard"),
            SentencePuzzle("We can build big blocks", listOf("We", "can", "build", "big", "blocks"), "🧱", "Hard")
        )
    }

    val currentFilteredList = remember(selectedDifficulty) {
        allSentences.filter { it.difficulty == selectedDifficulty }
    }

    var sentenceIndex by remember { mutableIntStateOf(0) }
    val currentPuzzle = remember(selectedDifficulty, sentenceIndex) {
        currentFilteredList.getOrElse(sentenceIndex % currentFilteredList.size) { currentFilteredList.first() }
    }

    // Shuffled available words for current puzzle
    var availableWords by remember(currentPuzzle) {
        mutableStateOf(currentPuzzle.words.shuffled())
    }
    var constructedWords by remember(currentPuzzle) {
        mutableStateOf(listOf<String>())
    }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(currentPuzzle) {
        audioEngine.speak("Arrange words to make: ${currentPuzzle.fullSentence}! ${currentPuzzle.emoji}")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFF6FF))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Build the Sentence 🧩",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Difficulty Tabs (Easy, Medium, Hard)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Easy", "Medium", "Hard").forEach { diff ->
                    Button(
                        onClick = {
                            selectedDifficulty = diff
                            sentenceIndex = 0
                            audioEngine.speak("$diff sentence level")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedDifficulty == diff) Color(0xFF2563EB) else Color.White,
                            contentColor = if (selectedDifficulty == diff) Color.White else Color(0xFF1E3A8A)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(diff, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Emoji Card Target
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .border(3.dp, Color(0xFF3B82F6), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = currentPuzzle.emoji, fontSize = 54.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Target Sentence Drop Slots
            Text(
                text = "Sentence Slots:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E3A8A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(2.dp, Color(0xFF93C5FD), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (constructedWords.isEmpty()) {
                    Text(
                        text = "Tap words below in order ⬇️",
                        fontSize = 14.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    constructedWords.forEachIndexed { idx, word ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFDBEAFE),
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clickable {
                                    // Remove word back to available pool
                                    constructedWords = constructedWords.toMutableList().apply { removeAt(idx) }
                                    availableWords = availableWords + word
                                }
                        ) {
                            Text(
                                text = word,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E40AF),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Shuffled Available Word Blocks
            Text(
                text = "Tap to place words:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E3A8A)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(0.92f),
                horizontalArrangement = Arrangement.Center
            ) {
                availableWords.forEachIndexed { idx, word ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF3B82F6),
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable {
                                audioEngine.speak(word)
                                val newConstructed = constructedWords + word
                                val newAvailable = availableWords.toMutableList().apply { removeAt(idx) }
                                constructedWords = newConstructed
                                availableWords = newAvailable

                                // Check if sentence complete
                                if (newAvailable.isEmpty()) {
                                    val attemptStr = newConstructed.joinToString(" ")
                                    if (attemptStr.equals(currentPuzzle.fullSentence, ignoreCase = true)) {
                                        audioEngine.playCorrectSound()
                                        audioEngine.speak("${currentPuzzle.fullSentence}! Great job!")
                                        repository.addStars(4)
                                        userStars = repository.getStars()
                                        showRewardDialog = true
                                        showConfetti = true
                                    } else {
                                        audioEngine.playWrongSound()
                                        audioEngine.speak("Not quite! Tap words in sentence slots to reset.")
                                    }
                                }
                            }
                    ) {
                        Text(
                            text = word,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Build cute sentences!",
                onClick = { audioEngine.speak("Arrange the words to make ${currentPuzzle.fullSentence}!") }
            )
        }

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Sentence Builder! 📜",
            message = "You built: \"${currentPuzzle.fullSentence}\"!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                sentenceIndex++
            },
            onHome = onBackClick
        )

        ConfettiOverlay(isVisible = showConfetti)
    }
}
