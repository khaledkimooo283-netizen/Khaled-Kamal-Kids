package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.*
import kotlin.math.sin

/**
 * SoundFxEngine generates soft, delightful sound effects for button clicks, 
 * correct answers, wrong attempts, star collections, and victory celebrations.
 */
class SoundFxEngine {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    var isEnabled: Boolean = true

    fun playClick() {
        if (!isEnabled) return
        scope.launch {
            playTone(freqStart = 800.0, freqEnd = 1200.0, durationMs = 40, volume = 0.2f)
        }
    }

    fun playCorrect() {
        if (!isEnabled) return
        scope.launch {
            // Happy two-note chime (C5 -> G5)
            playTone(freqStart = 523.25, freqEnd = 523.25, durationMs = 90, volume = 0.35f)
            delay(90)
            playTone(freqStart = 783.99, freqEnd = 783.99, durationMs = 180, volume = 0.4f)
        }
    }

    fun playWrong() {
        if (!isEnabled) return
        scope.launch {
            // Gentle low boing (300Hz -> 180Hz)
            playTone(freqStart = 320.0, freqEnd = 180.0, durationMs = 140, volume = 0.25f)
        }
    }

    fun playStarCollect() {
        if (!isEnabled) return
        scope.launch {
            // Sparkling rising arpeggio (E5 -> G5 -> C6)
            playTone(freqStart = 659.25, freqEnd = 659.25, durationMs = 60, volume = 0.3f)
            delay(55)
            playTone(freqStart = 783.99, freqEnd = 783.99, durationMs = 60, volume = 0.35f)
            delay(55)
            playTone(freqStart = 1046.50, freqEnd = 1046.50, durationMs = 150, volume = 0.4f)
        }
    }

    fun playVictoryFanfare() {
        if (!isEnabled) return
        scope.launch {
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
            for (freq in notes) {
                playTone(freqStart = freq, freqEnd = freq, durationMs = 100, volume = 0.35f)
                delay(95)
            }
        }
    }

    private fun playTone(freqStart: Double, freqEnd: Double, durationMs: Long, volume: Float) {
        val sampleRate = 22050
        val totalSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(100)
        val pcmBuffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val fraction = i.toDouble() / totalSamples
            val currentFreq = freqStart + (freqEnd - freqStart) * fraction
            val t = i.toDouble() / sampleRate
            val envelope = 1.0 - fraction // linear decay
            val wave = sin(2.0 * Math.PI * currentFreq * t) * envelope * volume
            pcmBuffer[i] = (wave * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(totalSamples * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(pcmBuffer, 0, pcmBuffer.size)
            track.play()
            scope.launch {
                delay(durationMs + 100)
                try {
                    track.release()
                } catch (e: Exception) {
                    // Ignore release errors
                }
            }
        } catch (e: Exception) {
            Log.e("SoundFxEngine", "Error playing tone", e)
        }
    }
}
