package com.example.ui.games

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.ui.components.*

enum class ColoringCategory(val title: String, val emoji: String) {
    CAPITALS("Capital Letters", "🔤"),
    SMALLS("Small Letters", "🔡"),
    NUMBERS("Numbers", "🔢"),
    WORDS("Words", "🍎")
}

enum class ColoringTool(
    val label: String,
    val emoji: String,
    val strokeWidthDp: Float,
    val isFill: Boolean = false,
    val isEraser: Boolean = false
) {
    PAINT_BRUSH("Paint Brush", "🖌️", 24f),
    THICK_BRUSH("Thick Brush", "🖍️", 48f),
    PENCIL("Pencil", "✏️", 8f),
    FILL_BUCKET("Fill Bucket", "🪣", 0f, isFill = true),
    ERASER("Eraser", "🧽", 36f, isEraser = true)
}

data class ColoringItem(
    val category: ColoringCategory,
    val text: String,
    val hint: String,
    val emoji: String,
    val themeColor: Color
)

data class DrawnStroke(
    val path: Path,
    val color: Color,
    val strokeWidth: Float,
    val isBucketFill: Boolean = false,
    val isEraser: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorByNumberGameScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var selectedCategory by remember { mutableStateOf(ColoringCategory.CAPITALS) }
    var itemIndex by remember { mutableIntStateOf(0) }

    // Color Palette
    val colorPalette = remember {
        listOf(
            Color(0xFFEF4444), // Red
            Color(0xFFF97316), // Orange
            Color(0xFFEAB308), // Yellow
            Color(0xFF22C55E), // Green
            Color(0xFF06B6D4), // Cyan
            Color(0xFF3B82F6), // Blue
            Color(0xFFA855F7), // Purple
            Color(0xFFEC4899), // Pink
            Color(0xFF8B4513), // Brown
            Color(0xFF1E293B), // Black
            Color(0xFFFFFFFF), // White
            Color(0xFF94A3B8)  // Gray
        )
    }

    var activeColor by remember { mutableStateOf(colorPalette[0]) }
    var activeTool by remember { mutableStateOf(ColoringTool.PAINT_BRUSH) }

    // Capital Letters (A-Z)
    val capitalsList = remember {
        ('A'..'Z').map { char ->
            ColoringItem(
                category = ColoringCategory.CAPITALS,
                text = char.toString(),
                hint = "Capital Letter $char",
                emoji = "🔤",
                themeColor = Color(0xFFEF4444)
            )
        }
    }

    // Small Letters (a-z)
    val smallsList = remember {
        ('a'..'z').map { char ->
            ColoringItem(
                category = ColoringCategory.SMALLS,
                text = char.toString(),
                hint = "Small Letter $char",
                emoji = "🔡",
                themeColor = Color(0xFF3B82F6)
            )
        }
    }

    // Numbers (0-20)
    val numbersList = remember {
        (0..20).map { num ->
            ColoringItem(
                category = ColoringCategory.NUMBERS,
                text = num.toString(),
                hint = "Number $num",
                emoji = "🔢",
                themeColor = Color(0xFFEAB308)
            )
        }
    }

    // Simple English Words
    val wordsList = remember {
        listOf(
            "Apple" to "🍎", "Cat" to "🐱", "Dog" to "🐶", "Book" to "📚",
            "Ball" to "⚽", "Sun" to "☀️", "Moon" to "🌙", "Tree" to "🌳",
            "Fish" to "🐟", "Car" to "🚗", "Elephant" to "🐘", "Milk" to "🥛",
            "Lion" to "🦁", "Star" to "⭐", "Rain" to "🌧️"
        ).map { (word, emoji) ->
            ColoringItem(
                category = ColoringCategory.WORDS,
                text = word,
                hint = "$word $emoji",
                emoji = emoji,
                themeColor = Color(0xFFA855F7)
            )
        }
    }

    val currentItems = remember(selectedCategory) {
        when (selectedCategory) {
            ColoringCategory.CAPITALS -> capitalsList
            ColoringCategory.SMALLS -> smallsList
            ColoringCategory.NUMBERS -> numbersList
            ColoringCategory.WORDS -> wordsList
        }
    }

    val currentItem = remember(selectedCategory, itemIndex) {
        currentItems.getOrElse(itemIndex % currentItems.size) { currentItems.first() }
    }

    // Drawing State stacks
    val drawnStrokes = remember { mutableStateListOf<DrawnStroke>() }
    val redoStrokes = remember { mutableStateListOf<DrawnStroke>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    // Reset drawn strokes when current item changes
    LaunchedEffect(currentItem) {
        drawnStrokes.clear()
        redoStrokes.clear()
        currentPath = null
        audioEngine.speak("Color ${currentItem.hint}")
    }

    fun finishAndCelebrate() {
        val praiseMessages = listOf(
            "Excellent!",
            "Great Job!",
            "You are amazing!",
            "Wonderful artwork!"
        )
        val praise = praiseMessages.random()
        audioEngine.playCorrectSound()
        audioEngine.speak("$praise You colored ${currentItem.text} beautifully!")

        repository.addStars(5)
        repository.rewardColoring()
        userStars = repository.getStars()

        showRewardDialog = true
        showConfetti = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF5FF))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Coloring Game 🎨",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Category Selector Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ColoringCategory.values().forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Button(
                        onClick = {
                            selectedCategory = cat
                            itemIndex = 0
                            audioEngine.speak("${cat.title} category")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFF9333EA) else Color.White,
                            contentColor = if (isSelected) Color.White else Color(0xFF6B21A8)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("${cat.emoji} ${cat.title.substringBefore(" ")}", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }

            // Top Control Bar (Prev / Next Item Title / Finish)
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (itemIndex > 0) itemIndex-- else itemIndex = currentItems.size - 1
                    },
                    modifier = Modifier
                        .background(Color.White, CircleShape)
                        .shadow(2.dp, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", tint = Color(0xFF9333EA))
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF3E8FF),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFC084FC))
                ) {
                    Text(
                        text = "${currentItem.emoji} ${currentItem.hint}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF581C87),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                IconButton(
                    onClick = {
                        itemIndex++
                    },
                    modifier = Modifier
                        .background(Color.White, CircleShape)
                        .shadow(2.dp, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = Color(0xFF9333EA))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Boundary Coloring Canvas
            val density = LocalDensity.current
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .weight(1f)
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(3.dp, Color(0xFFC084FC))
            ) {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val canvasWidth = constraints.maxWidth.toFloat()
                    val canvasHeight = constraints.maxHeight.toFloat()

                    // Generate Vector Path of Letter / Word / Number for exact boundary clipping
                    val textPath = remember(currentItem.text, canvasWidth, canvasHeight) {
                        val nativePath = android.graphics.Path()
                        val textPaint = Paint().apply {
                            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                            isAntiAlias = true
                            textSize = if (currentItem.text.length <= 2) {
                                Math.min(canvasWidth, canvasHeight) * 0.65f
                            } else {
                                Math.min(canvasWidth, canvasHeight) * 0.35f
                            }
                        }

                        val bounds = android.graphics.Rect()
                        textPaint.getTextBounds(currentItem.text, 0, currentItem.text.length, bounds)

                        val startX = (canvasWidth - bounds.width()) / 2f - bounds.left
                        val startY = (canvasHeight + bounds.height()) / 2f - bounds.bottom

                        textPaint.getTextPath(currentItem.text, 0, currentItem.text.length, startX, startY, nativePath)
                        nativePath.asComposePath()
                    }

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(activeTool, activeColor, currentItem) {
                                detectTapGestures { offset ->
                                    if (activeTool == ColoringTool.FILL_BUCKET) {
                                        val fillPath = Path().apply {
                                            addRect(androidx.compose.ui.geometry.Rect(0f, 0f, canvasWidth, canvasHeight))
                                        }
                                        drawnStrokes.add(
                                            DrawnStroke(
                                                path = fillPath,
                                                color = activeColor,
                                                strokeWidth = 0f,
                                                isBucketFill = true
                                            )
                                        )
                                        redoStrokes.clear()
                                        audioEngine.playClickSound()
                                    }
                                }
                            }
                            .pointerInput(activeTool, activeColor, currentItem) {
                                if (activeTool != ColoringTool.FILL_BUCKET) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            val p = Path().apply { moveTo(offset.x, offset.y) }
                                            currentPath = p
                                        },
                                        onDrag = { change, _ ->
                                            change.consume()
                                            currentPath?.let { p ->
                                                val lastPos = change.position
                                                p.lineTo(lastPos.x, lastPos.y)
                                            }
                                        },
                                        onDragEnd = {
                                            currentPath?.let { p ->
                                                val strokeWidthPx = with(density) { activeTool.strokeWidthDp.dp.toPx() }
                                                drawnStrokes.add(
                                                    DrawnStroke(
                                                        path = p,
                                                        color = if (activeTool.isEraser) Color.White else activeColor,
                                                        strokeWidth = strokeWidthPx,
                                                        isEraser = activeTool.isEraser
                                                    )
                                                )
                                                redoStrokes.clear()
                                            }
                                            currentPath = null
                                        }
                                    )
                                }
                            }
                    ) {
                        // 1. Clip all internal drawing operations strictly to letter boundary path
                        clipPath(textPath) {
                            // Base interior fill color
                            drawRect(color = Color(0xFFFFFBEB))

                            // 2. Draw user strokes & fill actions INSIDE letter boundary ONLY
                            drawnStrokes.forEach { stroke ->
                                if (stroke.isBucketFill) {
                                    drawRect(color = stroke.color)
                                } else if (stroke.isEraser) {
                                    drawPath(
                                        path = stroke.path,
                                        color = Color(0xFFFFFBEB),
                                        style = Stroke(
                                            width = stroke.strokeWidth,
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Round
                                        )
                                    )
                                } else {
                                    drawPath(
                                        path = stroke.path,
                                        color = stroke.color,
                                        style = Stroke(
                                            width = stroke.strokeWidth,
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Round
                                        )
                                    )
                                }
                            }

                            // Active stroke currently being drawn by user touch
                            currentPath?.let { path ->
                                val strokeWidthPx = activeTool.strokeWidthDp.dp.toPx()
                                drawPath(
                                    path = path,
                                    color = if (activeTool.isEraser) Color(0xFFFFFBEB) else activeColor,
                                    style = Stroke(
                                        width = strokeWidthPx,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }
                        }

                        // 3. Draw high-contrast letter border outline on top
                        drawPath(
                            path = textPath,
                            color = Color(0xFF1E293B),
                            style = Stroke(
                                width = 6.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Tools Bar: Paint Brush, Thick Brush, Pencil, Fill Bucket, Eraser, Undo, Redo, Clear
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(vertical = 4.dp, horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ColoringTool.values().forEach { tool ->
                    val isSelected = activeTool == tool
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) Color(0xFF9333EA) else Color(0xFFF3E8FF),
                        modifier = Modifier
                            .clickable {
                                activeTool = tool
                                audioEngine.speak(tool.label)
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(tool.emoji, fontSize = 16.sp)
                            Text(
                                tool.label.substringBefore(" "),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFF6B21A8)
                            )
                        }
                    }
                }

                // Undo
                IconButton(
                    onClick = {
                        if (drawnStrokes.isNotEmpty()) {
                            val last = drawnStrokes.removeAt(drawnStrokes.size - 1)
                            redoStrokes.add(last)
                            audioEngine.playClickSound()
                        }
                    },
                    enabled = drawnStrokes.isNotEmpty()
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo",
                        tint = if (drawnStrokes.isNotEmpty()) Color(0xFF9333EA) else Color.LightGray
                    )
                }

                // Redo
                IconButton(
                    onClick = {
                        if (redoStrokes.isNotEmpty()) {
                            val last = redoStrokes.removeAt(redoStrokes.size - 1)
                            drawnStrokes.add(last)
                            audioEngine.playClickSound()
                        }
                    },
                    enabled = redoStrokes.isNotEmpty()
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Redo,
                        contentDescription = "Redo",
                        tint = if (redoStrokes.isNotEmpty()) Color(0xFF9333EA) else Color.LightGray
                    )
                }

                // Clear
                IconButton(
                    onClick = {
                        drawnStrokes.clear()
                        redoStrokes.clear()
                        audioEngine.playClickSound()
                    }
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Clear", tint = Color(0xFFEF4444))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Color Palette Selector
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(colorPalette) { color ->
                    val isSelected = activeColor == color && activeTool != ColoringTool.ERASER
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.5.dp else 1.5.dp,
                                color = if (isSelected) Color(0xFF9333EA) else Color(0xFFCBD5E1),
                                shape = CircleShape
                            )
                            .clickable {
                                activeColor = color
                                if (activeTool == ColoringTool.ERASER) {
                                    activeTool = ColoringTool.PAINT_BRUSH
                                }
                                audioEngine.playClickSound()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = if (color == Color.White) Color.Black else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action Bar (Finish Artwork)
            Button(
                onClick = { finishAndCelebrate() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Finish Artwork ✨", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Masterpiece Completed! 🎨✨",
            message = "Excellent job! You colored \"${currentItem.text}\"! Earned 5 Stars & 5 Coins!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                itemIndex++
            },
            onHome = onBackClick
        )

        ConfettiOverlay(isVisible = showConfetti)
    }
}
