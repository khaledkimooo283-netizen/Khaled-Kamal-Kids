package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

/**
 * SpeechAndSoundEngine manages TextToSpeech (TTS) with pitch and rate tuned for a warm,
 * encouraging kindergarten teacher / friendly companion voice, alongside synthetic audio cues.
 */
class SpeechAndSoundEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isTtsReady = false
    var isMuted = false

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
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("SpeechEngine", "English language not supported")
            } else {
                // Pitch 1.25f gives a bright, cheerful, friendly kindergarten teacher tone
                tts?.setPitch(1.25f)
                // Speech rate 0.92f ensures ultra-clear, engaging pronunciation for young children
                tts?.setSpeechRate(0.92f)
                isTtsReady = true

                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) {}
                    override fun onError(utteranceId: String?) {}
                })
            }
        } else {
            Log.e("SpeechEngine", "TTS initialization failed")
        }
    }

    fun speak(text: String, queueMode: Int = TextToSpeech.QUEUE_FLUSH) {
        if (isMuted || !isTtsReady) return
        try {
            tts?.speak(text, queueMode, null, "UTTERANCE_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e("SpeechEngine", "Error speaking text", e)
        }
    }

    fun speakPhonetic(letter: String, word: String) {
        val prompt = "$letter... $word!"
        speak(prompt)
    }

    fun speakPraise() {
        val praises = listOf(
            "Excellent!",
            "Amazing!",
            "Good Job!",
            "Fantastic!",
            "Super Star!",
            "You did it!",
            "Way to go!",
            "Awesome!"
        )
        speak(praises.random())
    }

    fun speakTryAgain() {
        val tryAgainPhrases = listOf(
            "Let's try again! 😊",
            "Almost there!",
            "You can do it!",
            "Try one more time!"
        )
        speak(tryAgainPhrases.random())
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            soundPool?.release()
        } catch (e: Exception) {
            Log.e("SpeechEngine", "Error shutting down speech engine", e)
        }
    }
}
