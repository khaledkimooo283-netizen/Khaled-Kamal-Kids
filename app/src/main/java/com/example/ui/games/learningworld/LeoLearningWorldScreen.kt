package com.example.ui.games.learningworld

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.data.WorldEnvironment

@Composable
fun LeoLearningWorldScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var selectedWorld by remember { mutableStateOf<WorldEnvironment?>(null) }
    var selectedCharacterLetter by remember { mutableStateOf<Char?>(null) }

    // Record session learning time
    DisposableEffect(Unit) {
        val startTime = System.currentTimeMillis()
        onDispose {
            val elapsedSecs = ((System.currentTimeMillis() - startTime) / 1000).toInt()
            val mins = (elapsedSecs / 60).coerceAtLeast(1)
            repository.addLearningTimeMinutes(mins)
        }
    }

    // Handle back button
    BackHandler {
        if (selectedCharacterLetter != null) {
            selectedCharacterLetter = null
        } else if (selectedWorld != null) {
            selectedWorld = null
        } else {
            onBackClick()
        }
    }

    when {
        selectedCharacterLetter != null -> {
            LetterCharacterAdventureScreen(
                repository = repository,
                audioEngine = audioEngine,
                initialLetter = selectedCharacterLetter ?: 'A',
                onBackClick = {
                    selectedCharacterLetter = null
                }
            )
        }
        selectedWorld != null -> {
            WorldActivityScreen(
                world = selectedWorld!!,
                repository = repository,
                audioEngine = audioEngine,
                onBackToMap = {
                    selectedWorld = null
                }
            )
        }
        else -> {
            LearningWorldMapScreen(
                repository = repository,
                audioEngine = audioEngine,
                onSelectWorld = { world ->
                    selectedWorld = world
                },
                onSelectCharacter = { char ->
                    selectedCharacterLetter = char
                },
                onBackClick = onBackClick
            )
        }
    }
}
