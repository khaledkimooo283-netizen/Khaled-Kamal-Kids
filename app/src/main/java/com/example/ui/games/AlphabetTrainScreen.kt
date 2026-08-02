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
    var selectedMathTab by remember { mutableStateOf("train") } // "train", "sequence_order", "ice_cream"

    Column(modifier = Modifier.fillMaxSize()) {
        // Hub Top Tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF713F12))
                .padding(vertical = 6.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                Triple("train", "Watch then Copy Train", "🚂"),
                Triple("sequence_order", "Sequence Order", "🔢"),
                Triple("ice_cream", "Ice Cream Shop", "🍦")
            ).forEach { (modeId, modeTitle, emoji) ->
                val isSelected = selectedMathTab == modeId
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) Color(0xFFD97706) else Color(0xFFA16207))
                        .clickable {
                            selectedMathTab = modeId
                            audioEngine.speak("$modeTitle!")
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$emoji $modeTitle",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (selectedMathTab) {
                "train" -> AlphabetTrainContent(repository, audioEngine, onBackClick)
                "sequence_order" -> SequenceOrderScreen(repository, audioEngine, onBackClick)
                "ice_cream" -> IceCreamShopScreen(repository, audioEngine, onBackClick)
            }
        }
    }
}

@Composable
fun AlphabetTrainContent(
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

            audioEngine.speak("Choo Choo! What missing number belongs on the train? 🔢")
        }
    }

    LaunchedEffect(mode) {
        generateNewTrain()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF3C7))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Math & Sequence Train 🚂",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Mode Toggle Bar (Letters vs Numbers Train)
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    Pair("alphabet", "🔤 Alphabet Train"),
                    Pair("numbers", "🔢 Numbers Train")
                ).forEach { (mKey, mLabel) ->
                    val isSelected = mode == mKey
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color(0xFFD97706) else Color.White)
                            .border(2.dp, Color(0xFFB45309), RoundedCornerShape(16.dp))
                            .clickable { mode = mKey }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mLabel,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color(0xFF78350F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Train Graphic Row
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF92400E)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🚂 KK KIDS EXPRESS 🚂",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFDE68A)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        trainCars.forEach { car ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(70.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (car == "?") Color(0xFFFBBF24) else Color(0xFFF59E0B))
                                    .border(3.dp, Color(0xFF78350F), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = car,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (car == "?") Color(0xFF92400E) else Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tap the missing car piece below:",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF78350F)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Candidate Option Buttons
            Row(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                candidateOptions.forEach { option ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(75.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .border(3.dp, Color(0xFFD97706), RoundedCornerShape(20.dp))
                            .clickable {
                                if (option == missingItem) {
                                    audioEngine.speakPraise()
                                    audioEngine.speak("Choo Choo! $option is correct!")
                                    repository.addStars(2)
                                    userStars = repository.getStars()
                                    showConfetti = true
                                    showRewardDialog = true
                                } else {
                                    audioEngine.speakTryAgain()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFB45309)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Help Lion complete the train sequence!",
                onClick = {
                    audioEngine.speak("Choo Choo! Find the missing car for Lion's train!")
                }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Train Conductor! 🚂⭐",
            message = "You filled in the missing sequence perfectly!",
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

