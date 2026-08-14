package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.resume

/**
 * SpeechAndSoundEngine manages TextToSpeech (TTS) with customizable pitch, rate, volume,
 * and voice personas (Child Voice, Female Teacher, Male Teacher) across languages.
 */
class SpeechAndSoundEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    var isTtsReady = false
        private set
    var isMuted = false

    var currentVoiceType: String = "Female Teacher"
    var voiceVolume: Float = 1.0f
    var currentLanguage: String = "English"

    private val utteranceCallbacks = ConcurrentHashMap<String, () -> Unit>()

    // SoundPool for instant feedback sound effects
    private var soundPool: SoundPool? = null
    private val handler = Handler(Looper.getMainLooper())

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(audioAttributes)
            .build()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            applyVoiceConfig(currentVoiceType, voiceVolume, currentLanguage)

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}

                override fun onDone(utteranceId: String?) {
                    if (utteranceId != null) {
                        utteranceCallbacks.remove(utteranceId)?.invoke()
                    }
                }

                override fun onError(utteranceId: String?) {
                    if (utteranceId != null) {
                        utteranceCallbacks.remove(utteranceId)?.invoke()
                    }
                }
            })
        } else {
            Log.e("SpeechEngine", "TTS initialization failed")
        }
    }

    fun applyVoiceConfig(voiceType: String, volume: Float, language: String) {
        currentVoiceType = voiceType
        voiceVolume = volume.coerceIn(0.0f, 1.0f)
        currentLanguage = language

        if (!isTtsReady || tts == null) return

        val locale = if (language == "Arabic") {
            Locale.Builder().setLanguage("ar").setRegion("EG").build()
        } else {
            Locale.US
        }
        val res = tts?.setLanguage(locale)
        if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
            // Fallback to US if specific language not fully supported
            tts?.setLanguage(Locale.US)
        }

        when (voiceType) {
            "Child Voice" -> {
                tts?.setPitch(1.55f)
                tts?.setSpeechRate(0.90f)
            }
            "Male Teacher" -> {
                tts?.setPitch(0.80f)
                tts?.setSpeechRate(0.85f)
            }
            else -> { // "Female Teacher"
                tts?.setPitch(1.20f)
                tts?.setSpeechRate(0.88f)
            }
        }

        // Try selecting voice persona from installed TTS voices if available
        try {
            val voices = tts?.voices
            if (!voices.isNullOrEmpty()) {
                val targetVoice = voices.find { voice ->
                    val isLangMatch = voice.locale.language == locale.language
                    val nameLower = voice.name.lowercase()
                    when (voiceType) {
                        "Male Teacher" -> isLangMatch && (nameLower.contains("male") || nameLower.contains("man"))
                        "Female Teacher" -> isLangMatch && (nameLower.contains("female") || nameLower.contains("woman"))
                        "Child Voice" -> isLangMatch && (nameLower.contains("child") || nameLower.contains("kid"))
                        else -> isLangMatch
                    }
                }
                if (targetVoice != null) {
                    tts?.voice = targetVoice
                }
            }
        } catch (e: Exception) {
            Log.d("SpeechEngine", "Voice selection fallback to pitch modulation")
        }
    }

    private var lastSpokenText: String = ""
    private var lastSpeakTimestamp: Long = 0L

    fun speak(text: String, queueMode: Int = TextToSpeech.QUEUE_FLUSH) {
        if (isMuted || !isTtsReady || voiceVolume <= 0.01f) return
        val now = System.currentTimeMillis()
        if (text == lastSpokenText && (now - lastSpeakTimestamp) < 600) {
            return
        }
        lastSpokenText = text
        lastSpeakTimestamp = now

        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, voiceVolume)
        }

        try {
            tts?.speak(text, queueMode, params, "UTTERANCE_$now")
        } catch (e: Exception) {
            Log.e("SpeechEngine", "Error speaking text", e)
        }
    }

    /**
     * Speaks text and suspends until the utterance completes.
     * Guarantees no truncated or skipped words/letters.
     */
    suspend fun speakAndWait(text: String, queueMode: Int = TextToSpeech.QUEUE_FLUSH): Boolean {
        if (isMuted || voiceVolume <= 0.01f) return true
        if (!isTtsReady || tts == null) {
            val estimatedDurationMs = (text.length * 90L + 400L).coerceIn(600L, 4000L)
            delay(estimatedDurationMs)
            return true
        }

        val utteranceId = "SONG_UTT_${System.currentTimeMillis()}_${(1000..9999).random()}"
        lastSpokenText = text
        lastSpeakTimestamp = System.currentTimeMillis()

        return suspendCancellableCoroutine { continuation ->
            utteranceCallbacks[utteranceId] = {
                if (continuation.isActive) {
                    continuation.resume(true)
                }
            }

            continuation.invokeOnCancellation {
                utteranceCallbacks.remove(utteranceId)
            }

            val params = Bundle().apply {
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, voiceVolume)
            }

            try {
                val result = tts?.speak(text, queueMode, params, utteranceId)
                if (result != TextToSpeech.SUCCESS) {
                    utteranceCallbacks.remove(utteranceId)
                    if (continuation.isActive) continuation.resume(false)
                }
            } catch (e: Exception) {
                Log.e("SpeechEngine", "Error in speakAndWait", e)
                utteranceCallbacks.remove(utteranceId)
                if (continuation.isActive) continuation.resume(false)
            }
        }
    }

    fun speakPhonetic(letter: String, word: String) {
        val prompt = "$letter... $word!"
        speak(prompt)
    }

    val bgmEngine = BackgroundMusicEngine()
    val soundFxEngine = SoundFxEngine()

    fun playClickSound() {
        if (!isMuted) soundFxEngine.playClick()
    }

    fun playCorrectSound() {
        if (!isMuted) {
            soundFxEngine.playCorrect()
            speakPraise()
        }
    }

    fun playWrongSound() {
        if (!isMuted) {
            soundFxEngine.playWrong()
            speakTryAgain()
        }
    }

    fun playStarSound() {
        if (!isMuted) soundFxEngine.playStarCollect()
    }

    fun playVictorySound() {
        if (!isMuted) soundFxEngine.playVictoryFanfare()
    }

    fun startBgm() {
        if (!isMuted) bgmEngine.start()
    }

    fun pauseBgm() {
        bgmEngine.pause()
    }

    fun resumeBgm() {
        if (!isMuted) bgmEngine.resume()
    }

    fun setBgmVolume(volume: Float) {
        bgmEngine.volume = volume
    }

    fun setBgmEnabled(enabled: Boolean) {
        bgmEngine.isEnabled = enabled
        if (!enabled) bgmEngine.pause() else if (!isMuted) bgmEngine.resume()
    }

    fun setSoundFxEnabled(enabled: Boolean) {
        soundFxEngine.isEnabled = enabled
    }

    fun speakPraise() {
        val praises = if (currentLanguage == "Arabic") {
            listOf("ممتاز!", "رائع جداً!", "عمل رائع!", "أحسنت!", "نجم خارق!", "مذهل!")
        } else {
            listOf("Excellent!", "Amazing!", "Good Job!", "Fantastic!", "Super Star!", "You did it!")
        }
        speak(praises.random())
    }

    fun speakTryAgain() {
        val tryAgainPhrases = if (currentLanguage == "Arabic") {
            listOf("لنحاول مرة أخرى!", "اقتربت كثيراً!", "أنت تستطيع!")
        } else {
            listOf("Let's try again! 😊", "Almost there!", "You can do it!")
        }
        speak(tryAgainPhrases.random())
    }

    fun stop() {
        tts?.stop()
        bgmEngine.pause()
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            soundPool?.release()
            bgmEngine.stop()
        } catch (e: Exception) {
            Log.e("SpeechEngine", "Error shutting down speech engine", e)
        }
    }
}

