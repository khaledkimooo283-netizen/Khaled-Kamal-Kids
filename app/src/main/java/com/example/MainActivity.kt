package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.ui.games.*
import com.example.ui.home.HomeScreen
import com.example.ui.theme.MyApplicationTheme

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

class MainActivity : ComponentActivity() {

    private lateinit var audioEngine: SpeechAndSoundEngine
    private lateinit var repository: KkDataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        audioEngine = SpeechAndSoundEngine(this)
        repository = KkDataRepository(this)

        // Sync initial voice settings from persisted preferences
        audioEngine.applyVoiceConfig(
            voiceType = repository.getVoiceType(),
            volume = repository.getVoiceVolume(),
            language = repository.getLanguage()
        )

        setContent {
            val currentLang = repository.currentLanguageState.value
            val layoutDirection = com.example.util.Localization.getLayoutDirection(currentLang)

            MyApplicationTheme {
                CompositionLocalProvider(
                    LocalLayoutDirection provides layoutDirection,
                    com.example.util.LocalLanguage provides currentLang
                ) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        KkKidsApp(repository = repository, audioEngine = audioEngine)
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (::audioEngine.isInitialized) {
            audioEngine.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::audioEngine.isInitialized) {
            audioEngine.shutdown()
        }
    }
}

@Composable
fun KkKidsApp(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                repository = repository,
                audioEngine = audioEngine,
                onNavigateToGame = { route -> navController.navigate(route) }
            )
        }

        composable("songs_music") {
            SongsAndMusicScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("adventure_mode") {
            AdventureModeScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("dictionary") {
            DictionaryScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("parent_progress") {
            ParentProgressScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("profile_settings") {
            ProfileSettingsScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("capital_small") {
            CapitalSmallMatchScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("missing_letter") {
            MissingLetterScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("sequence_order") {
            SequenceOrderScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("listen_choose") {
            ListenAndChooseScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("tracing") {
            TracingGameScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("drag_match") {
            DragMatchGameScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("typing") {
            TypingGameScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("fishing") {
            FishingGameScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("balloon_pop") {
            BalloonPopScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("train") {
            AlphabetTrainScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("ice_cream") {
            IceCreamShopScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("animals") {
            AnimalSoundsScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("memory") {
            MemoryCardsScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("shadow_match") {
            ShadowMatchScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("space_adv") {
            SpaceAdventureScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("treasure_hunt") {
            TreasureHuntScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("feed_animal") {
            FeedAnimalScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("coloring") {
            ColoringGameScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("odd_one_out") {
            OddOneOutScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("dino_hatch") {
            DinoHatchScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("rewards") {
            RewardsScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("fishing_letters") {
            FishingGameScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() },
                initialMode = "letters"
            )
        }

        composable("fishing_numbers") {
            FishingGameScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() },
                initialMode = "numbers"
            )
        }

        composable("shopping_game") {
            ShoppingGameScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("listen_tap") {
            ListenTapGameScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("build_sentence") {
            BuildSentenceGameScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("color_by_number") {
            ColorByNumberGameScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("real_speaking") {
            RealSpeakingGameScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
