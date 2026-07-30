package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.ui.components.KkHeader
import com.example.ui.components.KkLionMascot
import com.example.ui.components.MascotState

@Composable
fun RewardsScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    val totalStars by remember { mutableIntStateOf(repository.getStars()) }
    val coins by remember { mutableIntStateOf(repository.getCoins()) }
    val streak by remember { mutableIntStateOf(repository.getStreak()) }
    val childName = remember { repository.getChildName() }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Rewards & Badges, 1: Certificates, 2: Stickers

    LaunchedEffect(Unit) {
        audioEngine.speak("Welcome to your Rewards Hall! You have earned $totalStars stars and $coins coins! Super star! 🏆")
    }

    val certificates = remember {
        listOf(
            Triple("📜 Alphabet Master Certificate", "Awarded to $childName for mastering Letters A-Z!", Color(0xFFFEF3C7)),
            Triple("🔢 Number Champion Certificate", "Awarded to $childName for counting 1 to 20!", Color(0xFFDBEAFE)),
            Triple("🎤 Pronunciation Star Certificate", "Awarded to $childName for Speaking & Repeat mastery!", Color(0xFFDCFCE7)),
            Triple("🎨 Creative Artist Certificate", "Awarded to $childName for Coloring & Drawing!", Color(0xFFFCE7F3))
        )
    }

    val stickers = remember {
        listOf(
            "🦁" to "Super Lion",
            "🐘" to "Big Elephant",
            "🚀" to "Star Rocket",
            "🦖" to "Dino Explorer",
            "🍦" to "Math Ice Cream",
            "🎨" to "Color Master",
            "🧩" to "Puzzle King",
            "⭐" to "Gold Star"
        )
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
                title = "My Rewards & Badges 🏆",
                starsCount = totalStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Profile & Total Counters Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFD54F))
                                .border(3.dp, Color(0xFFFF9800), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = repository.getAvatarEmoji(), fontSize = 42.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "$childName's Trophy Hall",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFE65100)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFFEF3C7)
                                ) {
                                    Text("⭐ $totalStars Stars", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFFEF08A)
                                ) {
                                    Text("🪙 $coins Coins", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF854D0E), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Text("🔥 $streak Days", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                    }
                }

                // Category Selector Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("🏆 Badges", "📜 Certificates", "🎨 Stickers").forEachIndexed { index, tabName ->
                        Button(
                            onClick = {
                                selectedTab = index
                                audioEngine.speak(tabName)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedTab == index) Color(0xFFFF9800) else Color.White,
                                contentColor = if (selectedTab == index) Color.White else Color(0xFF5D4037)
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Text(tabName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                // Tab Content
                when (selectedTab) {
                    0 -> {
                        // Badges & Achievements Section
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("Earned Achievements 🎖️", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf(
                                        Triple("✏️ Letter Trace Master", "Completed 5 handwriting tracing levels", true),
                                        Triple("🧩 Super Puzzle Solver", "Solved 3 picture puzzle challenges", true),
                                        Triple("🎤 Pronunciation Champ", "Scored 100% in Speaking practice", true),
                                        Triple("🍦 Math Ice Cream Scooper", "Built a 5-scoop math ice cream tower", true),
                                        Triple("🦖 Dino Explorer", "Found all hidden Dino numbers", true)
                                    ).forEach { (title, desc, isUnlocked) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(if (isUnlocked) Color(0xFFFFF7ED) else Color(0xFFF1F5F9))
                                                .clickable { audioEngine.speak("$title! $desc") }
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(title.substringBefore(" "), fontSize = 26.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(title.substringAfter(" "), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                                                Text(desc, fontSize = 12.sp, color = Color(0xFF64748B))
                                            }
                                            Text("✔️ Unlocked", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        // Certificates Section
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            certificates.forEach { (certTitle, certDesc, certBg) ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = certBg),
                                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFF59E0B))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = "👑 OFFICIAL KK KIDS CERTIFICATE 👑", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFFB45309))
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = certTitle, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF78350F))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = certDesc, fontSize = 13.sp, color = Color(0xFF451A03), textAlign = TextAlign.Center)
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = { audioEngine.speak("Congratulations $childName! $certTitle") },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("View & Play Celebration 🎉", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        // Stickers Collection Section
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("Sticker Collection 🎨", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    stickers.take(4).forEach { (emoji, label) ->
                                        Box(
                                            modifier = Modifier
                                                .size(70.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(Color(0xFFFEF3C7))
                                                .clickable { audioEngine.speak("Sticker: $label!") }
                                                .border(2.dp, Color(0xFFF59E0B), RoundedCornerShape(16.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(text = emoji, fontSize = 28.sp)
                                                Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
                                            }
                                        }
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    stickers.drop(4).forEach { (emoji, label) ->
                                        Box(
                                            modifier = Modifier
                                                .size(70.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(Color(0xFFDBEAFE))
                                                .clickable { audioEngine.speak("Sticker: $label!") }
                                                .border(2.dp, Color(0xFF3B82F6), RoundedCornerShape(16.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(text = emoji, fontSize = 28.sp)
                                                Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                KkLionMascot(
                    state = MascotState.CELEBRATE,
                    speechBubbleText = "You are a Super Star Learner, $childName!",
                    onClick = { audioEngine.speak("Keep playing games every day to collect more gold stars and stickers!") }
                )
            }
        }
    }
}
