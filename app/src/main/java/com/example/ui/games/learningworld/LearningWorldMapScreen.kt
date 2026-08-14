package com.example.ui.games.learningworld

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.data.LearningWorldData
import com.example.data.WorldEnvironment

@Composable
fun LearningWorldMapScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onSelectWorld: (WorldEnvironment) -> Unit,
    onBackClick: () -> Unit
) {
    val childName = repository.getChildName()
    val totalStars = repository.getStars()
    val totalCoins = repository.coinsState.intValue

    var isParentUnlockAll by remember {
        mutableStateOf(repository.isParentUnlockAllWorldsEnabled())
    }
    val unlockedIdx = remember(isParentUnlockAll) {
        repository.getLearningWorldUnlockedIndex()
    }

    val worlds = remember { LearningWorldData.worlds }

    val welcomePhrase = remember {
        "Hi $childName! Welcome to Leo's Learning World! Where would you like to explore today?"
    }

    LaunchedEffect(Unit) {
        audioEngine.speak(welcomePhrase)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE0F2FE), // Soft Blue Sky
                        Color(0xFFF0FDF4), // Gentle Meadow
                        Color(0xFFFEF3C7)  // Warm Gold
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        audioEngine.playClickSound()
                        onBackClick()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.5.dp, Color(0xFFCBD5E1), CircleShape)
                        .testTag("world_map_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF1E293B)
                    )
                }

                // Title Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🗺️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Leo's Learning World",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                    }
                }

                // Stars & Coins Counter
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFEF3C7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⭐", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "$totalStars",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFEF08A),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAB308))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🪙", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "$totalCoins",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF854D0E)
                            )
                        }
                    }
                }
            }

            // Leo Companion Top Greeting
            LeoCompanion(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                pose = LeoPose.HAPPY,
                speechText = "Choose an environment to explore!",
                onLeoClick = {
                    audioEngine.playClickSound()
                    audioEngine.speak(welcomePhrase)
                },
                onSpeakClick = {
                    audioEngine.speak(welcomePhrase)
                }
            )

            // Parent Bypass Toggle Switch Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 4.dp)
                    .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔓", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Parent: Unlock All Worlds for Testing",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334155)
                    )
                }
                Switch(
                    checked = isParentUnlockAll,
                    onCheckedChange = { isChecked ->
                        isParentUnlockAll = isChecked
                        repository.setParentUnlockAllWorldsEnabled(isChecked)
                        audioEngine.playClickSound()
                    },
                    modifier = Modifier.scale(0.75f)
                )
            }

            // World Journey Path List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                itemsIndexed(worlds) { index, world ->
                    val isUnlocked = isParentUnlockAll || index <= unlockedIdx
                    val worldStars = repository.getLearningWorldStars(world.id)

                    WorldIslandCard(
                        world = world,
                        index = index + 1,
                        isUnlocked = isUnlocked,
                        stars = worldStars,
                        onClick = {
                            if (isUnlocked) {
                                audioEngine.playClickSound()
                                onSelectWorld(world)
                            } else {
                                audioEngine.playWrongSound()
                                audioEngine.speak("Complete the previous world to unlock ${world.title}!")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WorldIslandCard(
    world: WorldEnvironment,
    index: Int,
    isUnlocked: Boolean,
    stars: Int,
    onClick: () -> Unit
) {
    val cardBrush = if (isUnlocked) {
        Brush.horizontalGradient(
            colors = listOf(
                Color(world.themeColorHex),
                Color(world.accentColorHex)
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF94A3B8),
                Color(0xFFCBD5E1)
            )
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isUnlocked) 6.dp else 2.dp,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { onClick() }
            .testTag("world_card_${world.id}"),
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(cardBrush)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // World Emoji & Number Badge
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .border(2.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = world.emoji,
                        fontSize = 36.sp
                    )
                    // Step number pill
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$index",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Info Section
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = world.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = world.description,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Stars Earned Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        for (i in 1..3) {
                            Text(
                                text = if (i <= stars) "⭐" else "☆",
                                fontSize = 15.sp,
                                color = if (i <= stars) Color(0xFFFFD700) else Color.White.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${world.vocabList.size} Words",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.95f)
                        )
                    }
                }

                // Action Indicator (Play Arrow or Lock)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.95f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isUnlocked) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Enter World",
                            tint = Color(world.themeColorHex),
                            modifier = Modifier.size(28.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked World",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}
