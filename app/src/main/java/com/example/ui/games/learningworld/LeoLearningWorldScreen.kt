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
        if (selectedWorld != null) {
            selectedWorld = null
        } else {
            onBackClick()
        }
    }

    Crossfade(
        targetState = selectedWorld,
        label = "worldScreenCrossfade",
        modifier = Modifier.fillMaxSize()
    ) { currentWorld ->
        if (currentWorld == null) {
            LearningWorldMapScreen(
                repository = repository,
                audioEngine = audioEngine,
                onSelectWorld = { world ->
                    selectedWorld = world
                },
                onBackClick = onBackClick
            )
        } else {
            WorldActivityScreen(
                world = currentWorld,
                repository = repository,
                audioEngine = audioEngine,
                onBackToMap = {
                    selectedWorld = null
                }
            )
        }
    }
}
