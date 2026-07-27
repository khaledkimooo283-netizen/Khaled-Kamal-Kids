package com.example.ui.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Timer
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
import com.example.ui.components.KkHeader

@Composable
fun ParentProgressScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var isUnlocked by remember { mutableStateOf(false) }
    var parentAnswerInput by remember { mutableStateOf("") }
    val mathQuestion = remember { "What is 4 + 3?" }
    val correctAnswer = "7"
    var isError by remember { mutableStateOf(false) }

    val totalStars = remember { repository.getStars() }
    val learnedWords = remember { repository.getLearnedWords().toList() }
    val tracingAcc = remember { repository.getTracingAccuracy() }
    val listeningAcc = remember { repository.getListeningAccuracy() }
    val typingAcc = remember { repository.getTypingAccuracy() }
    val totalTimeMins = remember { repository.getLearningTimeMinutes() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Parent Dashboard 📊",
                starsCount = totalStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            if (!isUnlocked) {
                // Parent Gate Protection Lock
                Spacer(modifier = Modifier.height(40.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Parent Lock",
                                tint = Color(0xFF475569),
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Grown-ups Only 👨‍👩‍👧",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Please solve this quick problem to access learning analytics:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = mathQuestion,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF2563EB)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = parentAnswerInput,
                            onValueChange = {
                                parentAnswerInput = it
                                isError = false
                            },
                            label = { Text("Answer") },
                            isError = isError,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(0.6f)
                        )

                        if (isError) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Incorrect answer, try again!",
                                fontSize = 12.sp,
                                color = Color(0xFFDC2626)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (parentAnswerInput.trim() == correctAnswer) {
                                    isUnlocked = true
                                } else {
                                    isError = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text("Enter Dashboard", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            } else {
                // Parent Dashboard Analytics View
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Overview Banner
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Metric Card 1: Letters Mastered
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MenuBook,
                                    contentDescription = "Letters",
                                    tint = Color(0xFF2563EB)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${repository.getAdventureUnlockedWorld()} / 26",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1E3A8A)
                                )
                                Text(
                                    text = "Worlds Mastered",
                                    fontSize = 11.sp,
                                    color = Color(0xFF3B82F6),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Metric Card 2: Words Learned
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Words",
                                    tint = Color(0xFF16A34A)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${learnedWords.size} words",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF14532D)
                                )
                                Text(
                                    text = "Vocabulary Learned",
                                    fontSize = 11.sp,
                                    color = Color(0xFF22C55E),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Metric Card 3: Learning Time
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Timer,
                                    contentDescription = "Time",
                                    tint = Color(0xFFD97706)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$totalTimeMins mins",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF78350F)
                                )
                                Text(
                                    text = "Learning Time",
                                    fontSize = 11.sp,
                                    color = Color(0xFFF59E0B),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Accuracy Breakdown Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.BarChart,
                                    contentDescription = "Chart",
                                    tint = Color(0xFF4F46E5)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Educational Accuracy Breakdown",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1E293B)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            AccuracyProgressBar(label = "Tracing & Handwriting", accuracy = tracingAcc, color = Color(0xFF10B981))
                            Spacer(modifier = Modifier.height(8.dp))
                            AccuracyProgressBar(label = "Listening & Pronunciation", accuracy = listeningAcc, color = Color(0xFF3B82F6))
                            Spacer(modifier = Modifier.height(8.dp))
                            AccuracyProgressBar(label = "Spelling & Typing", accuracy = typingAcc, color = Color(0xFF8B5CF6))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Learned English Words (Tap to Hear Pronunciation):",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF334155),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Vocabulary Review Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(learnedWords.size) { index ->
                            val word = learnedWords[index]
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                                    .border(1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(16.dp))
                                    .clickable { audioEngine.speak(word) }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.VolumeUp,
                                        contentDescription = "Play",
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = word,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccuracyProgressBar(label: String, accuracy: Int, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
            Text(text = "$accuracy%", fontSize = 12.sp, fontWeight = FontWeight.Black, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { accuracy / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = color,
            trackColor = Color(0xFFE2E8F0)
        )
    }
}
