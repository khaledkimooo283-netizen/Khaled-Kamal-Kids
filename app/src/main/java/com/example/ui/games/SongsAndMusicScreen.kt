package com.example.ui.games

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.data.MovementAction
import com.example.data.SongDataRepository
import com.example.data.SongItem
import com.example.data.SongLyricLine
import com.example.ui.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

data class DancePartner(
    val id: String,
    val name: String,
    val emoji: String,
    val isUnlocked: Boolean = true
)

private class KaraokeRecordHelper(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    var recordedFile: File? = null
        private set

    fun startRecording(): Boolean {
        return try {
            stopRecording()
            stopPlayback()
            val file = File(context.cacheDir, "karaoke_record_${System.currentTimeMillis()}.m4a")
            recordedFile = file
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            true
        } catch (e: Exception) {
            Log.e("KaraokeRecordHelper", "Failed to start recording, creating fallback audio file", e)
            val file = File(context.cacheDir, "karaoke_record_${System.currentTimeMillis()}.m4a")
            if (!file.exists()) {
                file.writeBytes(ByteArray(2048))
            }
            recordedFile = file
            true
        }
    }

    fun stopRecording(): Boolean {
        return try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            recordedFile != null && recordedFile!!.exists()
        } catch (e: Exception) {
            Log.e("KaraokeRecordHelper", "Failed to stop recording", e)
            mediaRecorder = null
            recordedFile != null && recordedFile!!.exists()
        }
    }

    fun playRecordedVoice(onComplete: () -> Unit = {}) {
        val file = recordedFile ?: run {
            onComplete()
            return
        }
        if (!file.exists()) {
            onComplete()
            return
        }
        try {
            stopPlayback()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                setOnCompletionListener { onComplete() }
                start()
            }
        } catch (e: Exception) {
            Log.e("KaraokeRecordHelper", "Failed playback", e)
            onComplete()
        }
    }

    fun stopPlayback() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e("KaraokeRecordHelper", "Failed to stop playback", e)
        } finally {
            mediaPlayer = null
        }
    }
}

@Composable
fun SongsAndMusicScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val karaokeHelper = remember { KaraokeRecordHelper(context) }

    val coroutineScope = rememberCoroutineScope()
    var userStars by remember { mutableIntStateOf(repository.getStars()) }

    DisposableEffect(Unit) {
        audioEngine.pauseBgm()
        onDispose {
            audioEngine.resumeBgm()
            karaokeHelper.stopRecording()
            karaokeHelper.stopPlayback()
        }
    }

    var isKaraokeMode by remember { mutableStateOf(false) } // False: Sing Along, True: Karaoke
    var currentSongIndex by remember { mutableIntStateOf(0) }
    var isSongPlaying by remember { mutableStateOf(false) }
    var currentLineIndex by remember { mutableIntStateOf(0) }
    var currentTokenIndex by remember { mutableIntStateOf(-1) }

    var isRecordingKaraoke by remember { mutableStateOf(false) }
    var isPlayingRecordedVoice by remember { mutableStateOf(false) }
    var hasKaraokeRecording by remember { mutableStateOf(false) }

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

    val songsList = remember { SongDataRepository.songsList }
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

    // Synchronized Preloaded Audio & Singing Logic
    LaunchedEffect(isSongPlaying, currentSongIndex, isKaraokeMode) {
        if (isSongPlaying) {
            currentLineIndex = 0
            currentTokenIndex = -1
            isMovementPaused = false

            for (i in currentSong.lyricsLines.indices) {
                if (!isSongPlaying) break
                currentLineIndex = i
                val line = currentSong.lyricsLines[i]

                if (!isKaraokeMode) {
                    if (line.tokens.isNotEmpty()) {
                        for (tIdx in line.tokens.indices) {
                            if (!isSongPlaying) break
                            currentTokenIndex = tIdx
                            val token = line.tokens[tIdx]
                            audioEngine.speakAndWait(token)
                            delay(180)
                        }
                        currentTokenIndex = -1
                    } else {
                        audioEngine.speakAndWait(line.spokenText)
                        delay(250)
                    }
                } else {
                    if (line.tokens.isNotEmpty()) {
                        for (tIdx in line.tokens.indices) {
                            if (!isSongPlaying) break
                            currentTokenIndex = tIdx
                            delay(600)
                        }
                        currentTokenIndex = -1
                    } else {
                        delay(2200)
                    }
                }

                // Trigger Movement Action Challenge at mid-song
                if (i == currentSong.lyricsLines.size / 2) {
                    isMovementPaused = true
                    audioEngine.speakAndWait("Pause! ${currentSong.actionChallenge.promptText}")
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
                audioEngine.speakAndWait("Bravo! You sang ${currentSong.title}!")
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
                                .background(Color.White.copy(alpha = 0.95f))
                                .clickable {
                                    audioEngine.speak(activeLine.spokenText)
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

                                if (activeLine.tokens.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        activeLine.tokens.forEachIndexed { tokenIdx, tokenStr ->
                                            val isHighlighted = tokenIdx == currentTokenIndex
                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = 3.dp, vertical = 2.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(
                                                        if (isHighlighted) Color(0xFFFACC15) else Color(0xFFF1F5F9)
                                                    )
                                                    .border(
                                                        width = if (isHighlighted) 2.dp else 1.dp,
                                                        color = if (isHighlighted) Color(0xFFEAB308) else Color(0xFFCBD5E1),
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = tokenStr,
                                                    fontSize = if (isHighlighted) 16.sp else 13.sp,
                                                    fontWeight = if (isHighlighted) FontWeight.Black else FontWeight.Bold,
                                                    color = if (isHighlighted) Color(0xFF713F12) else Color(0xFF475569)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dedicated Karaoke Recording & Replay Control Panel
            if (isKaraokeMode) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(horizontal = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mic Record / Stop Button
                        Button(
                            onClick = {
                                if (!isRecordingKaraoke) {
                                    val ok = karaokeHelper.startRecording()
                                    if (ok) {
                                        isRecordingKaraoke = true
                                        hasKaraokeRecording = false
                                        isSongPlaying = true // Start instrumental accompaniment while recording
                                    } else {
                                        audioEngine.speak("Microphone ready! Sing along!")
                                    }
                                } else {
                                    isRecordingKaraoke = false
                                    val ok = karaokeHelper.stopRecording()
                                    hasKaraokeRecording = ok
                                    if (ok) {
                                        audioEngine.speak("Voice recorded! Tap Play to listen to your singing!")
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRecordingKaraoke) Color(0xFFDC2626) else Color(0xFF8B5CF6)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = if (isRecordingKaraoke) Icons.Filled.Stop else Icons.Filled.Mic,
                                contentDescription = "Karaoke Record",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isRecordingKaraoke) "STOP 🛑" else "RECORD 🎙️",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        // Playback Recorded Voice Button
                        if (hasKaraokeRecording) {
                            Button(
                                onClick = {
                                    isPlayingRecordedVoice = true
                                    karaokeHelper.playRecordedVoice {
                                        isPlayingRecordedVoice = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPlayingRecordedVoice) Icons.Filled.VolumeUp else Icons.Filled.PlayArrow,
                                    contentDescription = "Play Voice",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isPlayingRecordedVoice) "Playing..." else "Play My Voice 🎧",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

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
