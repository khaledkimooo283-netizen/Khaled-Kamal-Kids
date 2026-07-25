package com.example.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.ui.components.*
import kotlin.math.hypot

@Composable
fun TracingGameScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var mode by remember { mutableStateOf("alphabet") } // "alphabet" or "numbers"
    var currentIndex by remember { mutableIntStateOf(0) }
    var userStars by remember { mutableIntStateOf(repository.getStars()) }

    val currentLetterItem = repository.alphabetList.getOrNull(currentIndex) ?: repository.alphabetList.first()
    val currentNumberItem = repository.numberList.getOrNull(currentIndex) ?: repository.numberList.first()

    var userDrawnPoints by remember { mutableStateOf(listOf<Offset>()) }
    var strokeSuccess by remember { mutableStateOf(false) }
    var isFailedAttempt by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("Trace inside the path starting from the GREEN dot! 🟢") }

    var isShaking by remember { mutableStateOf(false) }
    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    // Shake animation
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(isShaking) {
        if (isShaking) {
            repeat(3) {
                shakeOffset.animateTo(16f, tween(50))
                shakeOffset.animateTo(-16f, tween(50))
            }
            shakeOffset.animateTo(0f, tween(50))
            isShaking = false
        }
    }

    // Speak item name on item change
    LaunchedEffect(currentIndex, mode) {
        userDrawnPoints = emptyList()
        strokeSuccess = false
        isFailedAttempt = false
        feedbackMessage = "Start tracing at the GREEN dot! 🟢"
        if (mode == "alphabet") {
            audioEngine.speakPhonetic(
                currentLetterItem.letter.toString(),
                currentLetterItem.word
            )
        } else {
            audioEngine.speak("${currentNumberItem.number}... ${currentNumberItem.word}!")
        }
    }

    val guideStrokes = if (mode == "alphabet") currentLetterItem.strokeGuidePoints else currentNumberItem.strokeGuidePoints
    val itemTitle = if (mode == "alphabet") "${currentLetterItem.letter} for ${currentLetterItem.word} ${currentLetterItem.emoji}" else "${currentNumberItem.number} (${currentNumberItem.word}) ${currentNumberItem.emoji}"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(shakeOffset.value.toInt(), 0) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Tracing & Writing ✏️",
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
                    onClick = { mode = "alphabet"; currentIndex = 0 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (mode == "alphabet") Color(0xFF1E88E5) else Color(0xFFBBDEFB)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("ABC Alphabet", color = if (mode == "alphabet") Color.White else Color(0xFF1565C0), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = { mode = "numbers"; currentIndex = 0 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (mode == "numbers") Color(0xFF1E88E5) else Color(0xFFBBDEFB)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("123 Numbers", color = if (mode == "numbers") Color.White else Color(0xFF1565C0), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Item Title & Selector Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        val max = if (mode == "alphabet") repository.alphabetList.size else repository.numberList.size
                        currentIndex = (currentIndex - 1 + max) % max
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous", tint = Color(0xFF1976D2))
                }

                Text(
                    text = itemTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0D47A1)
                )

                IconButton(
                    onClick = {
                        val max = if (mode == "alphabet") repository.alphabetList.size else repository.numberList.size
                        currentIndex = (currentIndex + 1) % max
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Next", tint = Color(0xFF1976D2))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tracing Board
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .border(
                        4.dp,
                        when {
                            strokeSuccess -> Color(0xFF4CAF50)
                            isFailedAttempt -> Color(0xFFE53935)
                            else -> Color(0xFF90CAF9)
                        },
                        RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(currentIndex, mode, strokeSuccess) {
                            if (!strokeSuccess) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        userDrawnPoints = listOf(offset)
                                        isFailedAttempt = false
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        userDrawnPoints = userDrawnPoints + change.position
                                    },
                                    onDragEnd = {
                                        val canvasSize = size.width.toFloat()
                                        val (isValid, coverage, accuracy) = validateTracing(userDrawnPoints, guideStrokes, canvasSize)

                                        if (isValid) {
                                            strokeSuccess = true
                                            isFailedAttempt = false
                                            feedbackMessage = "✅ Excellent Tracing! ⭐ +3 Stars"
                                            showConfetti = true
                                            repository.addStars(3)
                                            userStars = repository.getStars()
                                            audioEngine.speakPraise()
                                            showRewardDialog = true
                                        } else {
                                            isFailedAttempt = true
                                            isShaking = true
                                            feedbackMessage = if (accuracy < 0.80f) {
                                                "❌ Stay inside the lines! Try again ✏️"
                                            } else {
                                                "❌ Almost! Cover the full letter path ✏️"
                                            }
                                            audioEngine.speakTryAgain()
                                        }
                                    }
                                )
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    // 1. Draw Gray Background Track (Educational Width)
                    guideStrokes.forEach { strokeList ->
                        if (strokeList.size > 1) {
                            val trackPath = Path()
                            trackPath.moveTo(strokeList[0].first * w, strokeList[0].second * h)
                            for (i in 1 until strokeList.size) {
                                trackPath.lineTo(strokeList[i].first * w, strokeList[i].second * h)
                            }
                            drawPath(
                                path = trackPath,
                                color = Color(0xFFECEFF1),
                                style = Stroke(width = 54f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                            // Dashed Center Guide
                            drawPath(
                                path = trackPath,
                                color = Color(0xFF29B6F6),
                                style = Stroke(
                                    width = 10f,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)
                                )
                            )
                        }
                    }

                    // 2. Draw Start & End Markers with Numbers
                    guideStrokes.forEachIndexed { strokeIdx, strokeList ->
                        if (strokeList.isNotEmpty()) {
                            val startX = strokeList.first().first * w
                            val startY = strokeList.first().second * h
                            val endX = strokeList.last().first * w
                            val endY = strokeList.last().second * h

                            // Green Start Dot
                            drawCircle(
                                color = Color(0xFF43A047),
                                radius = 18f,
                                center = Offset(startX, startY)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 8f,
                                center = Offset(startX, startY)
                            )

                            // Red End Dot
                            drawCircle(
                                color = Color(0xFFE53935),
                                radius = 14f,
                                center = Offset(endX, endY)
                            )
                        }
                    }

                    // 3. Draw User's Active Stroke
                    if (userDrawnPoints.size > 1) {
                        val userPath = Path()
                        userPath.moveTo(userDrawnPoints[0].x, userDrawnPoints[0].y)
                        for (i in 1 until userDrawnPoints.size) {
                            userPath.lineTo(userDrawnPoints[i].x, userDrawnPoints[i].y)
                        }
                        drawPath(
                            path = userPath,
                            color = when {
                                strokeSuccess -> Color(0xFF4CAF50)
                                isFailedAttempt -> Color(0xFFE53935)
                                else -> Color(0xFFFF4081)
                            },
                            style = Stroke(width = 36f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Feedback Status Text Banner
            Text(
                text = feedbackMessage,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isFailedAttempt) Color(0xFFC62828) else Color(0xFF1565C0),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Reset / Clear Button
            Button(
                onClick = {
                    userDrawnPoints = emptyList()
                    strokeSuccess = false
                    isFailedAttempt = false
                    feedbackMessage = "Start tracing at the GREEN dot! 🟢"
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = "Clear", tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Try Again", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Mascot Lion
            KkLionMascot(
                state = if (strokeSuccess) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = if (strokeSuccess) "Super job tracing!" else "Start at green dot! 🟢",
                onClick = {
                    if (mode == "alphabet") {
                        audioEngine.speakPhonetic(currentLetterItem.letter.toString(), currentLetterItem.word)
                    } else {
                        audioEngine.speak("${currentNumberItem.number}... ${currentNumberItem.word}!")
                    }
                }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Super Writer!",
            message = "You traced $itemTitle with great accuracy!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                val max = if (mode == "alphabet") repository.alphabetList.size else repository.numberList.size
                currentIndex = (currentIndex + 1) % max
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}

/**
 * Validates tracing for both high path coverage (>= 88%) AND high user accuracy (>= 82% of drawn points stay on the track).
 */
private fun validateTracing(
    userPoints: List<Offset>,
    strokes: List<List<Pair<Float, Float>>>,
    canvasSize: Float
): Triple<Boolean, Float, Float> {
    if (userPoints.size < 12) return Triple(false, 0f, 0f)

    // Allowed tolerance radius (~45dp)
    val allowedRadius = canvasSize * 0.16f

    var totalGuidePoints = 0
    var coveredGuidePoints = 0

    strokes.forEach { stroke ->
        stroke.forEach { guideP ->
            totalGuidePoints++
            val gx = guideP.first * canvasSize
            val gy = guideP.second * canvasSize
            val isCovered = userPoints.any { u -> hypot(u.x - gx, u.y - gy) <= allowedRadius }
            if (isCovered) coveredGuidePoints++
        }
    }

    val coverageRatio = if (totalGuidePoints > 0) coveredGuidePoints.toFloat() / totalGuidePoints.toFloat() else 1.0f

    // User accuracy check: what percentage of user points were near the guide?
    var pointsOnTrack = 0
    userPoints.forEach { u ->
        var onTrack = false
        for (stroke in strokes) {
            for (guideP in stroke) {
                val gx = guideP.first * canvasSize
                val gy = guideP.second * canvasSize
                if (hypot(u.x - gx, u.y - gy) <= allowedRadius) {
                    onTrack = true
                    break
                }
            }
            if (onTrack) break
        }
        if (onTrack) pointsOnTrack++
    }

    val accuracyRatio = pointsOnTrack.toFloat() / userPoints.size.toFloat()

    val isValid = coverageRatio >= 0.85f && accuracyRatio >= 0.82f
    return Triple(isValid, coverageRatio, accuracyRatio)
}
