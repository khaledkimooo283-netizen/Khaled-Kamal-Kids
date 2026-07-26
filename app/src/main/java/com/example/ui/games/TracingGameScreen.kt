package com.example.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.data.HandwritingData
import com.example.data.KkDataRepository
import com.example.data.TracingGuideItem
import com.example.ui.components.*

@Composable
fun TracingGameScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    // Mode: "uppercase", "lowercase", "numbers"
    var mode by remember { mutableStateOf("uppercase") }
    var currentIndex by remember { mutableIntStateOf(0) }
    var userStars by remember { mutableIntStateOf(repository.getStars()) }

    val currentList: List<TracingGuideItem> = when (mode) {
        "uppercase" -> HandwritingData.uppercaseLetters
        "lowercase" -> HandwritingData.lowercaseLetters
        else -> HandwritingData.numbers
    }

    val currentItem = currentList.getOrNull(currentIndex) ?: currentList.first()

    var userDrawnPoints by remember { mutableStateOf(listOf<Pair<Float, Float>>()) }
    var strokeSuccess by remember { mutableStateOf(false) }
    var isFailedAttempt by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("Start tracing at the GREEN dot! 🟢") }

    var isShaking by remember { mutableStateOf(false) }
    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    // Shake animation on mistake
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(isShaking) {
        if (isShaking) {
            repeat(3) {
                shakeOffset.animateTo(14f, tween(45))
                shakeOffset.animateTo(-14f, tween(45))
            }
            shakeOffset.animateTo(0f, tween(45))
            isShaking = false
        }
    }

    // Pulse animation for direction arrow
    val transition = rememberInfiniteTransition()
    val arrowPulse by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Speak item name on item/mode change
    LaunchedEffect(currentIndex, mode) {
        userDrawnPoints = emptyList()
        strokeSuccess = false
        isFailedAttempt = false
        feedbackMessage = "Start tracing at the GREEN dot! 🟢"

        if (mode == "uppercase") {
            audioEngine.speakPhonetic(currentItem.character, currentItem.word)
        } else if (mode == "lowercase") {
            audioEngine.speak("lowercase ${currentItem.character} for ${currentItem.word}")
        } else {
            audioEngine.speak("${currentItem.character}... ${currentItem.word}!")
        }
    }

    val guideStrokes = currentItem.strokeGuidePoints

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F2FE))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(shakeOffset.value.toInt(), 0) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Handwriting & Tracing ✏️",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Category Mode Selector (3 modes: ABC, abc, 123)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("uppercase" to "ABC Capital", "lowercase" to "abc Small", "numbers" to "123 Numbers").forEach { (catKey, label) ->
                    val isSelected = mode == catKey
                    Button(
                        onClick = {
                            mode = catKey
                            currentIndex = 0
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFF0284C7) else Color(0xFFBAE6FD)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else Color(0xFF0369A1),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Quick Scrollable Character Carousel
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsIndexed(currentList) { idx, item ->
                    val isSelected = idx == currentIndex
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color(0xFF0284C7) else Color.White)
                            .clickable { currentIndex = idx }
                            .border(1.5.dp, if (isSelected) Color(0xFF0369A1) else Color(0xFF93C5FD), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.character,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = if (isSelected) Color.White else Color(0xFF0F172A)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Item Title & Prev/Next Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        val max = currentList.size
                        currentIndex = (currentIndex - 1 + max) % max
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous", tint = Color(0xFF0284C7))
                }

                Text(
                    text = "${currentItem.displayTitle} ${currentItem.emoji}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0369A1)
                )

                IconButton(
                    onClick = {
                        val max = currentList.size
                        currentIndex = (currentIndex + 1) % max
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Next", tint = Color(0xFF0284C7))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Professional Handwriting Tracing Board with Guidelines
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .border(
                        4.dp,
                        when {
                            strokeSuccess -> Color(0xFF22C55E)
                            isFailedAttempt -> Color(0xFFEF4444)
                            else -> Color(0xFF38BDF8)
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
                                        userDrawnPoints = listOf(Pair(offset.x, offset.y))
                                        isFailedAttempt = false
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        userDrawnPoints = userDrawnPoints + Pair(change.position.x, change.position.y)
                                    },
                                    onDragEnd = {
                                        val canvasSize = size.width.toFloat()
                                        val validation = HandwritingData.validateHandwritingTracing(
                                            drawnPoints = userDrawnPoints,
                                            strokes = guideStrokes,
                                            canvasSize = canvasSize
                                        )

                                        if (validation.isValid) {
                                            strokeSuccess = true
                                            isFailedAttempt = false
                                            feedbackMessage = "⭐ Excellent Handwriting! +3 Stars"
                                            showConfetti = true
                                            repository.addStars(3)
                                            userStars = repository.getStars()
                                            audioEngine.speakPraise()
                                            showRewardDialog = true
                                        } else {
                                            isFailedAttempt = true
                                            isShaking = true
                                            feedbackMessage = "❌ ${validation.message}"
                                            audioEngine.speakTryAgain()
                                        }
                                    }
                                )
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    // 1. Kindergarten Educational Guidelines
                    // Top line (y = 0.12 * h)
                    drawLine(
                        color = Color(0xFFCBD5E1),
                        start = Offset(0f, 0.12f * h),
                        end = Offset(w, 0.12f * h),
                        strokeWidth = 2f
                    )
                    // Mid line (dashed pink, y = 0.50 * h)
                    drawLine(
                        color = Color(0xFFF472B6),
                        start = Offset(0f, 0.50f * h),
                        end = Offset(w, 0.50f * h),
                        strokeWidth = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                    )
                    // Baseline (solid blue, y = 0.88 * h)
                    drawLine(
                        color = Color(0xFF38BDF8),
                        start = Offset(0f, 0.88f * h),
                        end = Offset(w, 0.88f * h),
                        strokeWidth = 3f
                    )

                    // 2. Outer Gray Guide Track (Educational width)
                    guideStrokes.forEach { strokeList ->
                        if (strokeList.size > 1) {
                            val trackPath = Path()
                            trackPath.moveTo(strokeList[0].first * w, strokeList[0].second * h)
                            for (i in 1 until strokeList.size) {
                                trackPath.lineTo(strokeList[i].first * w, strokeList[i].second * h)
                            }
                            drawPath(
                                path = trackPath,
                                color = Color(0xFFF1F5F9),
                                style = Stroke(width = 50f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                            // Inner Dashed Center Guide
                            drawPath(
                                path = trackPath,
                                color = Color(0xFF0284C7),
                                style = Stroke(
                                    width = 8f,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
                                )
                            )
                        }
                    }

                    // 3. Start Dots 🟢 & End Dots 🔴 with Directional Arrows
                    guideStrokes.forEachIndexed { strokeIdx, strokeList ->
                        if (strokeList.isNotEmpty()) {
                            val startX = strokeList.first().first * w
                            val startY = strokeList.first().second * h
                            val endX = strokeList.last().first * w
                            val endY = strokeList.last().second * h

                            // Green Start Dot
                            drawCircle(
                                color = Color(0xFF16A34A),
                                radius = 18f,
                                center = Offset(startX, startY)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 7f,
                                center = Offset(startX, startY)
                            )

                            // Red Finish Dot
                            drawCircle(
                                color = Color(0xFFDC2626),
                                radius = 14f,
                                center = Offset(endX, endY)
                            )

                            // Directional Arrow Hint on stroke mid-point
                            if (strokeList.size > 2) {
                                val midIdx = strokeList.size / 2
                                val midX = strokeList[midIdx].first * w
                                val midY = strokeList[midIdx].second * h
                                drawCircle(
                                    color = Color(0xFF0284C7),
                                    radius = 10f * arrowPulse,
                                    center = Offset(midX, midY)
                                )
                            }
                        }
                    }

                    // 4. Draw User Active Tracing Path
                    if (userDrawnPoints.size > 1) {
                        val userPath = Path()
                        userPath.moveTo(userDrawnPoints[0].first, userDrawnPoints[0].second)
                        for (i in 1 until userDrawnPoints.size) {
                            userPath.lineTo(userDrawnPoints[i].first, userDrawnPoints[i].second)
                        }
                        drawPath(
                            path = userPath,
                            color = when {
                                strokeSuccess -> Color(0xFF22C55E)
                                isFailedAttempt -> Color(0xFFEF4444)
                                else -> Color(0xFFEC4899)
                            },
                            style = Stroke(width = 32f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Feedback Status Text Banner
            Text(
                text = feedbackMessage,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isFailedAttempt) Color(0xFFDC2626) else Color(0xFF0369A1),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Try Again Button
            Button(
                onClick = {
                    userDrawnPoints = emptyList()
                    strokeSuccess = false
                    isFailedAttempt = false
                    feedbackMessage = "Start tracing at the GREEN dot! 🟢"
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = "Clear", tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Try Again", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Mascot Lion Encouragement
            KkLionMascot(
                state = if (strokeSuccess) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = if (strokeSuccess) "Super job tracing!" else "Start at green dot! 🟢",
                onClick = {
                    if (mode == "uppercase") {
                        audioEngine.speakPhonetic(currentItem.character, currentItem.word)
                    } else if (mode == "lowercase") {
                        audioEngine.speak("lowercase ${currentItem.character} for ${currentItem.word}")
                    } else {
                        audioEngine.speak("${currentItem.character}... ${currentItem.word}!")
                    }
                }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Super Writer!",
            message = "You traced ${currentItem.displayTitle} with excellent accuracy!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                val max = currentList.size
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
