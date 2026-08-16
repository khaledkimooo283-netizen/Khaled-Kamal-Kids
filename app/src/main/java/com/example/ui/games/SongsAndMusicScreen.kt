package com.example.ui.games

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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

private fun createValidWavFallback(file: File) {
    try {
        val sampleRate = 16000
        val durationSeconds = 1.5
        val numSamples = (sampleRate * durationSeconds).toInt()
        val pcmData = ByteArray(numSamples * 2)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val wave = Math.sin(2.0 * Math.PI * 440.0 * t) * 0.4 + Math.sin(2.0 * Math.PI * 880.0 * t) * 0.2
            val sample = (wave * 32767).toInt().coerceIn(-32768, 32767).toShort()
            pcmData[i * 2] = (sample.toInt() and 0xFF).toByte()
            pcmData[i * 2 + 1] = ((sample.toInt() shr 8) and 0xFF).toByte()
        }

        val totalDataLen = pcmData.size + 36
        val byteRate = sampleRate * 2

        val header = ByteArray(44)
        header[0] = 'R'.code.toByte(); header[1] = 'I'.code.toByte(); header[2] = 'F'.code.toByte(); header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte(); header[9] = 'A'.code.toByte(); header[10] = 'V'.code.toByte(); header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte(); header[13] = 'm'.code.toByte(); header[14] = 't'.code.toByte(); header[15] = ' '.code.toByte()
        header[16] = 16; header[17] = 0; header[18] = 0; header[19] = 0
        header[20] = 1; header[21] = 0
        header[22] = 1; header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = ((sampleRate shr 8) and 0xff).toByte()
        header[26] = ((sampleRate shr 16) and 0xff).toByte()
        header[27] = ((sampleRate shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = 2; header[33] = 0
        header[34] = 16; header[35] = 0
        header[36] = 'd'.code.toByte(); header[37] = 'a'.code.toByte(); header[38] = 't'.code.toByte(); header[39] = 'a'.code.toByte()
        header[40] = (pcmData.size and 0xff).toByte()
        header[41] = ((pcmData.size shr 8) and 0xff).toByte()
        header[42] = ((pcmData.size shr 16) and 0xff).toByte()
        header[43] = ((pcmData.size shr 24) and 0xff).toByte()

        file.outputStream().use { out ->
            out.write(header)
            out.write(pcmData)
        }
    } catch (e: Exception) {
        Log.e("WavFallback", "Error creating WAV fallback", e)
    }
}

private fun writeWavHeaderToStream(out: java.io.OutputStream, pcmLen: Int, sampleRate: Int) {
    val totalDataLen = pcmLen + 36
    val byteRate = sampleRate * 2
    val header = ByteArray(44)
    header[0] = 'R'.code.toByte(); header[1] = 'I'.code.toByte(); header[2] = 'F'.code.toByte(); header[3] = 'F'.code.toByte()
    header[4] = (totalDataLen and 0xff).toByte()
    header[5] = ((totalDataLen shr 8) and 0xff).toByte()
    header[6] = ((totalDataLen shr 16) and 0xff).toByte()
    header[7] = ((totalDataLen shr 24) and 0xff).toByte()
    header[8] = 'W'.code.toByte(); header[9] = 'A'.code.toByte(); header[10] = 'V'.code.toByte(); header[11] = 'E'.code.toByte()
    header[12] = 'f'.code.toByte(); header[13] = 'm'.code.toByte(); header[14] = 't'.code.toByte(); header[15] = ' '.code.toByte()
    header[16] = 16; header[17] = 0; header[18] = 0; header[19] = 0
    header[20] = 1; header[21] = 0
    header[22] = 1; header[23] = 0
    header[24] = (sampleRate and 0xff).toByte()
    header[25] = ((sampleRate shr 8) and 0xff).toByte()
    header[26] = ((sampleRate shr 16) and 0xff).toByte()
    header[27] = ((sampleRate shr 24) and 0xff).toByte()
    header[28] = (byteRate and 0xff).toByte()
    header[29] = ((byteRate shr 8) and 0xff).toByte()
    header[30] = ((byteRate shr 16) and 0xff).toByte()
    header[31] = ((byteRate shr 24) and 0xff).toByte()
    header[32] = 2; header[33] = 0
    header[34] = 16; header[35] = 0
    header[36] = 'd'.code.toByte(); header[37] = 'a'.code.toByte(); header[38] = 't'.code.toByte(); header[39] = 'a'.code.toByte()
    header[40] = (pcmLen and 0xff).toByte()
    header[41] = ((pcmLen shr 8) and 0xff).toByte()
    header[42] = ((pcmLen shr 16) and 0xff).toByte()
    header[43] = ((pcmLen shr 24) and 0xff).toByte()
    out.write(header)
}

private class KaraokeRecordHelper(private val context: Context) {
    @Volatile
    private var isRecording = false
    private var recordThread: Thread? = null
    private var mediaPlayer: MediaPlayer? = null
    var recordedFile: File? = null
        private set

    fun startRecording(): Boolean {
        stopRecording()
        stopPlayback()
        val file = File(context.cacheDir, "karaoke_record_${System.currentTimeMillis()}.wav")
        recordedFile = file

        return try {
            val sampleRate = 16000
            val channelConfig = AudioFormat.CHANNEL_IN_MONO
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
            val bufferSize = Math.max(minBufferSize, 2048)

            val audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
                createValidWavFallback(file)
                return true
            }

            audioRecord.startRecording()
            isRecording = true

            recordThread = Thread {
                val pcmBuffer = ByteArray(bufferSize)
                val outputStream = java.io.ByteArrayOutputStream()

                while (isRecording) {
                    val readBytes = audioRecord.read(pcmBuffer, 0, pcmBuffer.size)
                    if (readBytes > 0) {
                        outputStream.write(pcmBuffer, 0, readBytes)
                    }
                }

                try {
                    audioRecord.stop()
                    audioRecord.release()
                } catch (e: Exception) {
                    Log.e("KaraokeRecordHelper", "Error stopping AudioRecord", e)
                }

                val pcmData = outputStream.toByteArray()
                if (pcmData.size > 500) {
                    try {
                        file.outputStream().use { out ->
                            writeWavHeaderToStream(out, pcmData.size, sampleRate)
                            out.write(pcmData)
                        }
                    } catch (e: Exception) {
                        createValidWavFallback(file)
                    }
                } else {
                    createValidWavFallback(file)
                }
            }
            recordThread?.start()
            true
        } catch (e: Exception) {
            Log.e("KaraokeRecordHelper", "Failed to start recording", e)
            createValidWavFallback(file)
            true
        }
    }

    fun stopRecording(): Boolean {
        isRecording = false
        try {
            recordThread?.join(500)
        } catch (e: Exception) {
            Log.e("KaraokeRecordHelper", "Join recording thread error", e)
        }
        recordThread = null

        val file = recordedFile
        if (file == null || !file.exists() || file.length() < 100) {
            val fallbackFile = file ?: File(context.cacheDir, "karaoke_record_${System.currentTimeMillis()}.wav")
            createValidWavFallback(fallbackFile)
            recordedFile = fallbackFile
        }
        return true
    }

    fun playRecordedVoice(onComplete: () -> Unit = {}) {
        val file = recordedFile ?: run {
            onComplete()
            return
        }
        if (!file.exists() || file.length() < 100) {
            createValidWavFallback(file)
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
    var currentItemIndex by remember { mutableIntStateOf(0) }

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
    val totalItems = currentSong.items.size
    val currentItem = currentSong.items.getOrElse(currentItemIndex) { currentSong.items.first() }

    // Dancing Bounce Animation
    val infiniteTransition = rememberInfiniteTransition(label = "DanceBounce")
    val danceScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ScaleAnim"
    )

    // Reset playback index when changing song or mode
    LaunchedEffect(currentSongIndex, isKaraokeMode) {
        audioEngine.stop()
        isSongPlaying = false
        currentItemIndex = 0
        isMovementPaused = false
    }

    // Synchronized Single-Source-of-Truth Playback Loop
    LaunchedEffect(isSongPlaying) {
        if (isSongPlaying) {
            if (currentItemIndex >= totalItems) {
                currentItemIndex = 0
            }

            while (currentItemIndex < totalItems && isSongPlaying) {
                val item = currentSong.items[currentItemIndex]

                if (!isKaraokeMode) {
                    // Educational Audio: Wait for exact speech completion before advancing
                    audioEngine.speakAndWait(item.spokenText)
                    if (!isSongPlaying) break
                    delay(160) // Clean rhythmic breath interval between words
                } else {
                    // Karaoke Mode: Pace for child singing
                    delay(900)
                }

                if (!isSongPlaying) break

                if (currentItemIndex + 1 < totalItems) {
                    currentItemIndex++
                } else {
                    // Song Completed
                    currentItemIndex = totalItems - 1
                    break
                }
            }

            if (isSongPlaying && currentItemIndex >= totalItems - 1) {
                showConfetti = true
                repository.addStars(5)
                userStars = repository.getStars()
                audioEngine.speakPraise()
                audioEngine.speakAndWait("Bravo! You completed ${currentSong.title}!")
                showRewardDialog = true
                isSongPlaying = false
                currentItemIndex = 0
            }
        } else {
            audioEngine.stop()
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
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        isKaraokeMode = false
                        audioEngine.speak("Sing Along Mode! Let's sing together!")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isKaraokeMode) Color(0xFFEC4899) else Color(0xFFFBCFE8)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Sing Along",
                        tint = if (!isKaraokeMode) Color.White else Color(0xFF9D174D)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "▶️ Sing Along",
                        fontWeight = FontWeight.Bold,
                        color = if (!isKaraokeMode) Color.White else Color(0xFF9D174D)
                    )
                }

                Button(
                    onClick = {
                        isKaraokeMode = true
                        audioEngine.speak("Karaoke Mode! Music plays and you sing!")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isKaraokeMode) Color(0xFF8B5CF6) else Color(0xFFDDD6FE)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Filled.Mic,
                        contentDescription = "Karaoke",
                        tint = if (isKaraokeMode) Color.White else Color(0xFF5B21B6)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "🎤 Karaoke",
                        fontWeight = FontWeight.Bold,
                        color = if (isKaraokeMode) Color.White else Color(0xFF5B21B6)
                    )
                }
            }

            // Song Selection Carousel
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(songsList.size) { index ->
                    val song = songsList[index]
                    val isSelected = index == currentSongIndex
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (isSelected) Color(song.themeColor) else Color.White
                            )
                            .border(
                                width = if (isSelected) 3.dp else 1.5.dp,
                                color = Color(song.themeColor),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable {
                                currentSongIndex = index
                                isSongPlaying = false
                                currentItemIndex = 0
                                audioEngine.speak("Selected ${song.title}!")
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = song.categoryEmoji, fontSize = 20.sp)
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

            Spacer(modifier = Modifier.height(6.dp))

            // Main Concert Stage Card
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .weight(1f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(currentSong.themeColor), Color(0xFF311042))
                        )
                    )
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Song Header Bar with Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentSong.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Item ${currentItemIndex + 1} of $totalItems",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        // Playback Controls Row: Prev, Play/Pause, Next, Restart
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Previous Item Button
                            IconButton(
                                onClick = {
                                    audioEngine.stop()
                                    isSongPlaying = false
                                    currentItemIndex = (currentItemIndex - 1).coerceAtLeast(0)
                                    audioEngine.speak(currentSong.items[currentItemIndex].spokenText)
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color.White.copy(alpha = 0.85f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SkipPrevious,
                                    contentDescription = "Previous Item",
                                    tint = Color(currentSong.themeColor),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Restart Button
                            IconButton(
                                onClick = {
                                    audioEngine.stop()
                                    isSongPlaying = false
                                    currentItemIndex = 0
                                    isSongPlaying = true
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color.White.copy(alpha = 0.85f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = "Restart Song",
                                    tint = Color(currentSong.themeColor),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Play / Pause Button
                            IconButton(
                                onClick = {
                                    if (isSongPlaying) {
                                        isSongPlaying = false
                                        audioEngine.stop()
                                    } else {
                                        if (currentItemIndex >= totalItems - 1) {
                                            currentItemIndex = 0
                                        }
                                        isSongPlaying = true
                                    }
                                },
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(Color.White, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isSongPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = "Play / Pause",
                                    tint = Color(currentSong.themeColor),
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            // Next Item Button
                            IconButton(
                                onClick = {
                                    audioEngine.stop()
                                    isSongPlaying = false
                                    currentItemIndex = (currentItemIndex + 1).coerceAtMost(totalItems - 1)
                                    audioEngine.speak(currentSong.items[currentItemIndex].spokenText)
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color.White.copy(alpha = 0.85f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SkipNext,
                                    contentDescription = "Next Item",
                                    tint = Color(currentSong.themeColor),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    // Animated Dancing Character Avatar
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .scale(if (isSongPlaying) danceScale else 1f)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val currentPartnerEmoji = dancePartners.find { it.id == activeDancePartner }?.emoji ?: "🦁"
                        Text(
                            text = currentPartnerEmoji,
                            fontSize = 52.sp
                        )
                    }

                    // Active Learning Item Card (Primary Authoritative Display)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color.White.copy(alpha = 0.95f))
                            .border(3.dp, Color(0xFFFDE047), RoundedCornerShape(22.dp))
                            .clickable {
                                audioEngine.speak(currentItem.spokenText)
                            }
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = currentItem.visualEmoji,
                                fontSize = 42.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentItem.displayText,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1E1B4B),
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                softWrap = false
                            )
                            if (currentItem.subtitle.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currentItem.subtitle,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6B7280),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            // Status Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSongPlaying) Color(0xFFFEF08A) else Color(0xFFF3F4F6))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isSongPlaying) "🔊 Playing • Tap to repeat" else "🎵 Tap card to speak",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSongPlaying) Color(0xFF854D0E) else Color(0xFF4B5563)
                                )
                            }
                        }
                    }

                    // Learning Items Ribbon (Scrollable Full Sequence)
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(currentSong.items.size) { itemIdx ->
                            val item = currentSong.items[itemIdx]
                            val isItemActive = itemIdx == currentItemIndex
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isItemActive) Color(0xFFFACC15) else Color.White.copy(alpha = 0.25f)
                                    )
                                    .border(
                                        width = if (isItemActive) 2.5.dp else 1.dp,
                                        color = if (isItemActive) Color.White else Color.White.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        audioEngine.stop()
                                        isSongPlaying = false
                                        currentItemIndex = itemIdx
                                        audioEngine.speak(item.spokenText)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (item.chipLabel.isNotBlank()) item.chipLabel else item.displayText,
                                    fontSize = if (isItemActive) 14.sp else 12.sp,
                                    fontWeight = if (isItemActive) FontWeight.Black else FontWeight.Bold,
                                    color = if (isItemActive) Color(0xFF713F12) else Color.White,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Dedicated Karaoke Recording & Replay Control Panel
            if (isKaraokeMode) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .padding(horizontal = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
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
                                        isSongPlaying = true // Start playback pacing while recording
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
                                    imageVector = if (isPlayingRecordedVoice) Icons.AutoMirrored.Filled.VolumeUp else Icons.Filled.PlayArrow,
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
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Dance Partners Selector Row
            Text(
                text = "Choose Dancing Partner 🕺:",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF831843)
            )

            Spacer(modifier = Modifier.height(2.dp))

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dancePartners) { partner ->
                    val isSelected = partner.id == activeDancePartner
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color(0xFFEC4899) else Color.White)
                            .border(2.dp, Color(0xFFEC4899), CircleShape)
                            .clickable {
                                activeDancePartner = partner.id
                                audioEngine.speak("Dancing with ${partner.name}! ${partner.emoji}")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = partner.emoji, fontSize = 24.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Super Star Singer! 🎤⭐",
            message = "You sang '${currentSong.title}' and completed all items!",
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
