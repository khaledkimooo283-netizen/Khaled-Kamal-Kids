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

class MainActivity : ComponentActivity() {

    private lateinit var audioEngine: SpeechAndSoundEngine
    private lateinit var repository: KkDataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        audioEngine = SpeechAndSoundEngine(this)
        repository = KkDataRepository(this)

        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    KkKidsApp(repository = repository, audioEngine = audioEngine)
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
    }
}
