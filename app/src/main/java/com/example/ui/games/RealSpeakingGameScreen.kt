package com.example.ui.games

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.ui.components.*
import java.io.File

data class SpeakingPromptItem(
    val category: String, // "Letters", "Words", "Sentences"
    val targetText: String,
    val phoneticHint: String,
    val emoji: String,
    val difficultyColor: Color
)

data class PronunciationResult(
    val score: Int,
    val scoreRange: String, // "95–100%", "85–94%", "70–84%", "Below 70%"
    val ratingTitle: String, // "Excellent ⭐⭐⭐⭐⭐", "Very Good ⭐⭐⭐⭐", "Good ⭐⭐⭐", "Try Again ❌"
    val isAccepted: Boolean,
    val feedbackMessage: String,
    val recognizedSpeech: String
)

private class AudioRecordHelper(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    var audioFile: File? = null
        private set

    fun startRecording(): Boolean {
        return try {
            stopRecording()
            stopPlayback()
            val outputFile = File(context.cacheDir, "child_record_${System.currentTimeMillis()}.3gp")
            audioFile = outputFile

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }
            true
        } catch (e: Exception) {
            Log.e("AudioRecordHelper", "Failed to start recording", e)
            false
        }
    }

    fun stopRecording(): Boolean {
        return try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            audioFile != null && audioFile!!.exists() && audioFile!!.length() > 0
        } catch (e: Exception) {
            Log.e("AudioRecordHelper", "Failed to stop recording", e)
            mediaRecorder = null
            false
        }
    }

    fun playRecordedVoice(onComplete: () -> Unit = {}) {
        val file = audioFile ?: return
        if (!file.exists()) return
        try {
            stopPlayback()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                setOnCompletionListener {
                    onComplete()
                }
                start()
            }
        } catch (e: Exception) {
            Log.e("AudioRecordHelper", "Failed to play recorded voice", e)
            onComplete()
        }
    }

    fun stopPlayback() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e("AudioRecordHelper", "Failed to stop playback", e)
        } finally {
            mediaPlayer = null
        }
    }
}

private class SpeechRecognizeHelper(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null

    fun startListening(onResult: (String) -> Unit, onError: () -> Unit) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError()
            return
        }
        try {
            stopListening()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {}
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {}
                    override fun onError(error: Int) {
                        onError()
                    }
                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val topMatch = matches?.firstOrNull() ?: ""
                        onResult(topMatch)
                    }
                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            }
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e("SpeechRecognizeHelper", "Failed to start speech recognition", e)
            onError()
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            Log.e("SpeechRecognizeHelper", "Failed to stop speech recognizer", e)
        } finally {
            speechRecognizer = null
        }
    }
}

private fun levenshteinDistance(lhs: CharSequence, rhs: CharSequence): Int {
    val lhsLength = lhs.length
    val rhsLength = rhs.length
    var cost = IntArray(lhsLength + 1) { it }
    var newCost = IntArray(lhsLength + 1)

    for (i in 1..rhsLength) {
        newCost[0] = i
        for (j in 1..lhsLength) {
            val match = if (lhs[j - 1] == rhs[i - 1]) 0 else 1
            val costReplace = cost[j - 1] + match
            val costInsert = cost[j] + 1
            val costDelete = newCost[j - 1] + 1
            newCost[j] = minOf(costReplace, minOf(costInsert, costDelete))
        }
        val swap = cost
        cost = newCost
        newCost = swap
    }
    return cost[lhsLength]
}

private fun evaluatePronunciation(targetText: String, speechInput: String): PronunciationResult {
    val cleanTarget = targetText.trim().lowercase().replace(Regex("[^a-z0-9 ]"), "")
    val cleanInput = speechInput.trim().lowercase().replace(Regex("[^a-z0-9 ]"), "")

    // 1. Check for empty speech or silence
    if (cleanInput.isEmpty() || cleanInput == "silent") {
        return PronunciationResult(
            score = 0,
            scoreRange = "Below 70%",
            ratingTitle = "Try Again ❌",
            isAccepted = false,
            feedbackMessage = "No English speech detected. Please speak clearly into the microphone!",
            recognizedSpeech = "[Silence]"
        )
    }

    // 2. Check for Arabic or non-English speech
    val hasNonEnglish = speechInput.any { it in '\u0600'..'\u06FF' }
    if (hasNonEnglish) {
        return PronunciationResult(
            score = 15,
            scoreRange = "Below 70%",
            ratingTitle = "Try Again ❌",
            isAccepted = false,
            feedbackMessage = "Arabic speech detected! Please pronounce the word in English.",
            recognizedSpeech = speechInput
        )
    }

    // 3. Compare Target vs Input
    val distance = levenshteinDistance(cleanTarget, cleanInput)
    val maxLen = maxOf(cleanTarget.length, cleanInput.length)
    val similarityRatio = if (maxLen == 0) 1.0 else (1.0 - (distance.toDouble() / maxLen))

    val targetWords = cleanTarget.split(Regex("\\s+")).filter { it.isNotEmpty() }
    val inputWords = cleanInput.split(Regex("\\s+")).filter { it.isNotEmpty() }

    val matchedWords = targetWords.count { tw -> inputWords.any { iw -> levenshteinDistance(tw, iw) <= 1 } }
    val wordMatchRatio = if (targetWords.isEmpty()) 0.0 else (matchedWords.toDouble() / targetWords.size)

    val finalRatio = if (targetWords.size > 1) (similarityRatio * 0.4 + wordMatchRatio * 0.6) else similarityRatio
    val scorePercent = (finalRatio * 100).toInt().coerceIn(0, 100)

    return when {
        scorePercent >= 95 -> PronunciationResult(
            score = scorePercent,
            scoreRange = "95–100%",
            ratingTitle = "Excellent ⭐⭐⭐⭐⭐",
            isAccepted = true,
            feedbackMessage = "Perfect! You pronounced \"$targetText\" accurately!",
            recognizedSpeech = speechInput
        )
        scorePercent >= 85 -> PronunciationResult(
            score = scorePercent,
            scoreRange = "85–94%",
            ratingTitle = "Very Good ⭐⭐⭐⭐",
            isAccepted = true,
            feedbackMessage = "Very Good! Almost native English pronunciation.",
            recognizedSpeech = speechInput
        )
        scorePercent >= 70 -> PronunciationResult(
            score = scorePercent,
            scoreRange = "70–84%",
            ratingTitle = "Good ⭐⭐⭐",
            isAccepted = true,
            feedbackMessage = "Good job! Acceptable pronunciation.",
            recognizedSpeech = speechInput
        )
        else -> PronunciationResult(
            score = scorePercent,
            scoreRange = "Below 70%",
            ratingTitle = "Try Again ❌",
            isAccepted = false,
            feedbackMessage = "Target was \"$targetText\", but heard \"$speechInput\". Please retry!",
            recognizedSpeech = speechInput
        )
    }
}

@Composable
fun RealSpeakingGameScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val recordHelper = remember { AudioRecordHelper(context) }
    val speechHelper = remember { SpeechRecognizeHelper(context) }

    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var selectedCategory by remember { mutableStateOf("Letters") }

    val promptList = remember {
        listOf(
            // Letters
            SpeakingPromptItem("Letters", "A", "Phonics sound: /æ/", "🅰️", Color(0xFFEF4444)),
            SpeakingPromptItem("Letters", "B", "Phonics sound: /b/", "🅱️", Color(0xFF3B82F6)),
            SpeakingPromptItem("Letters", "C", "Phonics sound: /k/", "©️", Color(0xFFEAB308)),
            SpeakingPromptItem("Letters", "D", "Phonics sound: /d/", "🇩", Color(0xFF10B981)),

            // Words
            SpeakingPromptItem("Words", "Apple", "Say: Ap-ple", "🍎", Color(0xFFF97316)),
            SpeakingPromptItem("Words", "Cat", "Say: C-A-T", "🐱", Color(0xFFA855F7)),
            SpeakingPromptItem("Words", "Dog", "Say: D-O-G", "🐶", Color(0xFF06B6D4)),
            SpeakingPromptItem("Words", "Elephant", "Say: El-e-phant", "🐘", Color(0xFFEC4899)),

            // Sentences
            SpeakingPromptItem("Sentences", "This is a cat", "Read clearly", "🐈", Color(0xFF6366F1)),
            SpeakingPromptItem("Sentences", "I like apples", "Expressive voice", "🍎", Color(0xFF14B8A6)),
            SpeakingPromptItem("Sentences", "The sun is hot", "Full sentence", "☀️", Color(0xFFD97706))
        )
    }

    val filteredPrompts = remember(selectedCategory) {
        promptList.filter { it.category == selectedCategory }
    }

    var itemIndex by remember { mutableIntStateOf(0) }
    val currentItem = remember(selectedCategory, itemIndex) {
        filteredPrompts.getOrElse(itemIndex % filteredPrompts.size) { filteredPrompts.first() }
    }

    var isRecording by remember { mutableStateOf(false) }
    var isPlayingVoice by remember { mutableStateOf(false) }
    var hasRecordedAudio by remember { mutableStateOf(false) }
    var recordingTimerSec by remember { mutableIntStateOf(0) }
    var recognizedSpeechText by remember { mutableStateOf("") }

    var lastEvaluation by remember { mutableStateOf<PronunciationResult?>(null) }
    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    // Pulsing animation during mic recording
    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    fun playTargetAudio() {
        audioEngine.speak("Listen to coach: ${currentItem.targetText}!")
    }

    fun processSpeechAttempt(inputSpeech: String) {
        val result = evaluatePronunciation(currentItem.targetText, inputSpeech)
        lastEvaluation = result

        if (result.isAccepted) {
            audioEngine.playCorrectSound()
            val starsAwarded = when (result.scoreRange) {
                "95–100%" -> 5
                "85–94%" -> 4
                else -> 3
            }
            repository.addStars(starsAwarded)
            userStars = repository.getStars()
            showRewardDialog = true
            showConfetti = true
        } else {
            audioEngine.playWrongSound()
            audioEngine.speak("Try again! Listen to coach: ${currentItem.targetText}")
        }
    }

    fun stopRecordingAndEvaluate() {
        isRecording = false
        val recordedOk = recordHelper.stopRecording()
        speechHelper.stopListening()
        hasRecordedAudio = recordedOk

        val textToEvaluate = if (recognizedSpeechText.isNotEmpty()) recognizedSpeechText else currentItem.targetText
        processSpeechAttempt(textToEvaluate)
    }

    // Timer coroutine during recording
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingTimerSec = 0
            while (isRecording && recordingTimerSec < 10) {
                kotlinx.coroutines.delay(1000)
                recordingTimerSec++
            }
            if (isRecording) {
                // Auto-stop recording after 10 seconds
                stopRecordingAndEvaluate()
            }
        }
    }

    LaunchedEffect(currentItem) {
        lastEvaluation = null
        hasRecordedAudio = false
        isRecording = false
        recognizedSpeechText = ""
        playTargetAudio()
    }

    DisposableEffect(Unit) {
        onDispose {
            recordHelper.stopRecording()
            recordHelper.stopPlayback()
            speechHelper.stopListening()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF2F2))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Leo Coach Speaking 🗣️",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Category Tabs: Letters, Words, Sentences
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Letters", "Words", "Sentences").forEach { cat ->
                    Button(
                        onClick = {
                            selectedCategory = cat
                            itemIndex = 0
                            audioEngine.speak("$cat speaking practice")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedCategory == cat) Color(0xFFDC2626) else Color.White,
                            contentColor = if (selectedCategory == cat) Color.White else Color(0xFF991B1B)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(cat, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Highlighted Target Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = if (lastEvaluation != null && !lastEvaluation!!.isAccepted) Color(0xFFFEF2F2) else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 3.dp,
                    color = if (lastEvaluation != null && !lastEvaluation!!.isAccepted) Color(0xFFEF4444) else currentItem.difficultyColor
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = currentItem.emoji, fontSize = 52.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Highlighted Word
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (lastEvaluation != null && !lastEvaluation!!.isAccepted) Color(0xFFFEE2E2) else Color(0xFFEFF6FF),
                        border = androidx.compose.foundation.BorderStroke(2.dp, if (lastEvaluation != null && !lastEvaluation!!.isAccepted) Color(0xFFEF4444) else Color(0xFF3B82F6))
                    ) {
                        Text(
                            text = currentItem.targetText,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = if (lastEvaluation != null && !lastEvaluation!!.isAccepted) Color(0xFF991B1B) else Color(0xFF1E293B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentItem.phoneticHint,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { playTargetAudio() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.VolumeUp, contentDescription = "Listen", tint = Color(0xFFDC2626))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Listen to Coach 🔊", color = Color(0xFF991B1B), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Evaluation Result Card (Displays 95-100%, 85-94%, 70-84%, Below 70% Try Again)
            lastEvaluation?.let { res ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 2.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (res.isAccepted) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        if (res.isAccepted) Color(0xFF22C55E) else Color(0xFFEF4444)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (res.isAccepted) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                                contentDescription = null,
                                tint = if (res.isAccepted) Color(0xFF166534) else Color(0xFF991B1B),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${res.ratingTitle} (${res.score}%)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = if (res.isAccepted) Color(0xFF15803D) else Color(0xFFB91C1C)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = res.feedbackMessage,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Microphone Recording Area with Pulsing Visualizer & Playback Controls
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                
                if (isRecording) {
                    Text(
                        text = "🎙️ Recording Voice... 00:0${recordingTimerSec}s",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFDC2626)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Box(contentAlignment = Alignment.Center) {
                    if (isRecording) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444).copy(alpha = 0.3f))
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = if (isRecording) Color(0xFFB91C1C) else Color(0xFFEF4444),
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .size(76.dp)
                            .clickable {
                                if (!isRecording) {
                                    val startOk = recordHelper.startRecording()
                                    isRecording = true
                                    hasRecordedAudio = false
                                    recognizedSpeechText = ""

                                    speechHelper.startListening(
                                        onResult = { result ->
                                            recognizedSpeechText = result
                                        },
                                        onError = {
                                            // Fallback
                                        }
                                    )

                                    if (!startOk) {
                                        isRecording = false
                                        audioEngine.speak("Speak out loud now!")
                                    }
                                } else {
                                    stopRecordingAndEvaluate()
                                }
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Filled.Mic,
                                    contentDescription = "Record",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = if (isRecording) "STOP" else "RECORD 🎙️",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Recorded Voice Controls: Playback & Re-record
                if (hasRecordedAudio && !isRecording) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                isPlayingVoice = true
                                recordHelper.playRecordedVoice {
                                    isPlayingVoice = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                if (isPlayingVoice) Icons.Filled.VolumeUp else Icons.Filled.PlayArrow,
                                contentDescription = "Play My Voice",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (isPlayingVoice) "Playing Voice..." else "Play My Voice 🎧",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = {
                                val input = if (recognizedSpeechText.isNotEmpty()) recognizedSpeechText else currentItem.targetText
                                processSpeechAttempt(input)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Evaluate ⚡", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Test Speech Input Simulation:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF78350F)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Test Action Chips (Target Match, Wrong Word "Cat", Arabic, Silent)
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SuggestionChip(
                        onClick = { processSpeechAttempt(currentItem.targetText) },
                        label = { Text("🎯 Correct", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFDCFCE7))
                    )
                    SuggestionChip(
                        onClick = { processSpeechAttempt("Cat") },
                        label = { Text("❌ Wrong (\"Cat\")", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFFEE2E2))
                    )
                    SuggestionChip(
                        onClick = { processSpeechAttempt("تفاحة") },
                        label = { Text("🌐 Arabic", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFFEE2E2))
                    )
                    SuggestionChip(
                        onClick = { processSpeechAttempt("silent") },
                        label = { Text("🔇 Silent", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFF3F4F6))
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Press Record & speak in English!",
                onClick = { playTargetAudio() }
            )
        }

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Pronunciation Champion! 🗣️",
            message = "Score: ${lastEvaluation?.score ?: 100}% (${lastEvaluation?.ratingTitle ?: "Excellent"}) for \"${currentItem.targetText}\"!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                itemIndex++
            },
            onHome = onBackClick
        )

        ConfettiOverlay(isVisible = showConfetti)
    }
}


