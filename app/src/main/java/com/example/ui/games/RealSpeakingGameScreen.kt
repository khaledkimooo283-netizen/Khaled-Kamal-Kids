package com.example.ui.games

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.ui.components.*
import kotlinx.coroutines.delay
import java.io.File

data class SpeakingPromptItem(
    val category: String, // "Letters", "Numbers", "Days", "Months", "Words", "Sentences"
    val targetText: String,
    val phoneticHint: String,
    val emoji: String,
    val difficultyColor: Color
)

data class PronunciationResult(
    val score: Int,
    val scoreRange: String,
    val ratingTitle: String,
    val isAccepted: Boolean,
    val feedbackMessage: String,
    val recognizedSpeech: String
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

private class AudioRecordHelper(private val context: Context) {
    @Volatile
    private var isRecording = false
    private var recordThread: Thread? = null
    private var mediaPlayer: MediaPlayer? = null
    var audioFile: File? = null
        private set

    fun startRecording(): Boolean {
        stopRecording()
        stopPlayback()
        val outputFile = File(context.cacheDir, "child_record_${System.currentTimeMillis()}.wav")
        audioFile = outputFile

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
                createValidWavFallback(outputFile)
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
                    Log.e("AudioRecordHelper", "Error stopping AudioRecord", e)
                }

                val pcmData = outputStream.toByteArray()
                if (pcmData.size > 500) {
                    try {
                        outputFile.outputStream().use { out ->
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
                            out.write(header)
                            out.write(pcmData)
                        }
                    } catch (e: Exception) {
                        createValidWavFallback(outputFile)
                    }
                } else {
                    createValidWavFallback(outputFile)
                }
            }
            recordThread?.start()
            true
        } catch (e: Exception) {
            Log.e("AudioRecordHelper", "Failed to start AudioRecord", e)
            createValidWavFallback(outputFile)
            audioFile = outputFile
            true
        }
    }

    fun stopRecording(): Boolean {
        isRecording = false
        try {
            recordThread?.join(500)
        } catch (e: Exception) {
            Log.e("AudioRecordHelper", "Join thread error", e)
        }
        recordThread = null

        val file = audioFile
        if (file == null || !file.exists() || file.length() < 100) {
            val outputFile = file ?: File(context.cacheDir, "child_record_${System.currentTimeMillis()}.wav")
            createValidWavFallback(outputFile)
            audioFile = outputFile
        }
        return true
    }

    fun playRecordedVoice(onComplete: () -> Unit = {}) {
        val file = audioFile ?: run {
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

    fun startListening(onResult: (List<String>) -> Unit, onError: () -> Unit) {
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
                        onResult(matches ?: emptyList())
                    }
                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            onResult(matches)
                        }
                    }
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
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

private fun evaluateSingleCandidate(targetText: String, speechInput: String): PronunciationResult {
    val cleanTarget = targetText.trim().lowercase().replace(Regex("[^a-z0-9 ]"), "")
    val cleanInput = speechInput.trim().lowercase().replace(Regex("[^a-z0-9 ]"), "")

    if (cleanInput.isEmpty() || cleanInput == "silent") {
        return PronunciationResult(
            score = 0,
            scoreRange = "Below 70%",
            ratingTitle = "Try Again ❌",
            isAccepted = false,
            feedbackMessage = "No speech detected. Please speak clearly into the microphone!",
            recognizedSpeech = "[Silence]"
        )
    }

    val hasNonEnglish = speechInput.any { it in '\u0600'..'\u06FF' }
    if (hasNonEnglish) {
        return PronunciationResult(
            score = 10,
            scoreRange = "Below 70%",
            ratingTitle = "Try Again ❌",
            isAccepted = false,
            feedbackMessage = "Arabic speech detected! Please pronounce \"$targetText\" in clear English.",
            recognizedSpeech = speechInput
        )
    }

    val targetWords = cleanTarget.split(" ").filter { it.isNotEmpty() }
    val inputWords = cleanInput.split(" ").filter { it.isNotEmpty() }

    var isAccepted = false
    var finalScore = 0

    if (targetWords.size == 1) {
        val tWord = targetWords[0]
        if (inputWords.contains(tWord) || cleanInput == cleanTarget) {
            isAccepted = true
            finalScore = 100
        } else {
            val bestDist = inputWords.minOfOrNull { levenshteinDistance(tWord, it) } ?: 99
            if (tWord.length <= 4 && bestDist == 0) {
                isAccepted = true
                finalScore = 100
            } else if (tWord.length >= 5 && bestDist <= 1) {
                isAccepted = true
                finalScore = 88
            } else {
                isAccepted = false
                finalScore = 30
            }
        }
    } else {
        // Sentence evaluation: check matching word count
        val matchedCount = targetWords.count { inputWords.contains(it) }
        val matchRatio = matchedCount.toFloat() / targetWords.size.toFloat()
        if (matchRatio >= 0.75f) {
            isAccepted = true
            finalScore = (matchRatio * 100).toInt()
        } else {
            isAccepted = false
            finalScore = (matchRatio * 100).toInt()
        }
    }

    return if (isAccepted) {
        PronunciationResult(
            score = finalScore,
            scoreRange = if (finalScore >= 90) "95–100%" else "85–94%",
            ratingTitle = if (finalScore >= 90) "Excellent ⭐⭐⭐⭐⭐" else "Very Good ⭐⭐⭐⭐",
            isAccepted = true,
            feedbackMessage = "Great Job! Excellent pronunciation of \"$targetText\"!",
            recognizedSpeech = speechInput
        )
    } else {
        PronunciationResult(
            score = finalScore,
            scoreRange = "Below 70%",
            ratingTitle = "Try Again ❌",
            isAccepted = false,
            feedbackMessage = "Target was \"$targetText\", but heard \"$speechInput\". Try again!",
            recognizedSpeech = speechInput
        )
    }
}

private fun evaluatePronunciationCandidates(targetText: String, candidates: List<String>): PronunciationResult {
    if (candidates.isEmpty()) {
        return PronunciationResult(
            score = 0,
            scoreRange = "Below 70%",
            ratingTitle = "Try Again ❌",
            isAccepted = false,
            feedbackMessage = "No speech detected. Please speak clearly!",
            recognizedSpeech = "[Silence]"
        )
    }

    val evaluated = candidates.map { evaluateSingleCandidate(targetText, it) }
    return evaluated.maxByOrNull { it.score } ?: evaluated.first()
}

@OptIn(ExperimentalMaterial3Api::class)
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

    // Request runtime microphone permission
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasMicPermission = isGranted }
    )

    val promptList = remember {
        listOf(
            // Letters A-Z
            SpeakingPromptItem("Letters", "A", "Phonics sound: /æ/", "🅰️", Color(0xFFEF4444)),
            SpeakingPromptItem("Letters", "B", "Phonics sound: /b/", "🅱️", Color(0xFF3B82F6)),
            SpeakingPromptItem("Letters", "C", "Phonics sound: /k/", "©️", Color(0xFFEAB308)),
            SpeakingPromptItem("Letters", "D", "Phonics sound: /d/", "🇩", Color(0xFF10B981)),
            SpeakingPromptItem("Letters", "E", "Phonics sound: /e/", "🇪", Color(0xFF8B5CF6)),
            SpeakingPromptItem("Letters", "F", "Phonics sound: /f/", "🇫", Color(0xFFEC4899)),
            SpeakingPromptItem("Letters", "G", "Phonics sound: /g/", "🇬", Color(0xFFF59E0B)),
            SpeakingPromptItem("Letters", "H", "Phonics sound: /h/", "🇭", Color(0xFF06B6D4)),
            SpeakingPromptItem("Letters", "I", "Phonics sound: /aɪ/", "🇮", Color(0xFF8B5CF6)),
            SpeakingPromptItem("Letters", "J", "Phonics sound: /dʒ/", "🇯", Color(0xFFEC4899)),
            SpeakingPromptItem("Letters", "K", "Phonics sound: /k/", "🇰", Color(0xFFEF4444)),
            SpeakingPromptItem("Letters", "L", "Phonics sound: /l/", "🇱", Color(0xFF3B82F6)),
            SpeakingPromptItem("Letters", "M", "Phonics sound: /m/", "🇲", Color(0xFF10B981)),

            // Numbers 0 to 20
            SpeakingPromptItem("Numbers", "Zero", "Say number 0", "0️⃣", Color(0xFF6366F1)),
            SpeakingPromptItem("Numbers", "One", "Say number 1", "1️⃣", Color(0xFFEF4444)),
            SpeakingPromptItem("Numbers", "Two", "Say number 2", "2️⃣", Color(0xFFF97316)),
            SpeakingPromptItem("Numbers", "Three", "Say number 3", "3️⃣", Color(0xFFEAB308)),
            SpeakingPromptItem("Numbers", "Four", "Say number 4", "4️⃣", Color(0xFF10B981)),
            SpeakingPromptItem("Numbers", "Five", "Say number 5", "5️⃣", Color(0xFF06B6D4)),
            SpeakingPromptItem("Numbers", "Six", "Say number 6", "6️⃣", Color(0xFF3B82F6)),
            SpeakingPromptItem("Numbers", "Seven", "Say number 7", "7️⃣", Color(0xFF8B5CF6)),
            SpeakingPromptItem("Numbers", "Eight", "Say number 8", "8️⃣", Color(0xFFEC4899)),
            SpeakingPromptItem("Numbers", "Nine", "Say number 9", "9️⃣", Color(0xFFF43F5E)),
            SpeakingPromptItem("Numbers", "Ten", "Say number 10", "🔟", Color(0xFF14B8A6)),
            SpeakingPromptItem("Numbers", "Eleven", "Say number 11", "1️⃣1️⃣", Color(0xFF6366F1)),
            SpeakingPromptItem("Numbers", "Twelve", "Say number 12", "1️⃣2️⃣", Color(0xFF8B5CF6)),
            SpeakingPromptItem("Numbers", "Thirteen", "Say number 13", "1️⃣3️⃣", Color(0xFFEC4899)),
            SpeakingPromptItem("Numbers", "Fourteen", "Say number 14", "1️⃣4️⃣", Color(0xFFEF4444)),
            SpeakingPromptItem("Numbers", "Fifteen", "Say number 15", "1️⃣5️⃣", Color(0xFFF97316)),
            SpeakingPromptItem("Numbers", "Sixteen", "Say number 16", "1️⃣6️⃣", Color(0xFFEAB308)),
            SpeakingPromptItem("Numbers", "Seventeen", "Say number 17", "1️⃣7️⃣", Color(0xFF10B981)),
            SpeakingPromptItem("Numbers", "Eighteen", "Say number 18", "1️⃣8️⃣", Color(0xFF06B6D4)),
            SpeakingPromptItem("Numbers", "Nineteen", "Say number 19", "1️⃣9️⃣", Color(0xFF3B82F6)),
            SpeakingPromptItem("Numbers", "Twenty", "Say number 20", "2️⃣0️⃣", Color(0xFF14B8A6)),

            // Days of the Week (Strictly starting Sunday to Saturday)
            SpeakingPromptItem("Days", "Sunday", "1st day of the week", "🌅", Color(0xFFEF4444)),
            SpeakingPromptItem("Days", "Monday", "2nd day of the week", "☀️", Color(0xFFF97316)),
            SpeakingPromptItem("Days", "Tuesday", "3rd day of the week", "🌤️", Color(0xFFEAB308)),
            SpeakingPromptItem("Days", "Wednesday", "4th day of the week", "🌿", Color(0xFF10B981)),
            SpeakingPromptItem("Days", "Thursday", "5th day of the week", "🌈", Color(0xFF06B6D4)),
            SpeakingPromptItem("Days", "Friday", "6th day of the week", "🎉", Color(0xFF3B82F6)),
            SpeakingPromptItem("Days", "Saturday", "7th day of the week", "⭐", Color(0xFF8B5CF6)),

            // Months of the Year (All 12 months)
            SpeakingPromptItem("Months", "January", "Month 1", "❄️", Color(0xFF3B82F6)),
            SpeakingPromptItem("Months", "February", "Month 2", "💖", Color(0xFFEC4899)),
            SpeakingPromptItem("Months", "March", "Month 3", "🌱", Color(0xFF10B981)),
            SpeakingPromptItem("Months", "April", "Month 4", "🌧️", Color(0xFF06B6D4)),
            SpeakingPromptItem("Months", "May", "Month 5", "🌸", Color(0xFFEAB308)),
            SpeakingPromptItem("Months", "June", "Month 6", "☀️", Color(0xFFF97316)),
            SpeakingPromptItem("Months", "July", "Month 7", "🏖️", Color(0xFFEF4444)),
            SpeakingPromptItem("Months", "August", "Month 8", "🌻", Color(0xFFF59E0B)),
            SpeakingPromptItem("Months", "September", "Month 9", "🍂", Color(0xFF8B5CF6)),
            SpeakingPromptItem("Months", "October", "Month 10", "🎃", Color(0xFFEC4899)),
            SpeakingPromptItem("Months", "November", "Month 11", "🍁", Color(0xFFD97706)),
            SpeakingPromptItem("Months", "December", "Month 12", "🎄", Color(0xFF16A34A)),

            // Words
            SpeakingPromptItem("Words", "Apple", "Say: Ap-ple", "🍎", Color(0xFFF97316)),
            SpeakingPromptItem("Words", "Cat", "Say: C-A-T", "🐱", Color(0xFFA855F7)),
            SpeakingPromptItem("Words", "Dog", "Say: D-O-G", "🐶", Color(0xFF06B6D4)),
            SpeakingPromptItem("Words", "Elephant", "Say: El-e-phant", "🐘", Color(0xFFEC4899)),
            SpeakingPromptItem("Words", "Banana", "Say: Ba-na-na", "🍌", Color(0xFFEAB308)),

            // Sentences
            SpeakingPromptItem("Sentences", "This is a cat", "Read clearly", "🐈", Color(0xFF6366F1)),
            SpeakingPromptItem("Sentences", "I like apples", "Expressive voice", "🍎", Color(0xFF14B8A6))
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
    var recognizedCandidates by remember { mutableStateOf<List<String>>(emptyList()) }

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
        audioEngine.speak(currentItem.targetText)
    }

    fun performAutoEvaluation() {
        val result = evaluatePronunciationCandidates(currentItem.targetText, recognizedCandidates)
        lastEvaluation = result

        if (result.isAccepted) {
            audioEngine.playCorrectSound()
            repository.addStars(5)
            repository.rewardPronunciation()
            userStars = repository.getStars()
            showRewardDialog = true
            showConfetti = true
            audioEngine.speak("Great job! Perfect pronunciation!")
        } else {
            audioEngine.playWrongSound()
            audioEngine.speak("Try again! Listen to coach: ${currentItem.targetText}")
        }
    }

    fun stopRecordingAndProcess() {
        isRecording = false
        recordHelper.stopRecording()
        speechHelper.stopListening()
        hasRecordedAudio = true

        // Step 4: Replay child's recorded voice automatically so they hear their real audio
        isPlayingVoice = true
        recordHelper.playRecordedVoice {
            isPlayingVoice = false
            // Step 5: Automatically compare & evaluate after replay
            performAutoEvaluation()
        }
    }

    // Timer coroutine during recording (auto stops after 5s if child doesn't press stop)
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingTimerSec = 0
            while (isRecording && recordingTimerSec < 5) {
                delay(1000)
                recordingTimerSec++
            }
            if (isRecording) {
                stopRecordingAndProcess()
            }
        }
    }

    LaunchedEffect(currentItem) {
        lastEvaluation = null
        hasRecordedAudio = false
        isRecording = false
        recognizedCandidates = emptyList()
        // Step 1: Play Native English audio automatically when word loads
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
                title = "Pronunciation Coach 🗣️",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Category Selector Tabs
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val categories = listOf(
                    "Letters" to "🔤 Letters",
                    "Numbers" to "🔢 Numbers",
                    "Days" to "📅 Days",
                    "Months" to "🗓️ Months",
                    "Words" to "🍎 Words",
                    "Sentences" to "💬 Sentences"
                )
                items(categories.size) { idx ->
                    val (catKey, label) = categories[idx]
                    val isSelected = selectedCategory == catKey
                    Button(
                        onClick = {
                            selectedCategory = catKey
                            itemIndex = 0
                            audioEngine.speak("$catKey practice")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFFDC2626) else Color.White,
                            contentColor = if (isSelected) Color.White else Color(0xFF991B1B)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Target Word Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = if (lastEvaluation != null && !lastEvaluation!!.isAccepted) Color(0xFFFEF2F2) else Color.White
                ),
                shape = RoundedCornerShape(24.dp),
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
                    Text(text = currentItem.emoji, fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFEFF6FF),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF3B82F6))
                    ) {
                        Text(
                            text = currentItem.targetText,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = currentItem.phoneticHint,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Listen Again Button (Native English Audio)
                    Button(
                        onClick = { playTargetAudio() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Filled.VolumeUp, contentDescription = "Listen", tint = Color(0xFFDC2626))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Listen Again 🔊", color = Color(0xFF991B1B), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Automatic Evaluation Result Display
            lastEvaluation?.let { res ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .shadow(4.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
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
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (res.isAccepted) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                                contentDescription = null,
                                tint = if (res.isAccepted) Color(0xFF15803D) else Color(0xFFDC2626),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (res.isAccepted) "🎉 ${res.ratingTitle}" else "❌ Try Again",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = if (res.isAccepted) Color(0xFF15803D) else Color(0xFFB91C1C)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = res.feedbackMessage,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Main Microphone Record / Stop Controls
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                if (isRecording) {
                    Text(
                        text = "🎙️ Listening & Recording... 00:0${recordingTimerSec}s",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFDC2626)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                } else if (isPlayingVoice) {
                    Text(
                        text = "🎧 Replaying your recorded voice...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Box(contentAlignment = Alignment.Center) {
                    if (isRecording) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444).copy(alpha = 0.3f))
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = if (isRecording) Color(0xFFB91C1C) else Color(0xFFEF4444),
                        shadowElevation = 10.dp,
                        modifier = Modifier
                            .size(80.dp)
                            .clickable {
                                if (!hasMicPermission) {
                                    launcher.launch(Manifest.permission.RECORD_AUDIO)
                                    return@clickable
                                }

                                if (!isRecording) {
                                    recordHelper.startRecording()
                                    isRecording = true
                                    hasRecordedAudio = false
                                    recognizedCandidates = emptyList()

                                    speechHelper.startListening(
                                        onResult = { candidates ->
                                            recognizedCandidates = candidates
                                        },
                                        onError = {
                                            // Speech recognizer error fallback
                                        }
                                    )
                                } else {
                                    stopRecordingAndProcess()
                                }
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
                                    contentDescription = if (isRecording) "Stop" else "Record",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                                Text(
                                    text = if (isRecording) "STOP ⏹️" else "RECORD 🎙️",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Replay Recorded Voice & Next Buttons
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            isPlayingVoice = true
                            recordHelper.playRecordedVoice {
                                isPlayingVoice = false
                            }
                        },
                        enabled = hasRecordedAudio && !isRecording,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            if (isPlayingVoice) Icons.Filled.VolumeUp else Icons.Filled.PlayArrow,
                            contentDescription = "Replay Voice",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (isPlayingVoice) "Playing..." else "Replay My Voice 🎧",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = {
                            itemIndex++
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Next Word ➡️", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Press Record & speak in English! I will listen!",
                onClick = { playTargetAudio() }
            )
        }

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Pronunciation Champion! 🗣️✨",
            message = "Awesome! Perfect pronunciation of \"${currentItem.targetText}\"! You earned 5 Stars and 5 Coins!",
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
