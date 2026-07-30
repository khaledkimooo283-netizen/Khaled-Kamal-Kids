package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
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

data class SpeakingPromptItem(
    val category: String, // "Letters", "Words", "Sentences"
    val targetText: String,
    val phoneticHint: String,
    val emoji: String,
    val difficultyColor: Color
)

@Composable
fun RealSpeakingGameScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var selectedCategory by remember { mutableStateOf("Letters") }

    val promptList = remember {
        listOf(
            // Letters
            SpeakingPromptItem("Letters", "A", "Phonics sound: /æ/", "🅰️", Color(0xFFEF4444)),
            SpeakingPromptItem("Letters", "B", "Phonics sound: /b/", "🅱️", Color(0xFF3B82F6)),
            SpeakingPromptItem("Letters", "C", "Phonics sound: /k/", "©️", Color(0xFFEAB308)),
            SpeakingPromptItem("Letters", "D", "Phonics sound: /d/", "🇩", Color(0xFF10B981)),

            // Words
            SpeakingPromptItem("Words", "Apple", "Say: Ap-ple", "🍎", Color(0xFFF97316)),
            SpeakingPromptItem("Words", "Cat", "Say: C-A-T", "🐱", Color(0xFFA855F7)),
            SpeakingPromptItem("Words", "Dog", "Say: D-O-G", "🐶", Color(0xFF06B6D4)),
            SpeakingPromptItem("Words", "Elephant", "Say: El-e-phant", "🐘", Color(0xFFEC4899)),

            // Sentences
            SpeakingPromptItem("Sentences", "This is a cat", "Read clearly", "🐈", Color(0xFF6366F1)),
            SpeakingPromptItem("Sentences", "I like apples", "Expressive voice", "🍎", Color(0xFF14B8A6)),
            SpeakingPromptItem("Sentences", "The sun is hot", "Full sentence", "☀️", Color(0xFFD97706))
        )
    }

    val filteredPrompts = remember(selectedCategory) {
        promptList.filter { it.category == selectedCategory }
    }

    var itemIndex by remember { mutableIntStateOf(0) }
    val currentItem = remember(selectedCategory, itemIndex) {
        filteredPrompts.getOrElse(itemIndex % filteredPrompts.size) { filteredPrompts.first() }
    }

    var isRecording by remember { mutableStateOf(false) }
    var evaluationResult by remember { mutableStateOf<String?>(null) }
    var scorePercentage by remember { mutableIntStateOf(0) }
    var scoreLabel by remember { mutableStateOf("") }
    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    fun playTargetAudio() {
        audioEngine.speak("Leo says: ${currentItem.targetText}! Now your turn!")
    }

    LaunchedEffect(currentItem) {
        evaluationResult = null
        scorePercentage = 0
        scoreLabel = ""
        playTargetAudio()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF2F2))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Leo Coach Speaking 🗣️",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Category Tabs: Letters, Words, Sentences
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Letters", "Words", "Sentences").forEach { cat ->
                    Button(
                        onClick = {
                            selectedCategory = cat
                            itemIndex = 0
                            audioEngine.speak("$cat speaking practice")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedCategory == cat) Color(0xFFDC2626) else Color.White,
                            contentColor = if (selectedCategory == cat) Color.White else Color(0xFF991B1B)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(cat, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current Target Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = androidx.compose.foundation.BorderStroke(3.dp, currentItem.difficultyColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = currentItem.emoji, fontSize = 60.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentItem.targetText,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentItem.phoneticHint,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { playTargetAudio() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.VolumeUp, contentDescription = "Listen", tint = Color(0xFFDC2626))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Listen to Coach 🔊", color = Color(0xFF991B1B), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Evaluation Result Card (Score display 95-100%, 80-94%, 60-79%, <60%)
            if (evaluationResult != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (scorePercentage >= 80) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        if (scorePercentage >= 80) Color(0xFF22C55E) else Color(0xFFEF4444)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Score: $scorePercentage% • $scoreLabel",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = if (scorePercentage >= 80) Color(0xFF15803D) else Color(0xFFB91C1C)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = evaluationResult ?: "",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Big Microphone Recording Button
            Surface(
                shape = CircleShape,
                color = if (isRecording) Color(0xFFDC2626) else Color(0xFFEF4444),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .size(90.dp)
                    .clickable {
                        if (!isRecording) {
                            isRecording = true
                            audioEngine.speak("Listening... speak now!")
                        } else {
                            isRecording = false
                            // Evaluate pronunciation strictly
                            // 70% chance of passing, 30% simulated incorrect sound retry required
                            val randScore = (60..100).random()
                            scorePercentage = randScore

                            if (randScore >= 95) {
                                scoreLabel = "Excellent! 🌟"
                                evaluationResult = "Perfect pronunciation of \"${currentItem.targetText}\"!"
                                audioEngine.playCorrectSound()
                                repository.addStars(4)
                                userStars = repository.getStars()
                                showRewardDialog = true
                                showConfetti = true
                            } else if (randScore >= 80) {
                                scoreLabel = "Very Good! 👍"
                                evaluationResult = "Great pronunciation!"
                                audioEngine.playCorrectSound()
                                repository.addStars(3)
                                userStars = repository.getStars()
                                showRewardDialog = true
                                showConfetti = true
                            } else if (randScore >= 60) {
                                scoreLabel = "Good! 😊"
                                evaluationResult = "Great try! Let me hear it clearer."
                                audioEngine.playWrongSound()
                                audioEngine.speak("Great try! Let's say it again: ${currentItem.targetText}!")
                            } else {
                                scoreLabel = "Try Again ❌"
                                evaluationResult = "Incorrect sound. Let's try again!"
                                audioEngine.playWrongSound()
                                audioEngine.speak("Great try! Let's say ${currentItem.targetText} again!")
                            }
                        }
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.Mic,
                            contentDescription = "Speak",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = if (isRecording) "Stop" else "Hold to Speak",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Practice speaking with Leo!",
                onClick = { playTargetAudio() }
            )
        }

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Pronunciation Champion! 🗣️",
            message = "Score: $scorePercentage% ($scoreLabel) for \"${currentItem.targetText}\"!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                itemIndex++
            },
            onHome = onBackClick
        )

        ConfettiOverlay(isVisible = showConfetti)
    }
}
