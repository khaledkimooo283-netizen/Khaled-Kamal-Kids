package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
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
import com.example.ui.components.*

data class AdventureWorld(
    val id: Int,
    val name: String,
    val emoji: String,
    val description: String,
    val bgStartColor: Long,
    val bgEndColor: Long,
    val targetLetterOrConcept: String,
    val learnedWords: List<String>,
    val wordEmojis: List<String>
)

@Composable
fun AdventureModeScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var unlockedWorldIdx by remember { mutableIntStateOf(repository.getAdventureUnlockedWorld()) }

    var selectedActiveWorld by remember { mutableStateOf<AdventureWorld?>(null) }
    var currentActivityStep by remember { mutableIntStateOf(0) } // 0: Vocab Card, 1: Mini Match, 2: Complete
    var showCertificateDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    val worlds = remember {
        listOf(
            AdventureWorld(
                id = 0,
                name = "Alphabet Forest",
                emoji = "🌳",
                description = "Master Letters A, B, C",
                bgStartColor = 0xFF4ADE80,
                bgEndColor = 0xFF16A34A,
                targetLetterOrConcept = "A, B, C",
                learnedWords = listOf("Apple", "Ant", "Ball", "Banana", "Cat"),
                wordEmojis = listOf("🍎", "🐜", "⚽", "🍌", "🐱")
            ),
            AdventureWorld(
                id = 1,
                name = "Number Island",
                emoji = "🏝️",
                description = "Count 1 to 5 with sea friends",
                bgStartColor = 0xFF38BDF8,
                bgEndColor = 0xFF0284C7,
                targetLetterOrConcept = "Numbers 1 - 5",
                learnedWords = listOf("One Star", "Two Fish", "Three Crabs", "Four Shells", "Five Boats"),
                wordEmojis = listOf("⭐", "🐟", "🦀", "🐚", "⛵")
            ),
            AdventureWorld(
                id = 2,
                name = "Color Town",
                emoji = "🎨",
                description = "Learn Rainbow Colors",
                bgStartColor = 0xFFF43F5E,
                bgEndColor = 0xFFE11D48,
                targetLetterOrConcept = "Red, Blue, Yellow",
                learnedWords = listOf("Red Apple", "Blue Car", "Yellow Sun", "Green Leaf"),
                wordEmojis = listOf("🍎", "🚗", "☀️", "🍃")
            ),
            AdventureWorld(
                id = 3,
                name = "Animal Valley",
                emoji = "🐶",
                description = "Discover Safari & Farm Friends",
                bgStartColor = 0xFFFACC15,
                bgEndColor = 0xFFCA8A04,
                targetLetterOrConcept = "Animal Sounds & Names",
                learnedWords = listOf("Lion", "Monkey", "Elephant", "Dog", "Cat"),
                wordEmojis = listOf("🦁", "🐒", "🐘", "🐶", "🐱")
            ),
            AdventureWorld(
                id = 4,
                name = "Food Garden",
                emoji = "🍎",
                description = "Healthy Fruit & Yummy Treats",
                bgStartColor = 0xFFFB923C,
                bgEndColor = 0xFFEA580C,
                targetLetterOrConcept = "Delicious English Vocabulary",
                learnedWords = listOf("Orange", "Cookie", "Cupcake", "Milk", "Pizza"),
                wordEmojis = listOf("🍊", "🍪", "🧁", "🥛", "🍕")
            ),
            AdventureWorld(
                id = 5,
                name = "Space World",
                emoji = "🚀",
                description = "Explore Galaxy Letters D to Z",
                bgStartColor = 0xFFA855F7,
                bgEndColor = 0xFF7E22CE,
                targetLetterOrConcept = "Letters D to Z",
                learnedWords = listOf("Dog", "Elephant", "Fish", "Star", "Rocket"),
                wordEmojis = listOf("🐶", "🐘", "🐟", "⭐", "🚀")
            ),
            AdventureWorld(
                id = 6,
                name = "Treasure Castle",
                emoji = "🎁",
                description = "Final Master Challenge!",
                bgStartColor = 0xFFEC4899,
                bgEndColor = 0xFFBE185D,
                targetLetterOrConcept = "Master Explorer Certificate",
                learnedWords = listOf("Gold Crown", "Master Badge", "Explorer Star"),
                wordEmojis = listOf("👑", "🏅", "🌟")
            )
        )
    }

    LaunchedEffect(Unit) {
        audioEngine.speak("Welcome to KK Kids Adventure Mode! Help the Lion find the magical treasure! 🗺️")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF3C7))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "KK Adventure 🗺️",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Story Clue Header
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFD97706))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🦁", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Complete worlds in order to reach the Treasure Castle!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Worlds Map Trail
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                itemsIndexed(worlds) { idx, world ->
                    val isUnlocked = idx <= unlockedWorldIdx
                    val isCompleted = idx < unlockedWorldIdx
                    val gradientBrush = Brush.linearGradient(
                        colors = if (isUnlocked) listOf(Color(world.bgStartColor), Color(world.bgEndColor))
                        else listOf(Color(0xFF94A3B8), Color(0xFF64748B))
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(brush = gradientBrush)
                            .border(
                                width = if (idx == unlockedWorldIdx) 4.dp else 1.5.dp,
                                color = if (idx == unlockedWorldIdx) Color(0xFFF59E0B) else Color.Transparent,
                                shape = RoundedCornerShape(24.dp)
                            )
                            .clickable(enabled = isUnlocked) {
                                selectedActiveWorld = world
                                currentActivityStep = 0
                                audioEngine.speak("${world.name}! ${world.description}")
                            }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = world.emoji, fontSize = 42.sp)

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = world.name,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    if (isCompleted) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = "Completed",
                                            tint = Color(0xFF86EFAC),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = world.description,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Target: ${world.targetLetterOrConcept}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFEF08A)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isUnlocked) Icons.Filled.PlayArrow else Icons.Filled.Lock,
                                    contentDescription = "Status",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Active World Mini-Learning Activity Dialog
        selectedActiveWorld?.let { activeWorld ->
            AlertDialog(
                onDismissRequest = { selectedActiveWorld = null },
                confirmButton = {},
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = activeWorld.emoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = activeWorld.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFB45309)
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Learn new English words in this world:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Word Cards Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            activeWorld.learnedWords.take(3).forEachIndexed { index, word ->
                                val emoji = activeWorld.wordEmojis.getOrElse(index) { "⭐" }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFFFEF3C7))
                                        .clickable {
                                            audioEngine.speak("$word! $emoji")
                                        }
                                        .padding(8.dp)
                                ) {
                                    Text(text = emoji, fontSize = 32.sp)
                                    Text(
                                        text = word,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFD97706)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                repository.addLearnedWords(activeWorld.learnedWords)
                                repository.addStars(10)
                                userStars = repository.getStars()

                                if (activeWorld.id == unlockedWorldIdx) {
                                    repository.unlockNextWorld(activeWorld.id)
                                    unlockedWorldIdx = repository.getAdventureUnlockedWorld()
                                }

                                audioEngine.speakPraise()
                                audioEngine.speak("World completed! You earned 10 stars and learned ${activeWorld.learnedWords.size} new words!")

                                if (activeWorld.id == 6) {
                                    showConfetti = true
                                    showCertificateDialog = true
                                }
                                selectedActiveWorld = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Complete Challenge! ⭐ (+10 Stars)",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(28.dp),
                containerColor = Color.White
            )
        }

        // Final Master Certificate Overlay Dialog
        if (showCertificateDialog) {
            AlertDialog(
                onDismissRequest = { showCertificateDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            showCertificateDialog = false
                            showConfetti = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Yay! Explorer Certificate Received 🏆", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("🏆 MASTER EXPLORER 🏆", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFFD97706))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("KK Kids Adventure Certificate", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                    }
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("🦁", fontSize = 54.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Congratulations!\n\nYou completed all 7 Adventure Worlds, mastered letters A-Z, and learned dozens of English vocabulary words!",
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF475569)
                        )
                    }
                },
                containerColor = Color(0xFFFEF3C7),
                shape = RoundedCornerShape(28.dp)
            )
        }

        ConfettiOverlay(isVisible = showConfetti)
    }
}
