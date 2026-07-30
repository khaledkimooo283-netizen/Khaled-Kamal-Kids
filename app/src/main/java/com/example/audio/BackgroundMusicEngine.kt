package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.*
import kotlin.math.sin

/**
 * BackgroundMusicEngine plays a soft, magical, instrumental nursery loop 
 * using Android AudioTrack synthesized wave generation. 
 * Completely offline, zero network or asset dependency.
 */
class BackgroundMusicEngine {

    private var audioTrack: AudioTrack? = null
    private var isPlaying = false
    private var musicJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    var isEnabled: Boolean = true
        set(value) {
            field = value
            if (!value) pause()
        }

    var volume: Float = 0.25f
        set(value) {
            field = value.coerceIn(0f, 1f)
            audioTrack?.setVolume(field)
        }

    // Gentle Pentatonic Melody Notes (Hz): C4, D4, E4, G4, A4, C5, D5, E5
    private val melodyNotes = doubleArrayOf(
        261.63, 293.66, 329.63, 392.00, 440.00, 523.25, 587.33, 659.25
    )

    fun start() {
        if (isPlaying || !isEnabled || volume <= 0.01f) return
        isPlaying = true

        musicJob?.cancel()
        musicJob = scope.launch {
            runMelodyLoop()
        }
    }

    fun pause() {
        isPlaying = false
        musicJob?.cancel()
        try {
            audioTrack?.pause()
            audioTrack?.flush()
        } catch (e: Exception) {
            Log.e("BgmEngine", "Error pausing BGM", e)
        }
    }

    fun resume() {
        if (isEnabled && volume > 0.01f) {
            start()
        }
    }

    fun stop() {
        pause()
        try {
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        } catch (e: Exception) {
            Log.e("BgmEngine", "Error stopping BGM", e)
        }
    }

    private suspend fun runMelodyLoop() {
        val sampleRate = 22050
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(4410)

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.setVolume(volume)
        audioTrack?.play()

        val patternSequence = intArrayOf(
            0, 2, 4, 3, 5, 4, 2, 0,
            1, 3, 5, 4, 6, 5, 3, 1,
            2, 4, 6, 5, 7, 6, 4, 2,
            0, 3, 5, 7, 6, 4, 2, 0
        )

        val noteDurationSamples = (sampleRate * 0.35).toInt()
        val pcmBuffer = ShortArray(noteDurationSamples)

        var noteIdx = 0
        while (isPlaying && scope.isActive) {
            val noteFreq = melodyNotes[patternSequence[noteIdx % patternSequence.size]]
            val noteFreq2 = melodyNotes[(patternSequence[noteIdx % patternSequence.size] + 2) % melodyNotes.size]

            for (i in 0 until noteDurationSamples) {
                val t = i.toDouble() / sampleRate
                // Soft bell-like envelope decay
                val envelope = Math.exp(-3.5 * t)
                // Fundamental wave + soft octave harmonic
                val wave1 = sin(2.0 * Math.PI * noteFreq * t)
                val wave2 = 0.3 * sin(2.0 * Math.PI * noteFreq2 * t)
                val combined = (wave1 + wave2) * envelope * 0.25
                val sample = (combined * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                pcmBuffer[i] = sample.toShort()
            }

            audioTrack?.write(pcmBuffer, 0, pcmBuffer.size)
            noteIdx++
            delay(340L)
        }
    }
}
