package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.ui.games.*
import com.example.ui.games.learningworld.LeoLearningWorldScreen
import com.example.ui.home.HomeScreen
import com.example.ui.theme.MyApplicationTheme

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

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

    Box(modifier = Modifier.fillMaxSize()) {
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

            composable("learning_world") {
                LeoLearningWorldScreen(
                    repository = repository,
                    audioEngine = audioEngine,
                    onBackClick = { navController.popBackStack() }
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

        composable("run_learn") {
            RunAndLearnScreen(
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

        composable("magic_school_bag") {
            MagicSchoolBagScreen(
                repository = repository,
                audioEngine = audioEngine,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("make_pizza") {
            MakePizzaScreen(
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

    CoinEarnedOverlay(repository = repository, audioEngine = audioEngine)
}

}

@Composable
fun CoinEarnedOverlay(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine
) {
    val coinEarnedAmount = repository.latestCoinEarnedState.value
    if (coinEarnedAmount != null && coinEarnedAmount > 0) {
        LaunchedEffect(coinEarnedAmount) {
            audioEngine.playStarSound()
            delay(1800)
            repository.clearLatestCoinEarned()
        }

        val infiniteTransition = rememberInfiniteTransition()
        val floatOffsetY by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -35f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 90.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            androidx.compose.material3.Surface(
                color = Color(0xFFFEF08A),
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 12.dp,
                modifier = Modifier.graphicsLayer { translationY = floatOffsetY }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "🪙",
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "+$coinEarnedAmount Coins!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF854D0E)
                    )
                }
            }
        }
    }
}
