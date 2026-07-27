package com.example

import androidx.test.core.app.ApplicationProvider
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.util.Localization
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class SettingsQaTest {

    private lateinit var repository: KkDataRepository
    private lateinit var audioEngine: SpeechAndSoundEngine

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        repository = KkDataRepository(context)
        audioEngine = SpeechAndSoundEngine(context)
    }

    @Test
    fun testLanguageSwitchingAndLocalization() {
        // Test English Language Switch
        repository.setLanguage("English")
        assertEquals("English", repository.getLanguage())
        assertEquals("English", repository.currentLanguageState.value)
        assertEquals("Tracing & Writing", Localization.tr("tracing", "English"))

        // Test Arabic Language Switch
        repository.setLanguage("Arabic")
        assertEquals("Arabic", repository.getLanguage())
        assertEquals("Arabic", repository.currentLanguageState.value)
        assertEquals("التتبع والكتابة", Localization.tr("tracing", "Arabic"))
    }

    @Test
    fun testVoiceSelectionAndEngineConfig() {
        // Test Child Voice Selection
        repository.setVoiceType("Child Voice")
        assertEquals("Child Voice", repository.getVoiceType())
        assertEquals("Child Voice", repository.currentVoiceTypeState.value)
        audioEngine.applyVoiceConfig("Child Voice", 1.0f, "English")

        // Test Female Teacher Voice
        repository.setVoiceType("Female Teacher")
        assertEquals("Female Teacher", repository.getVoiceType())
        assertEquals("Female Teacher", repository.currentVoiceTypeState.value)
        audioEngine.applyVoiceConfig("Female Teacher", 0.9f, "English")

        // Test Male Teacher Voice
        repository.setVoiceType("Male Teacher")
        assertEquals("Male Teacher", repository.getVoiceType())
        assertEquals("Male Teacher", repository.currentVoiceTypeState.value)
        audioEngine.applyVoiceConfig("Male Teacher", 0.8f, "Arabic")
    }

    @Test
    fun testAudioControlsAndVolumePersistence() {
        // Voice Volume
        repository.setVoiceVolume(0.7f)
        assertEquals(0.7f, repository.getVoiceVolume(), 0.01f)
        assertEquals(0.7f, repository.currentVoiceVolumeState.floatValue, 0.01f)

        // Music Volume & Toggle
        repository.setMusicEnabled(true)
        assertTrue(repository.isMusicEnabled())
        repository.setMusicVolume(0.4f)
        assertEquals(0.4f, repository.getMusicVolume(), 0.01f)

        repository.setMusicEnabled(false)
        assertFalse(repository.isMusicEnabled())

        // Sound FX Toggle
        repository.setSoundFxEnabled(false)
        assertFalse(repository.isSoundFxEnabled())
        repository.setSoundFxEnabled(true)
        assertTrue(repository.isSoundFxEnabled())
    }

    @Test
    fun testParentPinProtectionAndModification() {
        // Default PIN check
        val defaultPin = repository.getParentPin()
        assertNotNull(defaultPin)

        // Update PIN
        repository.setParentPin("5678")
        assertEquals("5678", repository.getParentPin())

        // Reset back to default
        repository.setParentPin("1234")
        assertEquals("1234", repository.getParentPin())
    }

    @Test
    fun testGameplayDifficultyAndTracingSensitivity() {
        // Difficulty Level
        repository.setGameDifficulty("Hard")
        assertEquals("Hard", repository.getGameDifficulty())

        // Tracing Sensitivity
        repository.setTracingSensitivity("High")
        assertEquals("High", repository.getTracingSensitivity())
    }

    @Test
    fun testLocalizationKeyCoverage() {
        val testKeys = listOf(
            "app_title", "voice_style", "bg_music", "sound_fx",
            "gameplay_difficulty", "difficulty_level", "tracing_sensitivity",
            "language", "english", "arabic"
        )

        testKeys.forEach { key ->
            val enText = Localization.tr(key, "English")
            val arText = Localization.tr(key, "Arabic")
            assertFalse("Key '$key' should have an English translation", enText.isBlank())
            assertFalse("Key '$key' should have an Arabic translation", arText.isBlank())
        }
    }
}
