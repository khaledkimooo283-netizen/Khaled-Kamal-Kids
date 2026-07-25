package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.ui.components.KkHeader
import com.example.ui.components.KkLionMascot
import com.example.ui.components.MascotState

data class BentoGameItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val category: String, // "featured", "letters", "numbers", "puzzles"
    val startColorHex: Long,
    val endColorHex: Long,
    val isSpanFull: Boolean = false,
    val route: String
)

@Composable
fun HomeScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onNavigateToGame: (String) -> Unit
) {
    val totalStars by remember { mutableIntStateOf(repository.getStars()) }
    var selectedCategory by remember { mutableStateOf("All") }

    val bentoGames = remember {
        listOf(
            BentoGameItem(
                id = "fishing",
                title = "Fishing Adventure",
                subtitle = "Catch swimming letters & numbers!",
                emoji = "🎣",
                category = "featured",
                startColorHex = 0xFF38BDF8,
                endColorHex = 0xFF2563EB,
                isSpanFull = true,
                route = "fishing"
            ),
            BentoGameItem(
                id = "tracing",
                title = "Tracing & Writing",
                subtitle = "Learn ABCs & 123s",
                emoji = "✏️",
                category = "letters",
                startColorHex = 0xFF34D399,
                endColorHex = 0xFF059669,
                route = "tracing"
            ),
            BentoGameItem(
                id = "drag_match",
                title = "Match & Learn",
                subtitle = "Drag & match objects",
                emoji = "🎯",
                category = "numbers",
                startColorHex = 0xFFFBBF24,
                endColorHex = 0xFFD97706,
                route = "drag_match"
            ),
            BentoGameItem(
                id = "typing",
                title = "Typing & Spelling",
                subtitle = "Build fun words!",
                emoji = "⌨️",
                category = "letters",
                startColorHex = 0xFFC084FC,
                endColorHex = 0xFF9333EA,
                route = "typing"
            ),
            BentoGameItem(
                id = "balloon_pop",
                title = "Balloon Pop",
                subtitle = "Pop target bubbles",
                emoji = "🎈",
                category = "puzzles",
                startColorHex = 0xFFFB7185,
                endColorHex = 0xFFE11D48,
                route = "balloon_pop"
            ),
            BentoGameItem(
                id = "train",
                title = "Alphabet Train",
                subtitle = "Fill missing sequence",
                emoji = "🚂",
                category = "letters",
                startColorHex = 0xFFA16207,
                endColorHex = 0xFF713F12,
                route = "train"
            ),
            BentoGameItem(
                id = "ice_cream",
                title = "Ice Cream Shop",
                subtitle = "Count delicious scoops",
                emoji = "🍦",
                category = "numbers",
                startColorHex = 0xFFF472B6,
                endColorHex = 0xFFDB2777,
                route = "ice_cream"
            ),
            BentoGameItem(
                id = "animals",
                title = "Animal Kingdom",
                subtitle = "Listen to animal sounds",
                emoji = "🐶",
                category = "puzzles",
                startColorHex = 0xFF4ADE80,
                endColorHex = 0xFF16A34A,
                route = "animals"
            ),
            BentoGameItem(
                id = "memory",
                title = "Memory Cards",
                subtitle = "Flip & match brain pairs",
                emoji = "🧠",
                category = "puzzles",
                startColorHex = 0xFF818CF8,
                endColorHex = 0xFF4F46E5,
                route = "memory"
            ),
            BentoGameItem(
                id = "shadow_match",
                title = "Shadow Matching",
                subtitle = "Find silhouette pairs",
                emoji = "👤",
                category = "puzzles",
                startColorHex = 0xFFA78BFA,
                endColorHex = 0xFF7C3AED,
                route = "shadow_match"
            ),
            BentoGameItem(
                id = "space_adv",
                title = "Space Adventure",
                subtitle = "Collect cosmic stars",
                emoji = "🚀",
                category = "adventures",
                startColorHex = 0xFF38BDF8,
                endColorHex = 0xFF0284C7,
                route = "space_adv"
            ),
            BentoGameItem(
                id = "treasure_hunt",
                title = "Treasure Hunt",
                subtitle = "Find golden chests",
                emoji = "💎",
                category = "adventures",
                startColorHex = 0xFFFBBF24,
                endColorHex = 0xFFB45309,
                route = "treasure_hunt"
            ),
            BentoGameItem(
                id = "feed_animal",
                title = "Feed the Lion",
                subtitle = "Yummy letter cookies",
                emoji = "🍪",
                category = "adventures",
                startColorHex = 0xFF4ADE80,
                endColorHex = 0xFF15803D,
                route = "feed_animal"
            ),
            BentoGameItem(
                id = "coloring",
                title = "Coloring Book",
                subtitle = "Color by letters",
                emoji = "🎨",
                category = "puzzles",
                startColorHex = 0xFFF472B6,
                endColorHex = 0xFFBE185D,
                route = "coloring"
            ),
            BentoGameItem(
                id = "odd_one_out",
                title = "Spot Difference",
                subtitle = "Find odd letters",
                emoji = "🔍",
                category = "letters",
                startColorHex = 0xFF38BDF8,
                endColorHex = 0xFF0369A1,
                route = "odd_one_out"
            ),
            BentoGameItem(
                id = "dino_hatch",
                title = "Dino Hatch",
                subtitle = "Hatch baby dinos",
                emoji = "🦖",
                category = "numbers",
                startColorHex = 0xFFA3E635,
                endColorHex = 0xFF4D7C0F,
                route = "dino_hatch"
            ),
            BentoGameItem(
                id = "rewards",
                title = "Trophy Room",
                subtitle = "View badges & stars",
                emoji = "🏆",
                category = "featured",
                startColorHex = 0xFFFACC15,
                endColorHex = 0xFFCA8A04,
                isSpanFull = true,
                route = "rewards"
            )
        )
    }

    val filteredGames = remember(selectedCategory) {
        if (selectedCategory == "All") bentoGames
        else bentoGames.filter { it.category == selectedCategory.lowercase() || it.isSpanFull }
    }

    LaunchedEffect(Unit) {
        audioEngine.speak("Welcome to KK Kids! Tap any Bento box to play! 🦁")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBF0)) // Bento Cream Yellow Canvas
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "KK Kids 🦁",
                starsCount = totalStars,
                onBackClick = null,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Mascot Welcome Banner
            KkLionMascot(
                state = MascotState.HAPPY,
                speechBubbleText = "Welcome! Let's explore learning games! 🎉",
                onClick = { audioEngine.speak("Welcome to KK Kids! Choose a fun game below!") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("All", "Letters", "Numbers", "Puzzles", "Adventures").forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color(0xFFEA580C) else Color.White)
                            .clickable {
                                selectedCategory = cat
                                audioEngine.speak("$cat games!")
                            }
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color(0xFF475569)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bento Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(
                    items = filteredGames,
                    span = { item -> if (item.isSpanFull) GridItemSpan(2) else GridItemSpan(1) }
                ) { game ->
                    val gradientBrush = Brush.linearGradient(
                        colors = listOf(Color(game.startColorHex), Color(game.endColorHex))
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (game.isSpanFull) 120.dp else 125.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(brush = gradientBrush)
                            .clickable {
                                audioEngine.speak(game.title)
                                onNavigateToGame(game.route)
                            }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (game.isSpanFull) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White.copy(alpha = 0.25f))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "FEATURED ADVENTURE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }

                                Text(
                                    text = game.title,
                                    fontSize = if (game.isSpanFull) 20.sp else 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    lineHeight = 18.sp
                                )

                                Text(
                                    text = game.subtitle,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }

                            Text(
                                text = game.emoji,
                                fontSize = if (game.isSpanFull) 48.sp else 36.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
