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
import kotlin.math.hypot

@Composable
fun HandwritingWorkbookScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    // 8 Sub-Game Modes:
    // 1. "copybook" - English Copybook Writing
    // 2. "rainbow" - Rainbow Tracing
    // 3. "dots" - Connect The Dots
    // 4. "color_letter" - Color The Letter
    // 5. "copy_memory" - Copy After Me
    // 6. "numbers" - Number Handwriting (0-20)
    // 7. "shape_prep" - Shape Preparation
    // 8. "pencil_control" - Pencil Control (Follow the Road)
    var activeGameMode by remember { mutableStateOf("copybook") }

    // Content Category: "uppercase", "lowercase", "numbers", "words", "shape_prep", "pencil_control"
    var categoryMode by remember { mutableStateOf("uppercase") }
    var currentIndex by remember { mutableIntStateOf(0) }
    var userStars by remember { mutableIntStateOf(repository.getStars()) }

    // Rainbow tracing pass index (0 = Red, 1 = Blue, 2 = Green, 3 = Yellow, 4 = Purple)
    var rainbowPass by remember { mutableIntStateOf(0) }
    val rainbowColors = remember {
        listOf(
            Color(0xFFEF4444), // Red
            Color(0xFF2563EB), // Blue
            Color(0xFF16A34A), // Green
            Color(0xFFEAB308), // Yellow
            Color(0xFF9333EA)  // Purple
        )
    }

    // Connect the Dots active target index
    var connectedDotCount by remember { mutableIntStateOf(0) }

    // Color the letter painted state
    var isLetterColored by remember { mutableStateOf(false) }

    val currentList: List<TracingGuideItem> = when (activeGameMode) {
        "numbers" -> HandwritingData.numbers
        "shape_prep" -> HandwritingData.shapePrepItems
        "pencil_control" -> HandwritingData.pencilControlRoads
        else -> when (categoryMode) {
            "lowercase" -> HandwritingData.lowercaseLetters
            "numbers" -> HandwritingData.numbers
            else -> HandwritingData.uppercaseLetters
        }
    }

    val currentItem = currentList.getOrNull(currentIndex) ?: currentList.first()

    var userDrawnPoints by remember { mutableStateOf(listOf<Pair<Float, Float>>()) }
    var strokeSuccess by remember { mutableStateOf(false) }
    var isFailedAttempt by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("Watch Coach write first or trace neatly! ✍️") }

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
                delay(25)
                demoProgress += 0.02f
            }
            demoProgress = 1.0f
            delay(400)
            isDemoPlaying = false
        }
    }

    // Reset state on item or mode change
    LaunchedEffect(currentIndex, categoryMode, activeGameMode) {
        userDrawnPoints = emptyList()
        strokeSuccess = false
        isFailedAttempt = false
        isDemoPlaying = false
        rainbowPass = 0
        connectedDotCount = 0
        isLetterColored = false

        feedbackMessage = when (activeGameMode) {
            "copybook" -> "Watch Coach demo or write on notebook lines! ✍️"
            "rainbow" -> "Trace 5 times to complete the Rainbow! 🌈 (Pass 1/5)"
            "dots" -> "Connect dots 1, 2, 3... in order! 🔢"
            "color_letter" -> "Color the letter inside, then trace it! 🎨"
            "copy_memory" -> "Watch Coach write first, then copy from memory! 🧠"
            "numbers" -> "Learn & write number ${currentItem.character}! 🔟"
            "shape_prep" -> "Trace shapes to prepare your hand control! 📐"
            "pencil_control" -> "Drive along the road from Start 🚗 to Finish 🏁!"
            else -> "Trace over the dotted lines! ✍️"
        }

        audioEngine.speak("Let's practice ${currentItem.displayTitle}!")
    }

    val guideStrokes = currentItem.strokeGuidePoints

    // Generate Connect-The-Dots checkpoints
    val dotCheckpoints = remember(currentItem) {
        val flattened = currentItem.strokeGuidePoints.flatten()
        if (flattened.size > 8) {
            val step = (flattened.size - 1) / 7
            (0..7).map { i ->
                val idx = (i * step).coerceIn(0, flattened.size - 1)
                flattened[idx]
            }
        } else {
            flattened
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF3C7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(shakeOffset.value.toInt(), 0) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Handwriting Learning System ✍️",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // 8 Sub-Game Modes Selector Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val games = listOf(
                    "copybook" to "📝 Copybook Writing",
                    "rainbow" to "🌈 Rainbow Tracing",
                    "dots" to "🔢 Connect Dots",
                    "color_letter" to "🎨 Color & Write",
                    "copy_memory" to "🧠 Copy After Me",
                    "numbers" to "🔟 Numbers 0-20",
                    "shape_prep" to "📐 Shape Prep",
                    "pencil_control" to "🚗 Pencil Road"
                )
                itemsIndexed(games) { _, (gKey, label) ->
                    val isSelected = activeGameMode == gKey
                    Button(
                        onClick = {
                            activeGameMode = gKey
                            currentIndex = 0
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFFD97706) else Color(0xFFFDE68A)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 5.dp, horizontal = 10.dp)
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

            // Category Selector (for standard letter games)
            if (activeGameMode == "copybook" || activeGameMode == "rainbow" || activeGameMode == "dots" || activeGameMode == "color_letter" || activeGameMode == "copy_memory") {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val cats = listOf(
                        "uppercase" to "ABC Capital",
                        "lowercase" to "abc Small",
                        "numbers" to "123 Numbers"
                    )
                    itemsIndexed(cats) { _, (cKey, label) ->
                        val isSelected = categoryMode == cKey
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                categoryMode = cKey
                                currentIndex = 0
                            },
                            label = {
                                Text(
                                    label,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color.White else Color(0xFF78350F)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFB45309),
                                containerColor = Color.White
                            )
                        )
                    }
                }
            }

            // Quick Item Carousel
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsIndexed(currentList) { idx, item ->
                    val isSelected = idx == currentIndex
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color(0xFFD97706) else Color.White)
                            .clickable { currentIndex = idx }
                            .border(1.5.dp, if (isSelected) Color(0xFFB45309) else Color(0xFFFCD34D), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.character.take(2),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = if (isSelected) Color.White else Color(0xFF78350F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Item Title & Navigation Buttons
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
                        .size(36.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous", tint = Color(0xFFD97706))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${currentItem.displayTitle} ${currentItem.emoji}",
                        fontSize = 17.sp,
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
                        .size(36.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Next", tint = Color(0xFFD97706))
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Main Interactive Notebook & Game Canvas
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFFFBEB)) // Clean English Notebook Paper
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
                        .pointerInput(currentIndex, activeGameMode, strokeSuccess, isDemoPlaying, rainbowPass) {
                            if (!strokeSuccess && !isDemoPlaying) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        userDrawnPoints = listOf(Pair(offset.x, offset.y))
                                        isFailedAttempt = false

                                        // Connect the dots interactive touch check
                                        if (activeGameMode == "dots") {
                                            val canvasSize = size.width.toFloat()
                                            if (connectedDotCount < dotCheckpoints.size) {
                                                val targetPt = dotCheckpoints[connectedDotCount]
                                                val tx = targetPt.first * canvasSize
                                                val ty = targetPt.second * canvasSize
                                                if (hypot(offset.x - tx, offset.y - ty) <= canvasSize * 0.18f) {
                                                    connectedDotCount++
                                                    audioEngine.speak("${connectedDotCount}!")
                                                    if (connectedDotCount >= dotCheckpoints.size) {
                                                        strokeSuccess = true
                                                        showConfetti = true
                                                        repository.addStars(4)
                                                        userStars = repository.getStars()
                                                        audioEngine.speak("Connected all dots! Great job!")
                                                        showRewardDialog = true
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        val newPt = Pair(change.position.x, change.position.y)
                                        userDrawnPoints = userDrawnPoints + newPt

                                        // Connect the dots drag progress check
                                        if (activeGameMode == "dots" && connectedDotCount < dotCheckpoints.size) {
                                            val canvasSize = size.width.toFloat()
                                            val targetPt = dotCheckpoints[connectedDotCount]
                                            val tx = targetPt.first * canvasSize
                                            val ty = targetPt.second * canvasSize
                                            if (hypot(change.position.x - tx, change.position.y - ty) <= canvasSize * 0.18f) {
                                                connectedDotCount++
                                                audioEngine.speak("${connectedDotCount}!")
                                                if (connectedDotCount >= dotCheckpoints.size) {
                                                    strokeSuccess = true
                                                    showConfetti = true
                                                    repository.addStars(4)
                                                    userStars = repository.getStars()
                                                    audioEngine.speak("Connected all dots! Great job!")
                                                    showRewardDialog = true
                                                }
                                            }
                                        }

                                        // Pencil Control Road boundary check
                                        if (activeGameMode == "pencil_control") {
                                            val canvasSize = size.width.toFloat()
                                            val allRoadPts = guideStrokes.flatten()
                                            val onRoad = allRoadPts.any { rPt ->
                                                hypot(change.position.x - rPt.first * canvasSize, change.position.y - rPt.second * canvasSize) <= canvasSize * 0.22f
                                            }
                                            if (!onRoad) {
                                                isFailedAttempt = true
                                                isShaking = true
                                                userDrawnPoints = emptyList()
                                                feedbackMessage = "❌ Oops! Stay on the road! Try again!"
                                                audioEngine.speakTryAgain()
                                            } else {
                                                // Check finish line arrival
                                                val finishPt = allRoadPts.lastOrNull()
                                                if (finishPt != null && hypot(change.position.x - finishPt.first * canvasSize, change.position.y - finishPt.second * canvasSize) <= canvasSize * 0.15f) {
                                                    strokeSuccess = true
                                                    showConfetti = true
                                                    repository.addStars(3)
                                                    userStars = repository.getStars()
                                                    audioEngine.speak("Vroom! You reached the finish line! 🚗🏁")
                                                    showRewardDialog = true
                                                }
                                            }
                                        }
                                    },
                                    onDragEnd = {
                                        if (activeGameMode == "dots" || activeGameMode == "pencil_control") return@detectDragGestures

                                        val canvasSize = size.width.toFloat()
                                        val validation = HandwritingData.validateHandwritingTracing(
                                            drawnPoints = userDrawnPoints,
                                            strokes = guideStrokes,
                                            canvasSize = canvasSize
                                        )

                                        if (validation.isValid) {
                                            if (activeGameMode == "rainbow") {
                                                if (rainbowPass < 4) {
                                                    rainbowPass++
                                                    userDrawnPoints = emptyList()
                                                    audioEngine.speak("Rainbow Pass ${rainbowPass + 1}!")
                                                    feedbackMessage = "Pass ${rainbowPass + 1}/5 - Next Color! 🌈"
                                                } else {
                                                    strokeSuccess = true
                                                    isFailedAttempt = false
                                                    feedbackMessage = "🌈 Rainbow Master! 5 Passes Completed! +5 Stars"
                                                    showConfetti = true
                                                    repository.addStars(5)
                                                    userStars = repository.getStars()
                                                    audioEngine.speak("Rainbow Master! Excellent!")
                                                    showRewardDialog = true
                                                }
                                            } else {
                                                strokeSuccess = true
                                                isFailedAttempt = false
                                                feedbackMessage = "⭐ Excellent handwriting! Perfect stroke! +3 Stars"
                                                showConfetti = true
                                                repository.addStars(3)
                                                userStars = repository.getStars()
                                                audioEngine.speak("Superb! You wrote ${currentItem.character}!")
                                                showRewardDialog = true
                                            }
                                        } else {
                                            isFailedAttempt = true
                                            isShaking = true
                                            feedbackMessage = "❌ ${validation.message}"
                                            audioEngine.speakTryAgain()
                                            // Replay stroke animation to show child correct stroke path
                                            isDemoPlaying = true
                                        }
                                    }
                                )
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    // 1. English Copybook 4-Line Notebook Ruling
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

                    // 2. Guide Strokes & Dotted Paths (Hidden in "copy_memory" after demo)
                    val showGuideLines = activeGameMode != "copy_memory" || isDemoPlaying || strokeSuccess

                    if (showGuideLines) {
                        guideStrokes.forEach { strokeList ->
                            if (strokeList.size > 1) {
                                val trackPath = Path()
                                trackPath.moveTo(strokeList[0].first * w, strokeList[0].second * h)
                                for (i in 1 until strokeList.size) {
                                    trackPath.lineTo(strokeList[i].first * w, strokeList[i].second * h)
                                }

                                if (strokeSuccess) {
                                    drawPath(
                                        path = trackPath,
                                        color = Color(0xFF16A34A),
                                        style = Stroke(width = 36f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                    )
                                } else if (activeGameMode == "pencil_control") {
                                    // Road Canvas styling
                                    drawPath(
                                        path = trackPath,
                                        color = Color(0xFFE2E8F0),
                                        style = Stroke(width = 110f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                    )
                                    drawPath(
                                        path = trackPath,
                                        color = Color(0xFF94A3B8),
                                        style = Stroke(
                                            width = 6f,
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Round,
                                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 14f), 0f)
                                        )
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
                    }

                    // 3. Connect-The-Dots UI rendering
                    if (activeGameMode == "dots") {
                        dotCheckpoints.forEachIndexed { idx, pt ->
                            val cx = pt.first * w
                            val cy = pt.second * h
                            val isReached = idx < connectedDotCount
                            drawCircle(
                                color = if (isReached) Color(0xFF16A34A) else Color(0xFFD97706),
                                radius = 22f,
                                center = Offset(cx, cy)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 18f,
                                center = Offset(cx, cy)
                            )
                        }
                    }

                    // 4. Animated Coach Stroke Demo
                    if (isDemoPlaying) {
                        val allPoints = guideStrokes.flatten()
                        if (allPoints.isNotEmpty()) {
                            val totalP = allPoints.size
                            val currentPIdx = ((totalP - 1) * demoProgress).toInt().coerceIn(0, totalP - 1)
                            val demoX = allPoints[currentPIdx].first * w
                            val demoY = allPoints[currentPIdx].second * h

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

                            // Animated Pencil Cursor
                            drawCircle(color = Color(0xFFDC2626), radius = 18f, center = Offset(demoX, demoY))
                            drawCircle(color = Color.White, radius = 7f, center = Offset(demoX, demoY))
                        }
                    }

                    // 5. User Drawn Path Rendering
                    if (userDrawnPoints.size > 1 && !isDemoPlaying) {
                        val userPath = Path()
                        userPath.moveTo(userDrawnPoints[0].first, userDrawnPoints[0].second)
                        for (i in 1 until userDrawnPoints.size) {
                            userPath.lineTo(userDrawnPoints[i].first, userDrawnPoints[i].second)
                        }

                        val activeColor = when (activeGameMode) {
                            "rainbow" -> rainbowColors[rainbowPass % rainbowColors.size]
                            else -> when {
                                strokeSuccess -> Color(0xFF16A34A)
                                isFailedAttempt -> Color(0xFFDC2626)
                                else -> Color(0xFF2563EB)
                            }
                        }

                        drawPath(
                            path = userPath,
                            color = activeColor,
                            style = Stroke(width = 28f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action Control Buttons: Watch Demo & Clear Canvas
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
                    Text("Watch Demo 🎬", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        userDrawnPoints = emptyList()
                        strokeSuccess = false
                        isFailedAttempt = false
                        isDemoPlaying = false
                        connectedDotCount = 0
                        rainbowPass = 0
                        feedbackMessage = "Trace over the lines! ✍️"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Clear", tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear Canvas", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

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
                speechBubbleText = if (strokeSuccess) "Super writing!" else "Practice makes perfect!",
                onClick = {
                    audioEngine.speak("Let me see your neat writing!")
                }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Star Writer! ✍️",
            message = "You mastered ${currentItem.displayTitle} on the workbook lines!",
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
