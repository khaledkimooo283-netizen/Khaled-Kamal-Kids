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
    var rainbowModeEnabled by remember { mutableStateOf(true) }
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
    var feedbackMessage by remember { mutableStateOf("Start tracing anywhere on the letter! ✏️") }

    var isDemoPlaying by remember { mutableStateOf(false) }
    var demoProgress by remember { mutableFloatStateOf(0f) }

    var isShaking by remember { mutableStateOf(false) }
    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    // Demo Coach Animation loop
    LaunchedEffect(isDemoPlaying) {
        if (isDemoPlaying) {
            demoProgress = 0f
            while (isDemoPlaying && demoProgress < 1.0f) {
                kotlinx.coroutines.delay(25)
                demoProgress += 0.02f
            }
            demoProgress = 1.0f
            kotlinx.coroutines.delay(350)
            isDemoPlaying = false
            if (!strokeSuccess) {
                feedbackMessage = "Now your turn! Trace ${currentItem.character} on the lines ✏️"
            }
        }
    }

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

    // Speak item name on item/mode change & play demo first
    LaunchedEffect(currentIndex, mode) {
        userDrawnPoints = emptyList()
        strokeSuccess = false
        isFailedAttempt = false
        isDemoPlaying = true
        feedbackMessage = "Watch Coach write ${currentItem.character} first! 🎬"

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
                        .pointerInput(currentIndex, mode, strokeSuccess, isDemoPlaying) {
                            if (!strokeSuccess && !isDemoPlaying) {
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
                                            feedbackMessage = "⭐ Excellent! I wrote the letter ${currentItem.character}! +3 Stars"
                                            showConfetti = true
                                            repository.addStars(3)
                                            userStars = repository.getStars()
                                            audioEngine.speak("Excellent! I wrote the letter ${currentItem.character}!")
                                            showRewardDialog = true
                                        } else {
                                            isFailedAttempt = true
                                            isShaking = true
                                            userDrawnPoints = emptyList()
                                            feedbackMessage = "❌ ${validation.message}"
                                            audioEngine.speakTryAgain()
                                            isDemoPlaying = true
                                        }
                                    }
                                )
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    // 1. English 4-Line Notebook Ruling
                    // Top line / Headline (Red, y = 0.12 * h)
                    drawLine(
                        color = Color(0xFFEF4444),
                        start = Offset(0f, 0.12f * h),
                        end = Offset(w, 0.12f * h),
                        strokeWidth = 2.5f
                    )
                    // Midline (Dashed Blue, y = 0.50 * h)
                    drawLine(
                        color = Color(0xFF3B82F6),
                        start = Offset(0f, 0.50f * h),
                        end = Offset(w, 0.50f * h),
                        strokeWidth = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                    )
                    // Baseline (Solid Dark Blue, y = 0.88 * h)
                    drawLine(
                        color = Color(0xFF1D4ED8),
                        start = Offset(0f, 0.88f * h),
                        end = Offset(w, 0.88f * h),
                        strokeWidth = 3.5f
                    )
                    // Descender / Bottom line (Dashed Light Purple, y = 0.95 * h)
                    drawLine(
                        color = Color(0xFFC084FC),
                        start = Offset(0f, 0.95f * h),
                        end = Offset(w, 0.95f * h),
                        strokeWidth = 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                    )

                    // 2. Outer Guide Track
                    guideStrokes.forEach { strokeList ->
                        if (strokeList.size > 1) {
                            val trackPath = Path()
                            trackPath.moveTo(strokeList[0].first * w, strokeList[0].second * h)
                            for (i in 1 until strokeList.size) {
                                trackPath.lineTo(strokeList[i].first * w, strokeList[i].second * h)
                            }

                            if (strokeSuccess) {
                                // Solid Vibrant Green/Gold Completion Path Animation replacing dotted lines
                                drawPath(
                                    path = trackPath,
                                    color = Color(0xFF16A34A),
                                    style = Stroke(width = 46f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                                drawPath(
                                    path = trackPath,
                                    color = Color(0xFF86EFAC),
                                    style = Stroke(width = 24f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            } else {
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
                    }

                    // 3. Start & Guide Dots
                    if (!strokeSuccess) {
                        guideStrokes.forEachIndexed { strokeIdx, strokeList ->
                            if (strokeList.isNotEmpty()) {
                                val startX = strokeList.first().first * w
                                val startY = strokeList.first().second * h

                                // Gentle Start Dot
                                drawCircle(
                                    color = Color(0xFF16A34A),
                                    radius = 16f,
                                    center = Offset(startX, startY)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 6f,
                                    center = Offset(startX, startY)
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
                            style = Stroke(width = 34f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }

                    // 5. Draw Animated Demo Pen Path
                    if (isDemoPlaying) {
                        val allDemoPts = guideStrokes.flatten()
                        if (allDemoPts.isNotEmpty()) {
                            val targetIndex = (demoProgress * (allDemoPts.size - 1)).toInt().coerceIn(0, allDemoPts.size - 1)
                            val demoPath = Path()
                            demoPath.moveTo(allDemoPts[0].first * w, allDemoPts[0].second * h)
                            for (i in 1..targetIndex) {
                                demoPath.lineTo(allDemoPts[i].first * w, allDemoPts[i].second * h)
                            }
                            drawPath(
                                path = demoPath,
                                color = Color(0xFF0284C7),
                                style = Stroke(width = 24f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                            val tip = allDemoPts[targetIndex]
                            drawCircle(
                                color = Color(0xFFF59E0B),
                                radius = 22f,
                                center = Offset(tip.first * w, tip.second * h)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 10f,
                                center = Offset(tip.first * w, tip.second * h)
                            )
                        }
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

            // Try Again / Clear Button
            Button(
                onClick = {
                    userDrawnPoints = emptyList()
                    strokeSuccess = false
                    isFailedAttempt = false
                    feedbackMessage = "Start tracing anywhere on the letter! ✏️"
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
                speechBubbleText = if (strokeSuccess) "Super job writing!" else "Start tracing anywhere on the letter! ✏️",
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
