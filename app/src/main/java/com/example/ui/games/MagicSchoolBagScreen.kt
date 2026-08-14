package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.ui.components.*
import kotlinx.coroutines.delay

data class BagCategory(
    val name: String,
    val emoji: String,
    val items: List<BagItem>
)

data class BagItem(
    val id: String,
    val name: String,
    val emoji: String,
    val colorHex: Long = 0xFF6366F1
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MagicSchoolBagScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var userCoins by remember { mutableIntStateOf(repository.getCoins()) }

    var selectedDifficulty by remember { mutableStateOf("Easy") } // "Easy" (3), "Medium" (5), "Hard" (8)
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }

    val categories = remember {
        listOf(
            BagCategory(
                name = "School Objects",
                emoji = "🎒",
                items = listOf(
                    BagItem("pencil", "Pencil", "✏️", 0xFFF59E0B),
                    BagItem("book", "Book", "📖", 0xFF3B82F6),
                    BagItem("eraser", "Eraser", "🧽", 0xFFEC4899),
                    BagItem("backpack", "Backpack", "🎒", 0xFF8B5CF6),
                    BagItem("apple", "Apple", "🍎", 0xFFEF4444),
                    BagItem("scissors", "Scissors", "✂️", 0xFF10B981),
                    BagItem("crayon", "Crayon", "🖍️", 0xFFF97316),
                    BagItem("ruler", "Ruler", "📏", 0xFF06B6D4),
                    BagItem("pen", "Pen", "🖊️", 0xFF6366F1),
                    BagItem("paper", "Paper", "📄", 0xFF6B7280)
                )
            ),
            BagCategory(
                name = "Fruits",
                emoji = "🍎",
                items = listOf(
                    BagItem("banana", "Banana", "🍌", 0xFFEAB308),
                    BagItem("orange", "Orange", "🍊", 0xFFF97316),
                    BagItem("strawberry", "Strawberry", "🍓", 0xFFEF4444),
                    BagItem("grapes", "Grapes", "🍇", 0xFF8B5CF6),
                    BagItem("watermelon", "Watermelon", "🍉", 0xFF10B981),
                    BagItem("pineapple", "Pineapple", "🍍", 0xFFF59E0B),
                    BagItem("cherry", "Cherry", "🍒", 0xFFDC2626),
                    BagItem("peach", "Peach", "🍑", 0xFFFB923C)
                )
            ),
            BagCategory(
                name = "Animals",
                emoji = "🦁",
                items = listOf(
                    BagItem("dog", "Dog", "🐶", 0xFFD97706),
                    BagItem("cat", "Cat", "🐱", 0xFFF59E0B),
                    BagItem("lion", "Lion", "🦁", 0xFFEAB308),
                    BagItem("elephant", "Elephant", "🐘", 0xFF6B7280),
                    BagItem("monkey", "Monkey", "🐵", 0xFF78350F),
                    BagItem("bear", "Bear", "🐻", 0xFF92400E),
                    BagItem("duck", "Duck", "🦆", 0xFF10B981),
                    BagItem("frog", "Frog", "🐸", 0xFF22C55E),
                    BagItem("panda", "Panda", "🐼", 0xFF1F2937)
                )
            ),
            BagCategory(
                name = "Colors",
                emoji = "🎨",
                items = listOf(
                    BagItem("red", "Red", "🔴", 0xFFEF4444),
                    BagItem("blue", "Blue", "🔵", 0xFF3B82F6),
                    BagItem("green", "Green", "🟢", 0xFF10B981),
                    BagItem("yellow", "Yellow", "🟡", 0xFFEAB308),
                    BagItem("purple", "Purple", "🟣", 0xFF8B5CF6),
                    BagItem("orange_color", "Orange", "🟠", 0xFFF97316),
                    BagItem("pink", "Pink", "🩷", 0xFFEC4899),
                    BagItem("black", "Black", "⬛", 0xFF111827)
                )
            ),
            BagCategory(
                name = "Shapes",
                emoji = "🔷",
                items = listOf(
                    BagItem("circle", "Circle", "🔴", 0xFFEF4444),
                    BagItem("square", "Square", "🟦", 0xFF3B82F6),
                    BagItem("triangle", "Triangle", "🔺", 0xFFF59E0B),
                    BagItem("star", "Star", "⭐", 0xFFEAB308),
                    BagItem("heart", "Heart", "🩷", 0xFFEC4899),
                    BagItem("diamond", "Diamond", "🔷", 0xFF06B6D4)
                )
            ),
            BagCategory(
                name = "Vehicles",
                emoji = "🚗",
                items = listOf(
                    BagItem("car", "Car", "🚗", 0xFFEF4444),
                    BagItem("bus", "Bus", "🚌", 0xFFEAB308),
                    BagItem("train", "Train", "🚂", 0xFF6366F1),
                    BagItem("airplane", "Airplane", "✈️", 0xFF06B6D4),
                    BagItem("bicycle", "Bicycle", "🚲", 0xFF10B981),
                    BagItem("boat", "Boat", "⛵", 0xFF3B82F6),
                    BagItem("firetruck", "Firetruck", "🚒", 0xFFDC2626)
                )
            ),
            BagCategory(
                name = "House Objects",
                emoji = "🏠",
                items = listOf(
                    BagItem("chair", "Chair", "🪑", 0xFFD97706),
                    BagItem("sofa", "Sofa", "🛋️", 0xFF8B5CF6),
                    BagItem("bed", "Bed", "🛏️", 0xFF3B82F6),
                    BagItem("clock", "Clock", "⏰", 0xFFEF4444),
                    BagItem("lamp", "Lamp", "💡", 0xFFEAB308),
                    BagItem("key", "Key", "🔑", 0xFFF59E0B),
                    BagItem("door", "Door", "🚪", 0xFF78350F)
                )
            )
        )
    }

    val currentCategory = categories[selectedCategoryIndex]

    var targetItem by remember { mutableStateOf(currentCategory.items.random()) }
    var displayedItems by remember { mutableStateOf(listOf<BagItem>()) }

    var shakingItemId by remember { mutableStateOf<String?>(null) }
    var successItemId by remember { mutableStateOf<String?>(null) }
    var roundScore by remember { mutableIntStateOf(0) }

    var showConfetti by remember { mutableStateOf(false) }
    var showVictoryDialog by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }

    val itemCountNeeded = when (selectedDifficulty) {
        "Medium" -> 5
        "Hard" -> 8
        else -> 3
    }

    fun generateNewRound() {
        val cat = categories[selectedCategoryIndex]
        targetItem = cat.items.random()

        val otherItems = cat.items.filter { it.id != targetItem.id }.shuffled()
        val distCount = (itemCountNeeded - 1).coerceAtMost(otherItems.size)
        val selectedOthers = otherItems.take(distCount)

        displayedItems = (listOf(targetItem) + selectedOthers).shuffled()
        shakingItemId = null
        successItemId = null
        feedbackMessage = "Find the ${targetItem.name}!"

        audioEngine.speak("Find the ${targetItem.name}!")
    }

    LaunchedEffect(selectedCategoryIndex, selectedDifficulty) {
        generateNewRound()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "magicBag")
    val bagBounceScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "bounce"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFEDE9FE),
                        Color(0xFFDDD6FE),
                        Color(0xFFC4B5FD)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Magic School Bag 🎒",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Difficulty & Category Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Difficulty Selector
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Difficulty:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF4C1D95)
                    )
                    listOf("Easy", "Medium", "Hard").forEach { diff ->
                        val isSelected = selectedDifficulty == diff
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedDifficulty = diff },
                            label = {
                                Text(
                                    text = diff,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF7C3AED),
                                selectedLabelColor = Color.White,
                                containerColor = Color.White.copy(alpha = 0.8f),
                                labelColor = Color(0xFF5B21B6)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Categories Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(categories.indices.toList()) { index ->
                        val cat = categories[index]
                        val isSelected = selectedCategoryIndex == index
                        Card(
                            onClick = { selectedCategoryIndex = index },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF6D28D9) else Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(cat.emoji, fontSize = 16.sp)
                                Text(
                                    cat.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color(0xFF4C1D95)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Magical School Bag Header Banner with Target Voice Prompt
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Magical Bag Animated Visual
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .scale(bagBounceScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFFC084FC), Color(0xFF7C3AED))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎒", fontSize = 38.sp)
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "Find the:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6B21A8)
                        )
                        Text(
                            text = "${targetItem.emoji} ${targetItem.name}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF5B21B6)
                        )
                    }

                    // Listen Audio Button
                    IconButton(
                        onClick = {
                            audioEngine.speak("Find the ${targetItem.name}!")
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFDDD6FE), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Speak Target",
                            tint = Color(0xFF5B21B6)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Game Items Grid (popped out of the bag)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.7f))
                    .padding(12.dp)
            ) {
                val gridColumns = when (displayedItems.size) {
                    in 1..3 -> 3
                    in 4..6 -> 3
                    else -> 4
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridColumns),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(displayedItems) { item ->
                        val isTarget = item.id == targetItem.id
                        val isShaking = shakingItemId == item.id
                        val isSuccess = successItemId == item.id

                        val scaleAnim by animateFloatAsState(
                            targetValue = if (isSuccess) 1.25f else 1.0f,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            label = "scale"
                        )

                        val shakeOffset by animateFloatAsState(
                            targetValue = if (isShaking) 15f else 0f,
                            animationSpec = repeatable(
                                iterations = 4,
                                animation = tween(60, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "shake"
                        )

                        Card(
                            onClick = {
                                if (isTarget) {
                                    successItemId = item.id
                                    showConfetti = true
                                    repository.rewardCorrectAnswer()
                                    repository.addStars(1)
                                    userStars = repository.getStars()
                                    userCoins = repository.getCoins()

                                    roundScore++
                                    audioEngine.speak("${item.name}! Great job!")
                                    feedbackMessage = "🎉 Excellent! That is the ${item.name}!"

                                    if (roundScore >= 5) {
                                        repository.rewardFinishGame()
                                        userStars = repository.getStars()
                                        userCoins = repository.getCoins()
                                        showVictoryDialog = true
                                    }
                                } else {
                                    shakingItemId = item.id
                                    audioEngine.speak("Try again! Find the ${targetItem.name}!")
                                    feedbackMessage = "❌ Try Again! Find the ${targetItem.name}!"
                                }
                            },
                            modifier = Modifier
                                .aspectRatio(1f)
                                .scale(scaleAnim)
                                .offset(x = shakeOffset.dp)
                                .shadow(6.dp, RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSuccess) Color(0xFFDCFCE7) else Color.White
                            ),
                            border = if (isSuccess) androidx.compose.foundation.BorderStroke(
                                3.dp,
                                Color(0xFF22C55E)
                            ) else null
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = item.emoji,
                                    fontSize = if (displayedItems.size > 6) 36.sp else 46.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.name,
                                    fontSize = if (displayedItems.size > 6) 12.sp else 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF374151),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Next / Refresh Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = feedbackMessage,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5B21B6),
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = { generateNewRound() },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Next")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Next Object 🎒", fontWeight = FontWeight.Bold)
                }
            }
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showVictoryDialog,
            title = "Magic School Bag Master! 🎒✨",
            message = "You found all items! You earned 10 Coins and 5 Stars!",
            onNext = {
                showVictoryDialog = false
                roundScore = 0
                generateNewRound()
            },
            onHome = onBackClick
        )
    }
}
