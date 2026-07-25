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

@Composable
fun AlphabetTrainScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var mode by remember { mutableStateOf("alphabet") }
    var userStars by remember { mutableIntStateOf(repository.getStars()) }

    var missingItem by remember { mutableStateOf("C") }
    var trainCars by remember { mutableStateOf(listOf("A", "B", "?", "D")) }
    var candidateOptions by remember { mutableStateOf(listOf("C", "E", "X")) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    fun generateNewTrain() {
        if (mode == "alphabet") {
            val startIndex = (0..22).random()
            val seq = repository.alphabetList.subList(startIndex, startIndex + 4).map { it.letter.toString() }
            val missingIdx = (1..2).random()
            missingItem = seq[missingIdx]

            trainCars = seq.mapIndexed { idx, item -> if (idx == missingIdx) "?" else item }

            val distractors = repository.alphabetList.filter { !seq.contains(it.letter.toString()) }.map { it.letter.toString() }.shuffled().take(2)
            candidateOptions = (distractors + missingItem).shuffled()

            audioEngine.speak("Choo Choo! What missing letter comes next in the train? 🚂")
        } else {
            val startIndex = (0..6).random()
            val seq = repository.numberList.subList(startIndex, startIndex + 4).map { it.number.toString() }
            val missingIdx = (1..2).random()
            missingItem = seq[missingIdx]

            trainCars = seq.mapIndexed { idx, item -> if (idx == missingIdx) "?" else item }

            val distractors = repository.numberList.filter { !seq.contains(it.number.toString()) }.map { it.number.toString() }.shuffled().take(2)
            candidateOptions = (distractors + missingItem).shuffled()

            audioEngine.speak("Choo Choo! What missing number completes the train sequence? 🚂")
        }
    }

    LaunchedEffect(mode) {
        generateNewTrain()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEBE9))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Alphabet Train 🚂",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Mode Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { mode = "alphabet" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (mode == "alphabet") Color(0xFF795548) else Color(0xFFD7CCC8)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("ABC Train", color = if (mode == "alphabet") Color.White else Color(0xFF3E2723), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = { mode = "numbers" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (mode == "numbers") Color(0xFF795548) else Color(0xFFD7CCC8)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("123 Train", color = if (mode == "numbers") Color.White else Color(0xFF3E2723), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Train Cars Display
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Locomotive Engine
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFD32F2F)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🚂", fontSize = 32.sp)
                }

                // Train Cars
                trainCars.forEach { car ->
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (car == "?") Color(0xFFFFF9C4) else Color(0xFF4CAF50))
                            .border(2.dp, if (car == "?") Color(0xFFFBC02D) else Color(0xFF2E7D32), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = car,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (car == "?") Color(0xFFE65100) else Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Tap the correct block to fill the missing train car!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D4037),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Candidate Options
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                candidateOptions.forEach { opt ->
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFF0288D1))
                            .clickable {
                                if (opt == missingItem) {
                                    audioEngine.speakPraise()
                                    audioEngine.speak("Choo Choo! $opt is correct!")
                                    showConfetti = true
                                    repository.addStars(4)
                                    userStars = repository.getStars()
                                    showRewardDialog = true
                                } else {
                                    audioEngine.speakTryAgain()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = opt,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Mascot Guide
            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Find missing block '$missingItem'!",
                onClick = { audioEngine.speak("Which block belongs in the missing train car?") }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Choo Choo Train Conductor!",
            message = "You completed the train sequence!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                generateNewTrain()
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}
