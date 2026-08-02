package com.example.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository

data class HomeCardItem(
    val id: String,
    val title: String,
    val emoji: String,
    val category: String, // "Letters", "Numbers", "Creativity", "Speaking", "Reading", "Writing", "Adventure", "Rewards"
    val cardColorHex: Long,
    val textColorHex: Long = 0xFFFFFFFF,
    val route: String
)

@Composable
fun HomeScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onNavigateToGame: (String) -> Unit
) {
    val totalStars by remember { mutableIntStateOf(repository.getStars()) }
    val totalCoins by remember { mutableIntStateOf(repository.getCoins()) }
    val childName by remember { mutableStateOf(repository.getChildName()) }
    val avatarEmoji by remember { mutableStateOf(repository.getAvatarEmoji()) }
    var selectedCategory by remember { mutableStateOf("All") }

    // Start / Resume BGM when entering Home Screen
    DisposableEffect(Unit) {
        audioEngine.resumeBgm()
        onDispose {
            // Keep BGM state managed by individual screens
        }
    }

    val homeCards = remember {
        listOf(
            HomeCardItem("learn_letters", "Learn Letters", "🔤", "Letters", 0xFF6366F1, route = "capital_small"),
            HomeCardItem("learn_numbers", "Learn Numbers", "🔢", "Numbers", 0xFFEC4899, route = "train"),
            HomeCardItem("songs_karaoke", "Songs & Karaoke", "🎵", "Speaking", 0xFFF59E0B, route = "songs_music"),

            HomeCardItem("real_speaking", "Leo Coach Speaking", "🗣️", "Speaking", 0xFFDC2626, route = "real_speaking"),
            HomeCardItem("fishing_letters", "Fishing Letters", "🎣", "Letters", 0xFF00ACC1, route = "fishing_letters"),
            HomeCardItem("fishing_numbers", "Fishing Numbers", "🐟", "Numbers", 0xFF0284C7, route = "fishing_numbers"),
            HomeCardItem("shopping_game", "Shopping Supermarket", "🛒", "Reading", 0xFFD97706, route = "shopping_game"),
            HomeCardItem("listen_tap", "Listen & Tap", "🎧", "Reading", 0xFF166534, route = "listen_tap"),
            HomeCardItem("build_sentence", "Build Sentence", "📜", "Writing", 0xFF2563EB, route = "build_sentence"),
            HomeCardItem("color_by_number", "Color By Number", "🎨", "Creativity", 0xFFC084FC, route = "color_by_number"),

            HomeCardItem("adventure_mode", "Adventure Mode", "🚀", "Adventure", 0xFFF97316, route = "adventure_mode"),
            HomeCardItem("vocabulary_book", "Vocabulary Book", "📖", "Reading", 0xFF6366F1, route = "dictionary"),
            HomeCardItem("coloring", "Coloring Studio", "🖌️", "Creativity", 0xFF06B6D4, route = "coloring"),
            HomeCardItem("memory_game", "Memory Game", "🧠", "Creativity", 0xFFA855F7, route = "memory"),
            HomeCardItem("handwriting", "Handwriting Tracing", "✏️", "Writing", 0xFFEAB308, route = "tracing"),
            HomeCardItem("workbook", "Letter Writing Workbook", "📝", "Writing", 0xFF059669, route = "handwriting_workbook"),

            HomeCardItem("puzzle_game", "Puzzle Game", "🧩", "Creativity", 0xFFA855F7, route = "odd_one_out"),
            HomeCardItem("treasure_hunt", "Treasure Hunt", "💎", "Adventure", 0xFFD97706, route = "treasure_hunt"),
            HomeCardItem("phonics_game", "Phonics Game", "🔤", "Letters", 0xFF10B981, route = "listen_choose"),
            HomeCardItem("spelling_game", "Spelling Game", "⌨️", "Writing", 0xFFF43F5E, route = "typing"),
            HomeCardItem("sorting_game", "Sorting Game", "🔷", "Numbers", 0xFF3B82F6, route = "drag_match"),

            HomeCardItem("find_letter", "Find the Letter", "🔍", "Letters", 0xFF6366F1, route = "missing_letter"),
            HomeCardItem("ice_cream_shop", "Ice Cream Shop", "🍦", "Numbers", 0xFFE11D48, route = "ice_cream"),
            HomeCardItem("dino_game", "Dino Eggs & Hunt", "🦖", "Adventure", 0xFF8B5CF6, route = "dino_hatch"),
            HomeCardItem("rewards", "Rewards & Badges", "🏆", "Rewards", 0xFFEAB308, route = "rewards"),
            HomeCardItem("parent_dashboard", "Parent Dashboard", "👨‍👩‍👧", "Rewards", 0xFFEC4899, route = "parent_progress"),
            HomeCardItem("settings", "Settings & Profile", "⚙️", "Rewards", 0xFF8B5CF6, route = "profile_settings")
        )
    }

    val filteredCards = remember(selectedCategory) {
        if (selectedCategory == "All") homeCards
        else homeCards.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFEFF6FF), // Soft Pastel Sky Blue
                        Color(0xFFFAF5FF), // Soft Purple Tint
                        Color(0xFFFFF7ED)  // Soft Warm Sunset
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar (Profile Header & Rainbow Arch)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                // Background Rainbow Graphic
                RainbowArchCanvas(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(65.dp)
                        .align(Alignment.TopCenter)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(text = avatarEmoji, fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Welcome,",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = childName,
                                fontSize = 13.sp,
                                color = Color(0xFF1E293B),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Coins Badge & Settings
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Coins Counter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White)
                                .border(1.5.dp, Color(0xFFFDE68A), RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "$totalCoins", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFB45309))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "🪙", fontSize = 16.sp)
                            }
                        }

                        // Settings Gear Button
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8B5CF6))
                                .clickable {
                                    audioEngine.playClickSound()
                                    onNavigateToGame("profile_settings")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Settings",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Logo & Subtitle
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Khaled Kamal Kids",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF4F46E5),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "✨ عالم لتعليم الإنجليزية ممتعة وسحر ✨",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEC4899),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Bar (Pills)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("All", "Letters", "Numbers", "Speaking", "Reading", "Writing", "Creativity").forEach { cat ->
                    val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) Color(0xFF4F46E5) else Color.White)
                            .border(1.dp, if (isSelected) Color(0xFF4F46E5) else Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                            .clickable {
                                audioEngine.playClickSound()
                                selectedCategory = cat
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color(0xFF64748B),
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Master Game Grid (3 Columns portrait)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 14.dp)
            ) {
                items(filteredCards) { card ->
                    InteractiveHomeCard(
                        card = card,
                        onClick = {
                            audioEngine.playClickSound()
                            audioEngine.speak(card.title)
                            onNavigateToGame(card.route)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Stars Badge
            Box(
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(1.5.dp, Color(0xFFFDE68A), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⭐", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$totalStars stars collected",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309)
                    )
                }
            }
        }
    }
}

@Composable
fun InteractiveHomeCard(
    card: HomeCardItem,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.92f else 1.0f, label = "cardScale")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Color(card.cardColorHex))
            .clickable(interactionSource = interactionSource, indication = null) {
                onClick()
            }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 3D Cartoon Icon Box
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = card.emoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = card.title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 13.sp,
                maxLines = 2
            )
        }
    }
}

@Composable
fun RainbowArchCanvas(modifier: Modifier = Modifier) {
    val rainbowColors = listOf(
        Color(0xFFF43F5E), // Red/Pink
        Color(0xFFFB923C), // Orange
        Color(0xFFFACC15), // Yellow
        Color(0xFF4ADE80), // Green
        Color(0xFF38BDF8), // Blue
        Color(0xFFA855F7)  // Purple
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        rainbowColors.forEachIndexed { index, color ->
            val strokeWidth = 5.dp.toPx()
            val radiusOffset = index * strokeWidth
            val path = Path().apply {
                moveTo(radiusOffset, height)
                cubicTo(
                    radiusOffset, height * 0.1f,
                    width - radiusOffset, height * 0.1f,
                    width - radiusOffset, height
                )
            }
            drawPath(
                path = path,
                color = color,
                style = Stroke(width = strokeWidth)
            )
        }
    }
}
