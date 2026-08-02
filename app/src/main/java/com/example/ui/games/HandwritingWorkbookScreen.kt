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
import androidx.compose.material.icons.filled.PlayArrow
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
import kotlinx.coroutines.delay

@Composable
fun HandwritingWorkbookScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    // Categories: "uppercase", "lowercase", "numbers", "words", "first_letter", "missing_letter"
    var categoryMode by remember { mutableStateOf("uppercase") }
    var currentIndex by remember { mutableIntStateOf(0) }
    var userStars by remember { mutableIntStateOf(repository.getStars()) }

    // First Letter of Picture Activities
    val firstLetterList = remember {
        listOf(
            TracingGuideItem("fl_apple", "First Letter of Apple 🍎", "A", "first_letter", "Apple", "🍎", HandwritingData.uppercaseLetters[0].strokeGuidePoints),
            TracingGuideItem("fl_ball", "First Letter of Ball ⚽", "B", "first_letter", "Ball", "⚽", HandwritingData.uppercaseLetters[1].strokeGuidePoints),
            TracingGuideItem("fl_cat", "First Letter of Cat 🐱", "C", "first_letter", "Cat", "🐱", HandwritingData.uppercaseLetters[2].strokeGuidePoints),
            TracingGuideItem("fl_dog", "First Letter of Dog 🐶", "D", "first_letter", "Dog", "🐶", HandwritingData.uppercaseLetters[3].strokeGuidePoints),
            TracingGuideItem("fl_egg", "First Letter of Egg 🥚", "E", "first_letter", "Egg", "🥚", HandwritingData.uppercaseLetters[4].strokeGuidePoints)
        )
    }

    // Missing Letter Activities
    val missingLetterList = remember {
        listOf(
            TracingGuideItem("ml_apple", "Write missing letter: _ p p l e 🍎", "A", "missing_letter", "Apple", "🍎", HandwritingData.uppercaseLetters[0].strokeGuidePoints),
            TracingGuideItem("ml_ball", "Write missing letter: _ a l l ⚽", "B", "missing_letter", "Ball", "⚽", HandwritingData.uppercaseLetters[1].strokeGuidePoints),
            TracingGuideItem("ml_cat", "Write missing letter: _ a t 🐱", "C", "missing_letter", "Cat", "🐱", HandwritingData.uppercaseLetters[2].strokeGuidePoints),
            TracingGuideItem("ml_dog", "Write missing letter: _ o g 🐶", "D", "missing_letter", "Dog", "🐶", HandwritingData.uppercaseLetters[3].strokeGuidePoints)
        )
    }

    // Custom simple words list for writing practice
    val wordsList = remember {
        listOf(
            TracingGuideItem("w_apple", "Apple 🍎", "Apple", "words", "Apple", "🍎", listOf(
                HandwritingData.generateLine(0.15f, 0.88f, 0.32f, 0.12f, 15),
                HandwritingData.generateLine(0.32f, 0.12f, 0.48f, 0.88f, 15),
                HandwritingData.generateLine(0.22f, 0.55f, 0.42f, 0.55f, 10),
                HandwritingData.generateCircle(0.60f, 0.69f, 0.08f, 0.12f, -90.0, 360.0, 15),
                HandwritingData.generateLine(0.68f, 0.50f, 0.68f, 0.88f, 10),
                HandwritingData.generateLine(0.78f, 0.50f, 0.78f, 0.88f, 10),
                HandwritingData.generateCircle(0.86f, 0.69f, 0.08f, 0.12f, -90.0, 360.0, 15)
            )),
            TracingGuideItem("w_ball", "Ball ⚽", "Ball", "words", "Ball", "⚽", listOf(
                HandwritingData.generateLine(0.15f, 0.12f, 0.15f, 0.88f, 15),
                HandwritingData.generateCubicBezier(0.15f, 0.12f, 0.35f, 0.12f, 0.35f, 0.50f, 0.15f, 0.50f, 12),
                HandwritingData.generateCubicBezier(0.15f, 0.50f, 0.38f, 0.50f, 0.38f, 0.88f, 0.15f, 0.88f, 12),
                HandwritingData.generateCircle(0.50f, 0.69f, 0.08f, 0.12f, -90.0, 360.0, 15),
                HandwritingData.generateLine(0.58f, 0.50f, 0.58f, 0.88f, 10),
                HandwritingData.generateLine(0.70f, 0.12f, 0.70f, 0.88f, 12),
                HandwritingData.generateLine(0.85f, 0.12f, 0.85f, 0.88f, 12)
            )),
            TracingGuideItem("w_cat", "Cat 🐱", "Cat", "words", "Cat", "🐱", listOf(
                HandwritingData.generateCubicBezier(0.35f, 0.25f, 0.30f, 0.12f, 0.18f, 0.12f, 0.18f, 0.50f, 12) +
                    HandwritingData.generateCubicBezier(0.18f, 0.50f, 0.18f, 0.88f, 0.30f, 0.88f, 0.35f, 0.75f, 12),
                HandwritingData.generateCircle(0.52f, 0.69f, 0.08f, 0.12f, -90.0, 360.0, 15),
                HandwritingData.generateLine(0.60f, 0.50f, 0.60f, 0.88f, 10),
                HandwritingData.generateLine(0.75f, 0.22f, 0.75f, 0.82f, 12),
                HandwritingData.generateLine(0.65f, 0.50f, 0.85f, 0.50f, 10)
            )),
            TracingGuideItem("w_dog", "Dog 🐶", "Dog", "words", "Dog", "🐶", listOf(
                HandwritingData.generateLine(0.18f, 0.12f, 0.18f, 0.88f, 15),
                HandwritingData.generateCubicBezier(0.18f, 0.12f, 0.45f, 0.12f, 0.45f, 0.88f, 0.18f, 0.88f, 18),
                HandwritingData.generateCircle(0.60f, 0.69f, 0.08f, 0.12f, -90.0, 360.0, 15),
                HandwritingData.generateCircle(0.82f, 0.65f, 0.08f, 0.12f, -90.0, 360.0, 15),
                HandwritingData.generateLine(0.90f, 0.50f, 0.90f, 0.88f, 12)
            )),
            TracingGuideItem("w_sun", "Sun ☀️", "Sun", "words", "Sun", "☀️", listOf(
                HandwritingData.generateCubicBezier(0.35f, 0.22f, 0.30f, 0.12f, 0.18f, 0.12f, 0.18f, 0.32f, 10) +
                    HandwritingData.generateCubicBezier(0.18f, 0.32f, 0.18f, 0.52f, 0.35f, 0.52f, 0.35f, 0.72f, 10) +
                    HandwritingData.generateCubicBezier(0.35f, 0.72f, 0.35f, 0.88f, 0.18f, 0.88f, 0.15f, 0.80f, 8),
                HandwritingData.generateCubicBezier(0.48f, 0.50f, 0.48f, 0.88f, 0.65f, 0.88f, 0.65f, 0.50f, 15),
                HandwritingData.generateLine(0.65f, 0.50f, 0.65f, 0.88f, 10),
                HandwritingData.generateLine(0.78f, 0.50f, 0.78f, 0.88f, 10),
                HandwritingData.generateCubicBezier(0.78f, 0.60f, 0.82f, 0.50f, 0.95f, 0.50f, 0.95f, 0.88f, 12)
            ))
        )
    }

    val currentList: List<TracingGuideItem> = when (categoryMode) {
        "uppercase" -> HandwritingData.uppercaseLetters
        "lowercase" -> HandwritingData.lowercaseLetters
        "numbers" -> HandwritingData.numbers
        "first_letter" -> firstLetterList
        "missing_letter" -> missingLetterList
        else -> wordsList
    }

    val currentItem = currentList.getOrNull(currentIndex) ?: currentList.first()

    var userDrawnPoints by remember { mutableStateOf(listOf<Pair<Float, Float>>()) }
    var strokeSuccess by remember { mutableStateOf(false) }
    var isFailedAttempt by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("Watch Coach write first or trace over the dotted lines! ✍️") }

    var isDemoPlaying by remember { mutableStateOf(false) }
    var demoProgress by remember { mutableFloatStateOf(0f) }

    var isShaking by remember { mutableStateOf(false) }
    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    // Shake animation on error
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(isShaking) {
        if (isShaking) {
            repeat(3) {
                shakeOffset.animateTo(12f, tween(45))
                shakeOffset.animateTo(-12f, tween(45))
            }
            shakeOffset.animateTo(0f, tween(45))
            isShaking = false
        }
    }

    // Demo Coach Animation loop
    LaunchedEffect(isDemoPlaying) {
        if (isDemoPlaying) {
            demoProgress = 0f
            while (isDemoPlaying && demoProgress < 1.0f) {
                delay(30)
                demoProgress += 0.02f
            }
            demoProgress = 1.0f
            delay(500)
            isDemoPlaying = false
        }
    }

    // Speak title & reset canvas when item changes
    LaunchedEffect(currentIndex, categoryMode) {
        userDrawnPoints = emptyList()
        strokeSuccess = false
        isFailedAttempt = false
        isDemoPlaying = false
        feedbackMessage = "Watch Coach write first or trace over the dotted lines! ✍️"

        audioEngine.speak("Let's write ${currentItem.displayTitle}!")
    }

    val guideStrokes = currentItem.strokeGuidePoints

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF3C7)) // Warm Paper Canvas Tint
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(shakeOffset.value.toInt(), 0) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "English Handwriting Workbook ✍️",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Category Selector Tabs
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(
                    listOf(
                        "uppercase" to "ABC Capital",
                        "lowercase" to "abc Small",
                        "numbers" to "123 Numbers",
                        "first_letter" to "First Letter 🍎",
                        "missing_letter" to "Missing Letter 🧩",
                        "words" to "Words 📝"
                    )
                ) { _, (catKey, label) ->
                    val isSelected = categoryMode == catKey
                    Button(
                        onClick = {
                            categoryMode = catKey
                            currentIndex = 0
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFFD97706) else Color(0xFFFDE68A)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 6.dp, horizontal = 10.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else Color(0xFF78350F),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Quick Item Carousel
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
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color(0xFFD97706) else Color.White)
                            .clickable { currentIndex = idx }
                            .border(1.5.dp, if (isSelected) Color(0xFFB45309) else Color(0xFFFCD34D), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.character.take(2),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = if (isSelected) Color.White else Color(0xFF78350F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Item Title & Watch Coach Button
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
                        .size(38.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous", tint = Color(0xFFD97706))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${currentItem.displayTitle} ${currentItem.emoji}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF78350F)
                    )
                }

                IconButton(
                    onClick = {
                        val max = currentList.size
                        currentIndex = (currentIndex + 1) % max
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Next", tint = Color(0xFFD97706))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 4-Line Copybook Workbook Canvas
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFFFBEB)) // Clean Notebook Paper
                    .border(
                        3.5.dp,
                        when {
                            strokeSuccess -> Color(0xFF16A34A)
                            isFailedAttempt -> Color(0xFFDC2626)
                            else -> Color(0xFFF59E0B)
                        },
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(currentIndex, categoryMode, strokeSuccess, isDemoPlaying) {
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
                                            feedbackMessage = "⭐ Excellent handwriting! Perfect stroke! +3 Stars"
                                            showConfetti = true
                                            repository.addStars(3)
                                            userStars = repository.getStars()
                                            audioEngine.speak("Superb! You wrote ${currentItem.character}!")
                                            showRewardDialog = true
                                        } else {
                                            isFailedAttempt = true
                                            isShaking = true
                                            feedbackMessage = "❌ ${validation.message}"
                                            audioEngine.speakTryAgain()
                                            // Trigger demo playback so child sees how to write correctly
                                            isDemoPlaying = true
                                        }
                                    }
                                )
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    // 1. English Copybook 4-Line Ruling
                    // Top headline (Red, y = 0.12 * h)
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
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                    )
                    // Baseline (Solid Dark Blue, y = 0.88 * h)
                    drawLine(
                        color = Color(0xFF1D4ED8),
                        start = Offset(0f, 0.88f * h),
                        end = Offset(w, 0.88f * h),
                        strokeWidth = 3f
                    )
                    // Descender line (Light Pink, y = 0.95 * h)
                    drawLine(
                        color = Color(0xFFF472B6),
                        start = Offset(0f, 0.95f * h),
                        end = Offset(w, 0.95f * h),
                        strokeWidth = 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )

                    // 2. Guide Strokes & Dotted Paths
                    guideStrokes.forEach { strokeList ->
                        if (strokeList.size > 1) {
                            val trackPath = Path()
                            trackPath.moveTo(strokeList[0].first * w, strokeList[0].second * h)
                            for (i in 1 until strokeList.size) {
                                trackPath.lineTo(strokeList[i].first * w, strokeList[i].second * h)
                            }

                            if (strokeSuccess) {
                                // Completed Gold/Green Path
                                drawPath(
                                    path = trackPath,
                                    color = Color(0xFF16A34A),
                                    style = Stroke(width = 36f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            } else {
                                // Light Background Track
                                drawPath(
                                    path = trackPath,
                                    color = Color(0xFFFEF3C7),
                                    style = Stroke(width = 40f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                                // Dotted Center Line
                                drawPath(
                                    path = trackPath,
                                    color = Color(0xFFD97706),
                                    style = Stroke(
                                        width = 8f,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round,
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                                    )
                                )
                            }
                        }
                    }

                    // 3. Demo Pencil Animation
                    if (isDemoPlaying) {
                        val allPoints = guideStrokes.flatten()
                        if (allPoints.isNotEmpty()) {
                            val totalP = allPoints.size
                            val currentPIdx = ((totalP - 1) * demoProgress).toInt().coerceIn(0, totalP - 1)
                            val demoX = allPoints[currentPIdx].first * w
                            val demoY = allPoints[currentPIdx].second * h

                            // Draw active demo trail
                            val demoTrailPath = Path()
                            demoTrailPath.moveTo(allPoints[0].first * w, allPoints[0].second * h)
                            for (i in 1..currentPIdx) {
                                demoTrailPath.lineTo(allPoints[i].first * w, allPoints[i].second * h)
                            }
                            drawPath(
                                path = demoTrailPath,
                                color = Color(0xFF2563EB),
                                style = Stroke(width = 24f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )

                            // Draw Pencil Icon Cursor
                            drawCircle(color = Color(0xFFDC2626), radius = 18f, center = Offset(demoX, demoY))
                            drawCircle(color = Color.White, radius = 7f, center = Offset(demoX, demoY))
                        }
                    }

                    // 4. Draw User Finger Handwriting Path
                    if (userDrawnPoints.size > 1 && !isDemoPlaying) {
                        val userPath = Path()
                        userPath.moveTo(userDrawnPoints[0].first, userDrawnPoints[0].second)
                        for (i in 1 until userDrawnPoints.size) {
                            userPath.lineTo(userDrawnPoints[i].first, userDrawnPoints[i].second)
                        }
                        drawPath(
                            path = userPath,
                            color = when {
                                strokeSuccess -> Color(0xFF16A34A)
                                isFailedAttempt -> Color(0xFFDC2626)
                                else -> Color(0xFF2563EB)
                            },
                            style = Stroke(width = 28f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons: Watch Coach Demo & Try Again
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        userDrawnPoints = emptyList()
                        strokeSuccess = false
                        isFailedAttempt = false
                        isDemoPlaying = true
                        audioEngine.speak("Watch how coach writes ${currentItem.character}!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "Demo", tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Watch Coach Write 🎬", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        userDrawnPoints = emptyList()
                        strokeSuccess = false
                        isFailedAttempt = false
                        isDemoPlaying = false
                        feedbackMessage = "Trace over the dotted lines! ✍️"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Clear", tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear Canvas", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = feedbackMessage,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isFailedAttempt) Color(0xFFDC2626) else Color(0xFF78350F),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            KkLionMascot(
                state = if (strokeSuccess) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = if (strokeSuccess) "Super writing!" else "Hold your finger and trace carefully!",
                onClick = {
                    audioEngine.speak("Let me see your neat writing!")
                }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Star Writer! ✍️",
            message = "You wrote ${currentItem.displayTitle} neatly on the workbook lines!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                val max = currentList.size
                currentIndex = (currentIndex + 1) % max
            },
            onHome = onBackClick
        )
    }
}
