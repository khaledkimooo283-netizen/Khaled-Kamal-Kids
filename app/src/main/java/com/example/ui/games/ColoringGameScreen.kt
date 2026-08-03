package com.example.ui.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
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
    // Categories: "letters", "numbers", "animals", "fruits", "vehicles", "shapes", "vocab"
    // Color The Letter mode: Pick colors to fill and outline the target letter & numbers
    var categoryMode by remember { mutableStateOf("letters") }
    var userStars by remember { mutableIntStateOf(repository.getStars()) }

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
            ColorOption("Indigo", Color(0xFF6366F1)),
            ColorOption("Gold", Color(0xFFF59E0B))
        )
    }

    var selectedColor by remember { mutableStateOf(palette[0].color) }
    var activeTool by remember { mutableStateOf("brush") } // "brush", "fill", "eraser"
    var brushSizeDp by remember { mutableFloatStateOf(32f) } // 16f, 32f, 56f

    // Sample Coloring Pages Data
    val categoryItems: List<TracingGuideItem> = remember(categoryMode) {
        when (categoryMode) {
            "numbers" -> HandwritingData.numbers
            "animals" -> listOf(
                TracingGuideItem("c_lion", "Lion", "🦁", "animals", "Lion", "🦁", HandwritingData.uppercaseLetters[11].strokeGuidePoints),
                TracingGuideItem("c_elephant", "Elephant", "🐘", "animals", "Elephant", "🐘", HandwritingData.uppercaseLetters[4].strokeGuidePoints),
                TracingGuideItem("c_cat", "Cat", "🐱", "animals", "Cat", "🐱", HandwritingData.uppercaseLetters[2].strokeGuidePoints),
                TracingGuideItem("c_dog", "Dog", "🐶", "animals", "Dog", "🐶", HandwritingData.uppercaseLetters[3].strokeGuidePoints),
                TracingGuideItem("c_panda", "Panda", "🐼", "animals", "Panda", "🐼", HandwritingData.uppercaseLetters[15].strokeGuidePoints)
            )
            "fruits" -> listOf(
                TracingGuideItem("c_apple", "Apple", "🍎", "fruits", "Apple", "🍎", HandwritingData.uppercaseLetters[0].strokeGuidePoints),
                TracingGuideItem("c_banana", "Banana", "🍌", "fruits", "Banana", "🍌", HandwritingData.uppercaseLetters[1].strokeGuidePoints),
                TracingGuideItem("c_strawberry", "Strawberry", "🍓", "fruits", "Strawberry", "🍓", HandwritingData.uppercaseLetters[18].strokeGuidePoints),
                TracingGuideItem("c_orange", "Orange", "🍊", "fruits", "Orange", "🍊", HandwritingData.uppercaseLetters[14].strokeGuidePoints)
            )
            "vehicles" -> listOf(
                TracingGuideItem("c_car", "Car", "🚗", "vehicles", "Car", "🚗", HandwritingData.uppercaseLetters[2].strokeGuidePoints),
                TracingGuideItem("c_rocket", "Rocket", "🚀", "vehicles", "Rocket", "🚀", HandwritingData.uppercaseLetters[17].strokeGuidePoints),
                TracingGuideItem("c_bus", "Bus", "🚌", "vehicles", "Bus", "🚌", HandwritingData.uppercaseLetters[1].strokeGuidePoints),
                TracingGuideItem("c_train", "Train", "🚂", "vehicles", "Train", "🚂", HandwritingData.uppercaseLetters[19].strokeGuidePoints)
            )
            "shapes" -> HandwritingData.shapePrepItems
            "vocab" -> listOf(
                TracingGuideItem("c_sun", "Sun", "☀️", "vocab", "Sun", "☀️", HandwritingData.uppercaseLetters[18].strokeGuidePoints),
                TracingGuideItem("c_cake", "Cake", "🎂", "vocab", "Cake", "🎂", HandwritingData.uppercaseLetters[2].strokeGuidePoints),
                TracingGuideItem("c_crown", "Crown", "👑", "vocab", "Crown", "👑", HandwritingData.uppercaseLetters[2].strokeGuidePoints)
            )
            else -> HandwritingData.uppercaseLetters
        }
    }

    var itemIndex by remember { mutableIntStateOf(0) }
    val currentItem = categoryItems.getOrNull(itemIndex) ?: categoryItems.first()

    // Canvas drawing state
    var userStrokes by remember { mutableStateOf(listOf<PaintStroke>()) }
    var bucketFillColor by remember { mutableStateOf<Color?>(null) }

    var isFinished by remember { mutableStateOf(false) }
    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    // Coloring Studio Sub-Activities:
    // "trace_color" -> Trace then Color
    // "connect_dots" -> Connect the dots to build letter
    // "follow_arrows" -> Follow arrow path
    // "paint_inside" -> Paint inside letter shape
    // "finish_missing" -> Finish missing part of letter
    var studioActivity by remember { mutableStateOf("trace_color") }
    var isLetterTraced by remember { mutableStateOf(false) }
    var connectedDotsCount by remember { mutableIntStateOf(0) }

    var isDemoPlaying by remember { mutableStateOf(false) }
    var demoProgress by remember { mutableFloatStateOf(0f) }

    // Coach Demo animation
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
        }
    }

    // Speak item & auto demo on load
    LaunchedEffect(currentItem, studioActivity) {
        userStrokes = emptyList()
        bucketFillColor = null
        isFinished = false
        isLetterTraced = false
        connectedDotsCount = 0
        isDemoPlaying = true

        val activityName = when (studioActivity) {
            "trace_color" -> "Trace then Color ${currentItem.displayTitle}!"
            "connect_dots" -> "Connect the dots for ${currentItem.displayTitle}!"
            "follow_arrows" -> "Follow arrows for ${currentItem.displayTitle}!"
            "paint_inside" -> "Paint inside ${currentItem.displayTitle}!"
            "finish_missing" -> "Finish the missing part of ${currentItem.displayTitle}!"
            else -> "Color ${currentItem.displayTitle}!"
        }
        audioEngine.speak(activityName)
    }

    fun triggerCompletion() {
        if (isFinished) return

        val allUserPoints = userStrokes.flatMap { it.points }.map { Pair(it.x, it.y) }
        
        if (allUserPoints.size < 10 && bucketFillColor == null && !isLetterTraced) {
            audioEngine.speak("Please draw or trace ${currentItem.displayTitle} first!")
            return
        }

        // Validate drawing quality
        if (allUserPoints.size >= 10) {
            val validation = HandwritingData.validateHandwritingTracing(
                drawnPoints = allUserPoints,
                strokes = currentItem.strokeGuidePoints,
                canvasSize = 270f
            )

            if (!validation.isValid && studioActivity != "paint_inside") {
                audioEngine.playWrongSound()
                audioEngine.speak("Try Again! Follow the letter lines to color neatly. ❌")
                isDemoPlaying = true
                return
            }
        }

        isFinished = true
        showConfetti = true
        repository.addStars(5)
        repository.rewardColoring()
        repository.rewardFinishGame()
        userStars = repository.getStars()

        audioEngine.speak("Great job with ${currentItem.displayTitle}! +5 Stars!")
        audioEngine.speakPraise()
        showRewardDialog = true
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
                title = "Coloring Studio 🎨",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Category Selector Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val cats = listOf(
                    "letters" to "🔤 Letters",
                    "numbers" to "🔟 Numbers",
                    "animals" to "🦁 Animals",
                    "fruits" to "🍎 Fruits",
                    "vehicles" to "🚀 Vehicles",
                    "shapes" to "⭐ Shapes",
                    "vocab" to "☀️ Words"
                )
                itemsIndexed(cats) { _, (cKey, label) ->
                    val isSelected = categoryMode == cKey
                    Button(
                        onClick = {
                            categoryMode = cKey
                            itemIndex = 0
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFFF59E0B) else Color(0xFFFEF3C7)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 10.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else Color(0xFF92400E),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Sub-Activity Row (Trace then Color, Connect Dots, Follow Arrows, Paint Inside, Finish Missing Part)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val activities = listOf(
                    "trace_color" to "✏️ Trace then Color",
                    "connect_dots" to "🔢 Connect Dots",
                    "follow_arrows" to "🏹 Follow Arrows",
                    "paint_inside" to "🎨 Paint Inside",
                    "finish_missing" to "🧩 Finish Missing Part"
                )
                itemsIndexed(activities) { _, (aKey, label) ->
                    val isSelected = studioActivity == aKey
                    FilterChip(
                        selected = isSelected,
                        onClick = { studioActivity = aKey },
                        label = {
                            Text(
                                label,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = if (isSelected) Color.White else Color(0xFF78350F)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFD97706),
                            containerColor = Color.White
                        )
                    )
                }
            }

            // Quick Item Switcher Carousel
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsIndexed(categoryItems) { idx, item ->
                    val isSelected = idx == itemIndex
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color(0xFFF59E0B) else Color.White)
                            .clickable { itemIndex = idx }
                            .border(1.5.dp, if (isSelected) Color(0xFFD97706) else Color(0xFFFCD34D), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.character.take(2),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = if (isSelected) Color.White else Color(0xFF92400E)
                        )
                    }
                }
            }

            // Display Title Banner
            Text(
                text = "Coloring Page: ${currentItem.displayTitle} ${currentItem.emoji}",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFB45309),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Main Coloring Canvas
            val density = LocalDensity.current
            val strokeWidthPx = with(density) { brushSizeDp.dp.toPx() }

            Box(
                modifier = Modifier
                    .size(270.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(bucketFillColor ?: Color.White)
                    .border(4.dp, Color(0xFFF59E0B), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                var currentStrokePoints by remember { mutableStateOf(listOf<Offset>()) }

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(selectedColor, activeTool, strokeWidthPx) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    if (activeTool == "fill") {
                                        bucketFillColor = selectedColor
                                        audioEngine.speak("Filled with color!")
                                    } else {
                                        currentStrokePoints = listOf(offset)
                                    }
                                },
                                onDrag = { change, _ ->
                                    if (activeTool != "fill") {
                                        change.consume()
                                        currentStrokePoints = currentStrokePoints + change.position
                                    }
                                },
                                onDragEnd = {
                                    if (currentStrokePoints.isNotEmpty() && activeTool != "fill") {
                                        val drawColor = if (activeTool == "eraser") (bucketFillColor ?: Color.White) else selectedColor
                                        userStrokes = userStrokes + PaintStroke(
                                            points = currentStrokePoints,
                                            color = drawColor,
                                            strokeWidth = strokeWidthPx
                                        )
                                        currentStrokePoints = emptyList()
                                    }
                                }
                            )
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    // 1. Draw user background paint strokes
                    userStrokes.forEach { stroke ->
                        if (stroke.points.size > 1) {
                            val path = Path()
                            path.moveTo(stroke.points[0].x, stroke.points[0].y)
                            for (i in 1 until stroke.points.size) {
                                path.lineTo(stroke.points[i].x, stroke.points[i].y)
                            }
                            drawPath(
                                path = path,
                                color = stroke.color,
                                style = Stroke(width = stroke.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                        }
                    }

                    // 2. Draw active live drag stroke
                    if (currentStrokePoints.size > 1) {
                        val path = Path()
                        path.moveTo(currentStrokePoints[0].x, currentStrokePoints[0].y)
                        for (i in 1 until currentStrokePoints.size) {
                            path.lineTo(currentStrokePoints[i].x, currentStrokePoints[i].y)
                        }
                        val drawColor = if (activeTool == "eraser") (bucketFillColor ?: Color.White) else selectedColor
                        drawPath(
                            path = path,
                            color = drawColor,
                            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }

                    // 3. Draw Thick Vector Outline of Item
                    currentItem.strokeGuidePoints.forEach { strokeList ->
                        if (strokeList.size > 1) {
                            val guidePath = Path()
                            guidePath.moveTo(strokeList[0].first * w, strokeList[0].second * h)
                            for (i in 1 until strokeList.size) {
                                guidePath.lineTo(strokeList[i].first * w, strokeList[i].second * h)
                            }
                            drawPath(
                                path = guidePath,
                                color = Color(0xFF0F172A),
                                style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Tool Bar: Brush, Bucket Fill, Eraser, Undo, Clear, Save
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brush tool
                IconButton(
                    onClick = { activeTool = "brush" },
                    modifier = Modifier
                        .size(36.dp)
                        .background(if (activeTool == "brush") Color(0xFFF59E0B) else Color.White, CircleShape)
                        .border(1.5.dp, Color(0xFFD97706), CircleShape)
                ) {
                    Icon(Icons.Filled.Brush, contentDescription = "Brush", tint = if (activeTool == "brush") Color.White else Color(0xFF92400E))
                }

                // Bucket fill tool
                IconButton(
                    onClick = { activeTool = "fill" },
                    modifier = Modifier
                        .size(36.dp)
                        .background(if (activeTool == "fill") Color(0xFFF59E0B) else Color.White, CircleShape)
                        .border(1.5.dp, Color(0xFFD97706), CircleShape)
                ) {
                    Icon(Icons.Filled.FormatColorFill, contentDescription = "Fill", tint = if (activeTool == "fill") Color.White else Color(0xFF92400E))
                }

                // Eraser tool
                IconButton(
                    onClick = { activeTool = "eraser" },
                    modifier = Modifier
                        .size(36.dp)
                        .background(if (activeTool == "eraser") Color(0xFFDC2626) else Color.White, CircleShape)
                        .border(1.5.dp, Color(0xFFDC2626), CircleShape)
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = "Eraser", tint = if (activeTool == "eraser") Color.White else Color(0xFFDC2626))
                }

                // Undo tool
                IconButton(
                    onClick = {
                        if (userStrokes.isNotEmpty()) {
                            userStrokes = userStrokes.dropLast(1)
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White, CircleShape)
                        .border(1.5.dp, Color(0xFFD97706), CircleShape)
                ) {
                    Icon(Icons.Filled.Undo, contentDescription = "Undo", tint = Color(0xFF92400E))
                }

                // Clear canvas tool
                IconButton(
                    onClick = {
                        userStrokes = emptyList()
                        bucketFillColor = null
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White, CircleShape)
                        .border(1.5.dp, Color(0xFFDC2626), CircleShape)
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Clear", tint = Color(0xFFDC2626))
                }

                // Finish & Save tool
                Button(
                    onClick = { triggerCompletion() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Filled.Save, contentDescription = "Save", tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Done! ✨", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Color Palette Picker
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(palette) { opt ->
                    val isSelected = selectedColor == opt.color
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(opt.color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color(0xFF0F172A) else Color.LightGray,
                                shape = CircleShape
                            )
                            .clickable {
                                selectedColor = opt.color
                                if (activeTool == "eraser") activeTool = "brush"
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Artist Extraordinaire! 🎨",
            message = "You painted a masterpiece for ${currentItem.displayTitle}!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                val max = categoryItems.size
                itemIndex = (itemIndex + 1) % max
            },
            onHome = onBackClick
        )
    }
}
