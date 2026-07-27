package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import kotlinx.coroutines.launch

data class SongItem(
    val id: String,
    val title: String,
    val categoryEmoji: String,
    val themeColor: Long,
    val lyricsLines: List<SongLyricLine>,
    val actionChallenge: MovementAction
)

data class SongLyricLine(
    val lineText: String,
    val highlightEmoji: String,
    val wordPrompt: String
)

data class MovementAction(
    val promptText: String,
    val actionEmoji: String,
    val targetType: String // "CLAP", "JUMP", "RAISE_HANDS", "WAVE", "SPIN"
)

data class DancePartner(
    val id: String,
    val name: String,
    val emoji: String,
    val isUnlocked: Boolean = true
)

@Composable
fun SongsAndMusicScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var userStars by remember { mutableIntStateOf(repository.getStars()) }

    var isKaraokeMode by remember { mutableStateOf(false) } // False: Sing Along, True: Karaoke
    var currentSongIndex by remember { mutableIntStateOf(0) }
    var isSongPlaying by remember { mutableStateOf(false) }
    var currentLineIndex by remember { mutableIntStateOf(0) }

    var isMovementPaused by remember { mutableStateOf(false) }
    var activeDancePartner by remember { mutableStateOf("lion") }

    var showConfetti by remember { mutableStateOf(false) }
    var showRewardDialog by remember { mutableStateOf(false) }

    val dancePartners = remember {
        listOf(
            DancePartner("lion", "KK Lion", "🦁"),
            DancePartner("elephant", "Ellie Elephant", "🐘"),
            DancePartner("monkey", "Milo Monkey", "🐵"),
            DancePartner("dog", "Duke Dog", "🐶"),
            DancePartner("panda", "Penny Panda", "🐼"),
            DancePartner("penguin", "Pipo Penguin", "🐧")
        )
    }

    val songsList = remember {
        listOf(
            SongItem(
                id = "s_abc",
                title = "Alphabet Song",
                categoryEmoji = "🔤",
                themeColor = 0xFFEC4899,
                lyricsLines = listOf(
                    SongLyricLine("A - B - C - D - E - F - G", "🅰️", "A B C"),
                    SongLyricLine("H - I - J - K - L - M - N - O - P", "🔤", "H I J K"),
                    SongLyricLine("Q - R - S, T - U - V", "🍎", "Q R S T"),
                    SongLyricLine("W - X - Y and Z!", "🌟", "W X Y Z"),
                    SongLyricLine("Now I know my ABCs!", "🎉", "Now I know my ABCs"),
                    SongLyricLine("Next time won't you sing with me!", "🎵", "Sing with me!")
                ),
                actionChallenge = MovementAction("👏 Clap your hands 3 times!", "👏", "CLAP")
            ),
            SongItem(
                id = "s_num",
                title = "Numbers 1 to 10 Song",
                categoryEmoji = "🔢",
                themeColor = 0xFF3B82F6,
                lyricsLines = listOf(
                    SongLyricLine("1, 2, 3... Count with me!", "1️⃣", "One, Two, Three"),
                    SongLyricLine("4, 5, 6... Fun and quick!", "4️⃣", "Four, Five, Six"),
                    SongLyricLine("7, 8, 9... Feeling fine!", "7️⃣", "Seven, Eight, Nine"),
                    SongLyricLine("Number 10! Let's count again!", "🔟", "Number Ten!")
                ),
                actionChallenge = MovementAction("👣 Jump up and down 2 times!", "👣", "JUMP")
            ),
            SongItem(
                id = "s_col",
                title = "Rainbow Colors Song",
                categoryEmoji = "🎨",
                themeColor = 0xFF10B981,
                lyricsLines = listOf(
                    SongLyricLine("Red is Apple, Sweet and round!", "🍎", "Red Apple"),
                    SongLyricLine("Yellow Sun high off the ground!", "☀️", "Yellow Sun"),
                    SongLyricLine("Blue Sky flying high above!", "☁️", "Blue Sky"),
                    SongLyricLine("Green Leaf growing full of love!", "🍃", "Green Leaf")
                ),
                actionChallenge = MovementAction("🙌 Raise your hands high in the air!", "🙌", "RAISE_HANDS")
            ),
            SongItem(
                id = "s_ani",
                title = "Animal Friends Song",
                categoryEmoji = "🦁",
                themeColor = 0xFFF59E0B,
                lyricsLines = listOf(
                    SongLyricLine("The Lion roars... ROAR ROAR!", "🦁", "Lion Roars"),
                    SongLyricLine("The Dog barks... WOOF WOOF!", "🐶", "Dog Barks"),
                    SongLyricLine("The Cat meows... MEOW MEOW!", "🐱", "Cat Meows"),
                    SongLyricLine("The Duck quacks... QUACK QUACK!", "🦆", "Duck Quacks")
                ),
                actionChallenge = MovementAction("👋 Wave hello to your animal friends!", "👋", "WAVE")
            )
        )
    }

    val currentSong = songsList[currentSongIndex]

    // Dancing Bounce Animation
    val infiniteTransition = rememberInfiniteTransition(label = "DanceBounce")
    val danceScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ScaleAnim"
    )

    // Dynamic Song Singing Logic
    LaunchedEffect(isSongPlaying, currentSongIndex, isKaraokeMode) {
        if (isSongPlaying) {
            currentLineIndex = 0
            isMovementPaused = false

            for (i in currentSong.lyricsLines.indices) {
                if (!isSongPlaying) break
                currentLineIndex = i
                val line = currentSong.lyricsLines[i]

                if (!isKaraokeMode) {
                    audioEngine.speak(line.lineText)
                }

                delay(2800)

                // Trigger Movement Action Challenge at mid-song
                if (i == currentSong.lyricsLines.size / 2) {
                    isMovementPaused = true
                    audioEngine.speak("Pause! ${currentSong.actionChallenge.promptText}")
                    while (isMovementPaused && isSongPlaying) {
                        delay(300)
                    }
                }
            }

            if (isSongPlaying && !isMovementPaused) {
                showConfetti = true
                repository.addStars(5)
                userStars = repository.getStars()
                audioEngine.speakPraise()
                audioEngine.speak("Bravo! You sang ${currentSong.title}!")
                showRewardDialog = true
                isSongPlaying = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF2F8)) // Warm Soft Magenta Stage
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Songs & Music 🎵",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Mode Toggle: Sing Along vs. Karaoke
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        isKaraokeMode = false
                        audioEngine.speak("Sing Along Mode! Let's sing together!")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isKaraokeMode) Color(0xFFEC4899) else Color(0xFFFBCFE8)
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.VolumeUp, contentDescription = "Sing Along", tint = if (!isKaraokeMode) Color.White else Color(0xFF9D174D))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("▶️ Sing Along", fontWeight = FontWeight.Bold, color = if (!isKaraokeMode) Color.White else Color(0xFF9D174D))
                }

                Button(
                    onClick = {
                        isKaraokeMode = true
                        audioEngine.speak("Karaoke Mode! Music plays and you sing!")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isKaraokeMode) Color(0xFF8B5CF6) else Color(0xFFDDD6FE)
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Mic, contentDescription = "Karaoke", tint = if (isKaraokeMode) Color.White else Color(0xFF5B21B6))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("🎤 Karaoke", fontWeight = FontWeight.Bold, color = if (isKaraokeMode) Color.White else Color(0xFF5B21B6))
                }
            }

            // Song Selection Carousel
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(songsList.size) { index ->
                    val song = songsList[index]
                    val isSelected = index == currentSongIndex
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) Color(song.themeColor) else Color.White
                            )
                            .border(
                                width = if (isSelected) 3.dp else 1.5.dp,
                                color = Color(song.themeColor),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                currentSongIndex = index
                                isSongPlaying = false
                                audioEngine.speak("Selected ${song.title}!")
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = song.categoryEmoji, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = song.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) Color.White else Color(0xFF334155)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Concert Stage Card
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .weight(1f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(currentSong.themeColor), Color(0xFF4C1D95))
                        )
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Song Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentSong.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )

                        IconButton(
                            onClick = {
                                isSongPlaying = !isSongPlaying
                                if (isSongPlaying) {
                                    audioEngine.speak("Let's sing ${currentSong.title}!")
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isSongPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = "Play Control",
                                tint = Color(currentSong.themeColor),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    // Animated Dancing Character Avatar Stage
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .scale(if (isSongPlaying) danceScale else 1f)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val currentPartnerEmoji = dancePartners.find { it.id == activeDancePartner }?.emoji ?: "🦁"
                        Text(
                            text = currentPartnerEmoji,
                            fontSize = 64.sp
                        )
                    }

                    // Active Lyrics Line Card or Movement Challenge Overlay
                    if (isMovementPaused) {
                        // Movement Challenge Interruption!
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFFEF08A))
                                .border(3.dp, Color(0xFFEAB308), RoundedCornerShape(20.dp))
                                .clickable {
                                    isMovementPaused = false
                                    audioEngine.speakPraise()
                                    audioEngine.speak("Awesome movement! Continuing song!")
                                }
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = currentSong.actionChallenge.actionEmoji, fontSize = 40.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentSong.actionChallenge.promptText,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF854D0E),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Tap to Complete Action! ⭐",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFCA8A04)
                                )
                            }
                        }
                    } else {
                        // Current Lyric Display
                        val activeLine = currentSong.lyricsLines.getOrElse(currentLineIndex) { currentSong.lyricsLines[0] }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.92f))
                                .clickable {
                                    audioEngine.speak(activeLine.lineText)
                                }
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = activeLine.highlightEmoji,
                                    fontSize = 38.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = activeLine.lineText,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1E1B4B),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dance Partners Selector Row
            Text(
                text = "Choose Dancing Partner 🕺:",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF831843)
            )

            Spacer(modifier = Modifier.height(4.dp))

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(dancePartners) { partner ->
                    val isSelected = partner.id == activeDancePartner
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color(0xFFEC4899) else Color.White)
                            .border(2.dp, Color(0xFFEC4899), CircleShape)
                            .clickable {
                                activeDancePartner = partner.id
                                audioEngine.speak("Dancing with ${partner.name}! ${partner.emoji}")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = partner.emoji, fontSize = 28.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Super Star Singer! 🎤⭐",
            message = "You sang '${currentSong.title}' and completed all dance moves!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                currentSongIndex = (currentSongIndex + 1) % songsList.size
            },
            onHome = {
                showRewardDialog = false
                showConfetti = false
                onBackClick()
            }
        )
    }
}
