package com.example.ui.games

import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.HandwritingData
import com.example.data.KkDataRepository
import com.example.data.TracingGuideItem
import com.example.ui.components.*

data class PaintStroke(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

data class ColorOption(
    val name: String,
    val color: Color
)

@Composable
fun ColoringGameScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    // Mode: "uppercase" (A-Z), "lowercase" (a-z), "all" (A-Z + a-z)
    var mode by remember { mutableStateOf("uppercase") }
    var userStars by remember { mutableIntStateOf(repository.getStars()) }

    // 13 Rich Palette Colors
    val palette = remember {
        listOf(
            ColorOption("Red", Color(0xFFEF4444)),
            ColorOption("Orange", Color(0xFFF97316)),
            ColorOption("Yellow", Color(0xFFEAB308)),
            ColorOption("Green", Color(0xFF22C55E)),
            ColorOption("Lime", Color(0xFF84CC16)),
            ColorOption("Blue", Color(0xFF3B82F6)),
            ColorOption("Cyan", Color(0xFF06B6D4)),
            ColorOption("Purple", Color(0xFFA855F7)),
            ColorOption("Pink", Color(0xFFEC4899)),
            ColorOption("Brown", Color(0xFF78350F)),
            ColorOption("Black", Color(0xFF0F172A)),
            ColorOption("White", Color(0xFFFFFFFF)),
            ColorOption("Indigo", Color(0xFF6366F1))
        )
    }

    var selectedColor by remember { mutableStateOf(palette[0].color) }
    var activeTool by remember { mutableStateOf("brush") } // "brush", "fill", "eraser"
    var brushSizeDp by remember { mutableFloatStateOf(36f) } // 20f, 36f, 56f

    // Shuffled queue management (Never repeat too soon)
    val getSourceItems: () -> List<TracingGuideItem> = {
        when (mode) {
            "uppercase" -> HandwritingData.uppercaseLetters
            "lowercase" -> HandwritingData.lowercaseLetters
            else -> HandwritingData.uppercaseLetters + HandwritingData.lowercaseLetters
        }
    }

    var shuffledQueue by remember(mode) { mutableStateOf(getSourceItems().shuffled()) }
    var queueIndex by remember { mutableIntStateOf(0) }

    val currentItem = remember(shuffledQueue, queueIndex) {
        if (shuffledQueue.isNotEmpty()) shuffledQueue[queueIndex % shuffledQueue.size]
        else HandwritingData.uppercaseLetters[0]
    }

    // Canvas drawing state
    var userStrokes by remember { mutableStateOf(listOf<PaintStroke>()) }
    var bucketFillColor by remember { mutableStateOf<Color?>(null) }
    var paintedAreaRatio by remember { mutableFloatStateOf(0f) }

    var isFinished by remember { mutableStateOf(false) }
    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    // Speak letter when displayed
    LaunchedEffect(currentItem) {
        userStrokes = emptyList()
        bucketFillColor = null
        paintedAreaRatio = 0f
        isFinished = false

        val letterName = currentItem.character
        if (currentItem.category == "lowercase") {
            audioEngine.speak("This is lowercase $letterName for ${currentItem.word}! 🎨")
        } else {
            audioEngine.speak("This is capital letter $letterName for ${currentItem.word}! 🎨")
        }
    }

    fun pickNextRandomLetter() {
        val size = shuffledQueue.size
        if (size > 1) {
            var nextIdx = (queueIndex + 1) % size
            if (nextIdx == 0) {
                // Reshuffle queue ensuring first item is different from last
                val lastItem = currentItem
                var newQueue = getSourceItems().shuffled()
                if (newQueue.first().id == lastItem.id && newQueue.size > 1) {
                    newQueue = newQueue.drop(1) + newQueue.first()
                }
                shuffledQueue = newQueue
                queueIndex = 0
            } else {
                queueIndex = nextIdx
            }
        }
    }

    fun triggerCompletion() {
        if (!isFinished) {
            isFinished = true
            showConfetti = true
            repository.addStars(5)
            userStars = repository.getStars()

            audioEngine.speak("Great job! Letter ${currentItem.character}!")
            audioEngine.speakPraise()
            showRewardDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8E1))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Letter Coloring Book 🎨",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Mode Selector & Randomizer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    "uppercase" to "ABC Capital",
                    "lowercase" to "abc Small",
                    "all" to "All 52"
                ).forEach { (mKey, label) ->
                    val isSelected = mode == mKey
                    Button(
                        onClick = {
                            mode = mKey
                            queueIndex = 0
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFFF59E0B) else Color(0xFFFEF3C7)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else Color(0xFF92400E),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Random Next Button 🎲
                IconButton(
                    onClick = {
                        pickNextRandomLetter()
                        audioEngine.speak("Random letter!")
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFF0284C7), CircleShape)
                ) {
                    Icon(Icons.Filled.Shuffle, contentDescription = "Random Letter", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Display Title Banner
            Text(
                text = "Color Letter '${currentItem.character}' for ${currentItem.word} ${currentItem.emoji}",
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFB45309),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Vector Coloring Canvas with Masking & Light Guide
            val density = LocalDensity.current
            val strokeWidthPx = with(density) { brushSizeDp.dp.toPx() }

            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White)
                    .border(4.dp, Color(0xFFF59E0B), RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(currentItem, activeTool, selectedColor, brushSizeDp) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    if (activeTool == "fill") {
                                        bucketFillColor = selectedColor
                                        triggerCompletion()
                                    } else {
                                        val strokeColor = if (activeTool == "eraser") Color(0xFFF1F5F9) else selectedColor
                                        userStrokes = userStrokes + PaintStroke(
                                            points = listOf(offset),
                                            color = strokeColor,
                                            strokeWidth = strokeWidthPx
                                        )
                                    }
                                },
                                onDrag = { change, _ ->
                                    if (activeTool != "fill") {
                                        change.consume()
                                        val lastStroke = userStrokes.lastOrNull()
                                        if (lastStroke != null) {
                                            val updatedPoints = lastStroke.points + change.position
                                            userStrokes = userStrokes.dropLast(1) + lastStroke.copy(points = updatedPoints)
                                            paintedAreaRatio = (userStrokes.size * 0.1f).coerceAtMost(1f)
                                        }
                                    }
                                },
                                onDragEnd = {
                                    if (userStrokes.size >= 6 || bucketFillColor != null) {
                                        triggerCompletion()
                                    }
                                }
                            )
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    // 1. Build Raw Center Guideline Path
                    val rawComposePath = Path()
                    currentItem.strokeGuidePoints.forEach { strokeList ->
                        if (strokeList.isNotEmpty()) {
                            rawComposePath.moveTo(strokeList[0].first * w, strokeList[0].second * h)
                            for (i in 1 until strokeList.size) {
                                rawComposePath.lineTo(strokeList[i].first * w, strokeList[i].second * h)
                            }
                        }
                    }

                    // 2. Generate 2D Vector Letter Mask Outline using Android Paint.getFillPath
                    val nativeStrokePaint = AndroidPaint().apply {
                        style = AndroidPaint.Style.STROKE
                        strokeWidth = 56.dp.toPx()
                        strokeCap = AndroidPaint.Cap.ROUND
                        strokeJoin = AndroidPaint.Join.ROUND
                    }

                    val nativeMaskPath = AndroidPath()
                    nativeStrokePaint.getFillPath(rawComposePath.asAndroidPath(), nativeMaskPath)
                    val maskComposePath = nativeMaskPath.asComposePath()

                    // 3. Strict Masking Clip inside Letter Outline
                    clipPath(maskComposePath) {
                        // A. Light Gray Guide Interior
                        drawRect(color = Color(0xFFF1F5F9))

                        // B. Bucket Fill Layer
                        bucketFillColor?.let { fColor ->
                            drawRect(color = fColor)
                        }

                        // C. User Painted Brush Strokes
                        userStrokes.forEach { stroke ->
                            if (stroke.points.size > 1) {
                                val strokePath = Path()
                                strokePath.moveTo(stroke.points[0].x, stroke.points[0].y)
                                for (i in 1 until stroke.points.size) {
                                    strokePath.lineTo(stroke.points[i].x, stroke.points[i].y)
                                }
                                drawPath(
                                    path = strokePath,
                                    color = stroke.color,
                                    style = Stroke(
                                        width = stroke.strokeWidth,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            } else if (stroke.points.isNotEmpty()) {
                                drawCircle(
                                    color = stroke.color,
                                    radius = stroke.strokeWidth / 2f,
                                    center = stroke.points[0]
                                )
                            }
                        }
                    }

                    // 4. Thick Crisp Black Outer Letter Contour
                    drawPath(
                        path = maskComposePath,
                        color = Color(0xFF0F172A),
                        style = Stroke(width = 12f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )

                    // 5. Light Center Dashed Line Detail
                    drawPath(
                        path = rawComposePath,
                        color = Color(0x22000000),
                        style = Stroke(
                            width = 6f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tools Selector Row (Brush 🖌️, Bucket 🪣, Eraser 🧽)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brush
                FilterChip(
                    selected = activeTool == "brush",
                    onClick = { activeTool = "brush" },
                    label = { Text("Brush 🖌️") },
                    leadingIcon = { Icon(Icons.Filled.Brush, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFF59E0B),
                        selectedLabelColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Bucket Fill
                FilterChip(
                    selected = activeTool == "fill",
                    onClick = { activeTool = "fill" },
                    label = { Text("Fill 🪣") },
                    leadingIcon = { Icon(Icons.Filled.FormatColorFill, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0284C7),
                        selectedLabelColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Eraser
                FilterChip(
                    selected = activeTool == "eraser",
                    onClick = { activeTool = "eraser" },
                    label = { Text("Eraser 🧽") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF64748B),
                        selectedLabelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Brush Size Selector
            if (activeTool == "brush") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(20f to "S", 36f to "M", 56f to "L").forEach { (sizeVal, label) ->
                        val isSelected = brushSizeDp == sizeVal
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFFF59E0B) else Color(0xFFE2E8F0))
                                .clickable { brushSizeDp = sizeVal },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color.Black
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Scrollable Palette Swatches
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(palette) { item ->
                    val isSelected = selectedColor == item.color && activeTool != "eraser"
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(item.color)
                            .border(
                                width = if (isSelected) 4.dp else 1.5.dp,
                                color = if (isSelected) Color.Black else Color.White,
                                shape = CircleShape
                            )
                            .clickable {
                                selectedColor = item.color
                                if (activeTool == "eraser") activeTool = "brush"
                                audioEngine.speak("${item.name} color!")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Action Bar (Clear & Finish)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = {
                        userStrokes = emptyList()
                        bucketFillColor = null
                        paintedAreaRatio = 0f
                        isFinished = false
                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear")
                }

                Button(
                    onClick = { triggerCompletion() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = "Finish", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Finish ✨", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = if (isFinished) "Beautiful artwork!" else "Paint inside letter ${currentItem.character}! 🎨",
                onClick = {
                    audioEngine.speak("Color letter ${currentItem.character} with your favorite colors!")
                }
            )
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Master Artist! 🎨",
            message = "You colored letter '${currentItem.character}' beautifully!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                pickNextRandomLetter()
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}
