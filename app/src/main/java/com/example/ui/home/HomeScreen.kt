package com.example.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository

enum class TileType {
    ABC,
    NUMBERS,
    EMOJI
}

data class KkHomeCardItem(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val tileType: TileType,
    val emoji: String = "",
    val category: String, // "featured", "letters", "numbers", "games", "parents"
    val bgColorHex: Long,
    val route: String
)

@Composable
fun RainbowHeaderCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = 12.dp.toPx()
        val colors = listOf(
            Color(0xFFFF7BB0), // Pink
            Color(0xFFFF9E7A), // Coral
            Color(0xFFFFD56B), // Yellow
            Color(0xFF5CDBB5), // Mint Green
            Color(0xFF67C6FF), // Cyan Blue
            Color(0xFFA78BFA)  // Soft Violet
        )

        val baseRadius = size.width * 0.44f
        val centerX = size.width / 2f
        val centerY = size.height * 0.96f

        colors.forEachIndexed { index, color ->
            val radius = baseRadius - (index * (strokeWidth * 0.86f))
            if (radius > 0) {
                drawArc(
                    color = color,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(centerX - radius, centerY - radius),
                    size = Size(radius * 2f, radius * 2f),
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    )
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onNavigateToGame: (String) -> Unit
) {
    val totalStars by remember { mutableIntStateOf(repository.getStars()) }
    var selectedCategory by remember { mutableStateOf("All") }
    val currentLang = remember { repository.getLanguage() }

    val homeCards = remember {
        listOf(
            KkHomeCardItem(
                id = "speak_pronunciation",
                title = "Speak & Repeat",
                subtitle = "Microphone Speaking Games",
                tileType = TileType.EMOJI,
                emoji = "🎤",
                category = "featured",
                bgColorHex = 0xFFEC4899, // Vibrant Pink
                route = "speak_pronunciation"
            ),
            KkHomeCardItem(
                id = "letters",
                title = "Learn Letters",
                subtitle = "A to Z Phonics & Tracing",
                tileType = TileType.ABC,
                category = "letters",
                bgColorHex = 0xFF8B5CF6, // Pastel Purple
                route = "capital_small"
            ),
            KkHomeCardItem(
                id = "numbers",
                title = "Learn Numbers",
                subtitle = "1 2 3 Counting & Math",
                tileType = TileType.NUMBERS,
                category = "numbers",
                bgColorHex = 0xFFFF6B9B, // Pastel Pink
                route = "sequence_order"
            ),
            KkHomeCardItem(
                id = "games",
                title = "Games",
                subtitle = "Fun & Interactive Puzzles",
                tileType = TileType.EMOJI,
                emoji = "🎮",
                category = "games",
                bgColorHex = 0xFF38BDF8, // Soft Sky Blue
                route = "drag_match"
            ),
            KkHomeCardItem(
                id = "rewards",
                title = "Rewards",
                subtitle = "Trophy Room & Badges",
                tileType = TileType.EMOJI,
                emoji = "🏆",
                category = "featured",
                bgColorHex = 0xFFFBBF24, // Soft Yellow
                route = "rewards"
            ),
            KkHomeCardItem(
                id = "settings",
                title = "Settings",
                subtitle = "Child Profile & Audio",
                tileType = TileType.EMOJI,
                emoji = "⚙️",
                category = "featured",
                bgColorHex = 0xFF34D399, // Mint Green
                route = "profile_settings"
            ),
            KkHomeCardItem(
                id = "parents",
                title = "Parents Area",
                subtitle = "Progress & Analytics",
                tileType = TileType.EMOJI,
                emoji = "👥",
                category = "parents",
                bgColorHex = 0xFFFB923C, // Warm Orange
                route = "parent_progress"
            ),
            KkHomeCardItem(
                id = "adventure_mode",
                title = "Adventure Mode",
                subtitle = "Explore 26 Learning Worlds",
                tileType = TileType.EMOJI,
                emoji = "🗺️",
                category = "featured",
                bgColorHex = 0xFFF59E0B, // Warm Amber
                route = "adventure_mode"
            ),
            KkHomeCardItem(
                id = "songs_music",
                title = "Songs & Karaoke",
                subtitle = "Sing, Record & Dance",
                tileType = TileType.EMOJI,
                emoji = "🎵",
                category = "featured",
                bgColorHex = 0xFFEC4899, // Deep Pink
                route = "songs_music"
            ),
            KkHomeCardItem(
                id = "dictionary",
                title = "Vocabulary Book",
                subtitle = "My A-Z Word Dictionary",
                tileType = TileType.EMOJI,
                emoji = "📖",
                category = "letters",
                bgColorHex = 0xFF3B82F6, // Royal Blue
                route = "dictionary"
            ),
            KkHomeCardItem(
                id = "feed_animal",
                title = "Leo AI Coach",
                subtitle = "Feed & Speak with Lion",
                tileType = TileType.EMOJI,
                emoji = "🦁",
                category = "featured",
                bgColorHex = 0xFFA855F7, // Lavender Purple
                route = "feed_animal"
            ),
            KkHomeCardItem(
                id = "tracing",
                title = "Handwriting Tracing",
                subtitle = "Trace ABCs & 123s",
                tileType = TileType.EMOJI,
                emoji = "✏️",
                category = "letters",
                bgColorHex = 0xFF22C55E, // Bright Green
                route = "tracing"
            ),
            KkHomeCardItem(
                id = "drag_match",
                title = "Match & Learn",
                subtitle = "Drag & Drop Pair Matching",
                tileType = TileType.EMOJI,
                emoji = "🎯",
                category = "games",
                bgColorHex = 0xFF14B8A6, // Teal
                route = "drag_match"
            ),
            KkHomeCardItem(
                id = "memory",
                title = "Memory Cards",
                subtitle = "Brain Flip & Match",
                tileType = TileType.EMOJI,
                emoji = "🧠",
                category = "games",
                bgColorHex = 0xFF6366F1, // Indigo
                route = "memory"
            ),
            KkHomeCardItem(
                id = "coloring",
                title = "Coloring Game",
                subtitle = "Color by ABC Paint",
                tileType = TileType.EMOJI,
                emoji = "🎨",
                category = "games",
                bgColorHex = 0xFFF43F5E, // Rose Pink
                route = "coloring"
            ),
            KkHomeCardItem(
                id = "treasure_hunt",
                title = "Treasure Hunt",
                subtitle = "Discover Hidden Golden Chests",
                tileType = TileType.EMOJI,
                emoji = "💎",
                category = "featured",
                bgColorHex = 0xFFEAB308, // Gold
                route = "treasure_hunt"
            ),
            KkHomeCardItem(
                id = "missing_letter",
                title = "Find Missing Letter",
                subtitle = "Complete the words",
                tileType = TileType.EMOJI,
                emoji = "🔍",
                category = "letters",
                bgColorHex = 0xFFF59E0B, // Amber
                route = "missing_letter"
            ),
            KkHomeCardItem(
                id = "typing",
                title = "Typing & Spelling",
                subtitle = "Build Fun Words",
                tileType = TileType.EMOJI,
                emoji = "⌨️",
                category = "letters",
                bgColorHex = 0xFFC084FC, // Light Purple
                route = "typing"
            ),
            KkHomeCardItem(
                id = "fishing",
                title = "Fishing Adventure",
                subtitle = "Catch Swimming Letters",
                tileType = TileType.EMOJI,
                emoji = "🎣",
                category = "games",
                bgColorHex = 0xFF0284C7, // Ocean Blue
                route = "fishing"
            ),
            KkHomeCardItem(
                id = "balloon_pop",
                title = "Balloon Pop",
                subtitle = "Pop Target Bubbles",
                tileType = TileType.EMOJI,
                emoji = "🎈",
                category = "games",
                bgColorHex = 0xFFFB7185, // Coral Pink
                route = "balloon_pop"
            ),
            KkHomeCardItem(
                id = "train",
                title = "Alphabet Train",
                subtitle = "Fill Sequence Wagons",
                tileType = TileType.EMOJI,
                emoji = "🚂",
                category = "letters",
                bgColorHex = 0xFFA16207, // Golden Brown
                route = "train"
            ),
            KkHomeCardItem(
                id = "ice_cream",
                title = "Ice Cream Shop",
                subtitle = "Count Delicious Scoops",
                tileType = TileType.EMOJI,
                emoji = "🍦",
                category = "numbers",
                bgColorHex = 0xFFF472B6, // Soft Pink
                route = "ice_cream"
            ),
            KkHomeCardItem(
                id = "animals",
                title = "Animal Kingdom",
                subtitle = "Listen to Animal Sounds",
                tileType = TileType.EMOJI,
                emoji = "🐶",
                category = "games",
                bgColorHex = 0xFF4ADE80, // Bright Green
                route = "animals"
            ),
            KkHomeCardItem(
                id = "listen_choose",
                title = "Listen & Choose",
                subtitle = "Listen & Pick Picture",
                tileType = TileType.EMOJI,
                emoji = "🎧",
                category = "games",
                bgColorHex = 0xFFA78BFA, // Soft Violet
                route = "listen_choose"
            ),
            KkHomeCardItem(
                id = "space_adv",
                title = "Space Adventure",
                subtitle = "Collect Cosmic Stars",
                tileType = TileType.EMOJI,
                emoji = "🚀",
                category = "games",
                bgColorHex = 0xFF0EA5E9, // Electric Cyan
                route = "space_adv"
            ),
            KkHomeCardItem(
                id = "dino_hatch",
                title = "Dino Hatch",
                subtitle = "Hatch Baby Dinos",
                tileType = TileType.EMOJI,
                emoji = "🦖",
                category = "numbers",
                bgColorHex = 0xFFA3E635, // Lime Green
                route = "dino_hatch"
            ),
            KkHomeCardItem(
                id = "odd_one_out",
                title = "Spot Difference",
                subtitle = "Find Odd Letters",
                tileType = TileType.EMOJI,
                emoji = "🔍",
                category = "letters",
                bgColorHex = 0xFF0E7490, // Deep Cyan
                route = "odd_one_out"
            )
        )
    }

    val filteredCards = remember(selectedCategory) {
        if (selectedCategory == "All") homeCards
        else homeCards.filter { it.category == selectedCategory.lowercase() }
    }

    LaunchedEffect(Unit) {
        audioEngine.speak("Welcome to Khaled Kamal Kids!")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8F5FF), // Soft Lavender Tint Top
                        Color(0xFFF0F7FF), // Soft Cyan Tint Middle
                        Color(0xFFFFFBF5)  // Soft Warm Bottom
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sound Mute Controls Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { audioEngine.isMuted = !audioEngine.isMuted },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f))
                ) {
                    Icon(
                        imageVector = if (audioEngine.isMuted) Icons.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Mute Toggle",
                        tint = Color(0xFF6366F1)
                    )
                }
            }

            // Rainbow Header Graphic
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                RainbowHeaderCanvas(
                    modifier = Modifier
                        .width(280.dp)
                        .height(125.dp)
                )

                Text(
                    text = "✨",
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.TopStart).padding(start = 40.dp, top = 20.dp)
                )
                Text(
                    text = "✨",
                    fontSize = 24.sp,
                    modifier = Modifier.align(Alignment.TopEnd).padding(end = 40.dp, top = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Khaled Kamal Kids Multi-Color Styled Title
            val titleAnnotated = remember {
                buildAnnotatedString {
                    val text = "Khaled Kamal Kids"
                    val colors = listOf(
                        Color(0xFF7C3AED), // K - Purple
                        Color(0xFF2563EB), // h - Blue
                        Color(0xFF059669), // a - Green
                        Color(0xFFD97706), // l - Amber
                        Color(0xFFDC2626), // e - Red
                        Color(0xFFDB2777), // d - Pink
                        Color(0xFF7C3AED), // K
                        Color(0xFF2563EB), // a
                        Color(0xFF059669), // m
                        Color(0xFFD97706), // a
                        Color(0xFFDC2626), // l
                        Color(0xFFDB2777), // K
                        Color(0xFF7C3AED), // i
                        Color(0xFF2563EB), // d
                        Color(0xFF059669)  // s
                    )
                    var colorIdx = 0
                    text.forEach { ch ->
                        if (ch != ' ') {
                            val color = colors[colorIdx % colors.size]
                            withStyle(SpanStyle(color = color)) {
                                append(ch.toString())
                            }
                            colorIdx++
                        } else {
                            append(" ")
                        }
                    }
                }
            }

            Text(
                text = titleAnnotated,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle
            Text(
                text = if (currentLang == "Arabic") "✨ تعلّم اللغة الإنجليزية بمتعة وسحر ✨" else "✨ Learn English with Fun & Magic ✨",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Category Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("All", "Letters", "Numbers", "Games", "Parents").forEach { categoryName ->
                    val isSelected = selectedCategory == categoryName
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Color(0xFF8B5CF6) else Color.White)
                            .clickable {
                                selectedCategory = categoryName
                                audioEngine.speak("$categoryName games!")
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = categoryName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2-Column Grid of Colorful Rounded Cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(filteredCards) { cardItem ->
                    KkKidsHomeCard(
                        item = cardItem,
                        onClick = {
                            audioEngine.speak(cardItem.title)
                            onNavigateToGame(cardItem.route)
                        }
                    )
                }
            }
        }

        // Floating Stars Pill at Bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .shadow(10.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .clickable {
                    audioEngine.speak("You have collected $totalStars stars!")
                    onNavigateToGame("rewards")
                }
                .padding(horizontal = 22.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "⭐", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$totalStars stars collected",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF6366F1)
                )
            }
        }
    }
}

@Composable
fun KkKidsHomeCard(
    item: KkHomeCardItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .shadow(6.dp, RoundedCornerShape(32.dp), spotColor = Color(item.bgColorHex).copy(alpha = 0.35f))
            .clickable { onClick() },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color(item.bgColorHex))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Translucent light overlay circle in bottom right corner (from screenshot reference!)
            Box(
                modifier = Modifier
                    .size(105.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 24.dp, y = 24.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Left Icon Container
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.28f)),
                    contentAlignment = Alignment.Center
                ) {
                    when (item.tileType) {
                        TileType.ABC -> {
                            Text(
                                text = "abc",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                        TileType.NUMBERS -> {
                            Text(
                                text = "1 2\n3 4",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                lineHeight = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        TileType.EMOJI -> {
                            Text(
                                text = item.emoji,
                                fontSize = 28.sp
                            )
                        }
                    }
                }

                // Bottom Left Title
                Text(
                    text = item.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    lineHeight = 21.sp
                )
            }
        }
    }
}
