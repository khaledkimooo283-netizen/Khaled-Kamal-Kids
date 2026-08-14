package com.example.ui.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.ui.components.ConfettiOverlay
import com.example.ui.components.KkHeader
import com.example.ui.components.StarRewardDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Question Types for Run & Learn
enum class RunQuestionType {
    PICTURE_QUESTION,  // Show 🍎 -> "What is this?" -> Choices: "Apple", "Cat", "Car"
    FIND_THE_WORD,     // "Find the ball." -> Choices: ⚽ Ball, 🍎 Apple, 🐱 Cat
    FIND_THE_PICTURE,  // "Find the cat." -> Choices: 🐱 Cat, 🐶 Dog, 🚗 Car
    COLORS,            // "Find the red car." -> Choices: 🔴 Red Car, 🔵 Blue Car, 🟢 Green Car
    NUMBERS,           // "Which one is three?" -> Choices: 1, 3, 5
    COUNTING,          // Show 🍎🍎🍎 -> "How many apples?" -> Choices: 2, 3, 5
    SIMPLE_ENGLISH     // "The cat is ___." -> Choices: Big, Apple, Car
}

data class RunChoiceItem(
    val id: String,
    val text: String,
    val emoji: String,
    val isCorrect: Boolean
)

data class RunQuestion(
    val id: String,
    val type: RunQuestionType,
    val voicePrompt: String,
    val promptText: String,
    val promptEmoji: String = "",
    val choices: List<RunChoiceItem>
)

data class RunWorld(
    val id: Int,
    val name: String,
    val emoji: String,
    val runnerEmoji: String,
    val runnerName: String,
    val skyGradient: List<Color>,
    val groundColor: Color,
    val themeColor: Color,
    val sceneryEmojis: List<String>,
    val levelName: String
)

@Composable
fun RunAndLearnScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    val coroutineScope = rememberCoroutineScope()

    // 4 Worlds Definition
    val worlds = remember {
        listOf(
            RunWorld(
                id = 1,
                name = "Green Forest",
                emoji = "🌳",
                runnerEmoji = "🦊",
                runnerName = "Fox",
                skyGradient = listOf(Color(0xFFD1FAE5), Color(0xFFA7F3D0)),
                groundColor = Color(0xFF10B981),
                themeColor = Color(0xFF059669),
                sceneryEmojis = listOf("🌳", "🌲", "🌿", "🍄", "🌺", "🌸"),
                levelName = "Level 1: Easy Vocabulary"
            ),
            RunWorld(
                id = 2,
                name = "Sunny Beach",
                emoji = "🏖️",
                runnerEmoji = "🐶",
                runnerName = "Puppy",
                skyGradient = listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD)),
                groundColor = Color(0xFFFCD34D),
                themeColor = Color(0xFF0284C7),
                sceneryEmojis = listOf("🌴", "🌊", "🐚", "🦀", "🐠", "⛵"),
                levelName = "Level 2: Beach Fun Words"
            ),
            RunWorld(
                id = 3,
                name = "Busy City",
                emoji = "🏙️",
                runnerEmoji = "🐱",
                runnerName = "Kitty",
                skyGradient = listOf(Color(0xFFEDE9FE), Color(0xFFDDD6FE)),
                groundColor = Color(0xFF64748B),
                themeColor = Color(0xFF7C3AED),
                sceneryEmojis = listOf("🏢", "🏬", "🛣️", "🚗", "🚕", "🚦"),
                levelName = "Level 3: Colors & Numbers"
            ),
            RunWorld(
                id = 4,
                name = "Outer Space",
                emoji = "🚀",
                runnerEmoji = "🧑‍🚀",
                runnerName = "Astronaut",
                skyGradient = listOf(Color(0xFF1E1B4B), Color(0xFF312E81)),
                groundColor = Color(0xFF4C1D95),
                themeColor = Color(0xFF6366F1),
                sceneryEmojis = listOf("✨", "🌟", "🪐", "🌙", "🛸", "☄️"),
                levelName = "Level 4: Sentences & Mix"
            )
        )
    }

    var unlockedWorldId by remember { mutableIntStateOf(1) }
    var selectedWorldId by remember { mutableIntStateOf(1) }

    val currentWorld = remember(selectedWorldId) {
        worlds.find { it.id == selectedWorldId } ?: worlds[0]
    }

    // World Questions Generator
    fun getQuestionsForWorld(worldId: Int): List<RunQuestion> {
        return when (worldId) {
            1 -> listOf(
                RunQuestion(
                    id = "w1_q1",
                    type = RunQuestionType.PICTURE_QUESTION,
                    voicePrompt = "What is this?",
                    promptText = "What is this?",
                    promptEmoji = "🍎",
                    choices = listOf(
                        RunChoiceItem("c1", "Apple", "🍎", true),
                        RunChoiceItem("c2", "Cat", "🐱", false)
                    )
                ),
                RunQuestion(
                    id = "w1_q2",
                    type = RunQuestionType.FIND_THE_WORD,
                    voicePrompt = "Find the ball!",
                    promptText = "Find the ball",
                    choices = listOf(
                        RunChoiceItem("c1", "Ball", "⚽", true),
                        RunChoiceItem("c2", "Dog", "🐶", false)
                    )
                ),
                RunQuestion(
                    id = "w1_q3",
                    type = RunQuestionType.FIND_THE_PICTURE,
                    voicePrompt = "Find the cat!",
                    promptText = "Find the cat",
                    choices = listOf(
                        RunChoiceItem("c1", "Cat", "🐱", true),
                        RunChoiceItem("c2", "Car", "🚗", false)
                    )
                ),
                RunQuestion(
                    id = "w1_q4",
                    type = RunQuestionType.PICTURE_QUESTION,
                    voicePrompt = "What is this?",
                    promptText = "What is this?",
                    promptEmoji = "☀️",
                    choices = listOf(
                        RunChoiceItem("c1", "Sun", "☀️", true),
                        RunChoiceItem("c2", "Cup", "☕", false)
                    )
                ),
                RunQuestion(
                    id = "w1_q5",
                    type = RunQuestionType.FIND_THE_WORD,
                    voicePrompt = "Find the book!",
                    promptText = "Find the book",
                    choices = listOf(
                        RunChoiceItem("c1", "Book", "📖", true),
                        RunChoiceItem("c2", "Bag", "🎒", false)
                    )
                )
            )

            2 -> listOf(
                RunQuestion(
                    id = "w2_q1",
                    type = RunQuestionType.FIND_THE_PICTURE,
                    voicePrompt = "Find the fish!",
                    promptText = "Find the fish",
                    choices = listOf(
                        RunChoiceItem("c1", "Fish", "🐟", true),
                        RunChoiceItem("c2", "Bird", "🐦", false),
                        RunChoiceItem("c3", "Tree", "🌳", false)
                    )
                ),
                RunQuestion(
                    id = "w2_q2",
                    type = RunQuestionType.PICTURE_QUESTION,
                    voicePrompt = "What is this?",
                    promptText = "What is this?",
                    promptEmoji = "🥛",
                    choices = listOf(
                        RunChoiceItem("c1", "Milk", "🥛", true),
                        RunChoiceItem("c2", "Juice", "🧃", false),
                        RunChoiceItem("c3", "Cake", "🎂", false)
                    )
                ),
                RunQuestion(
                    id = "w2_q3",
                    type = RunQuestionType.FIND_THE_WORD,
                    voicePrompt = "Find the shoe!",
                    promptText = "Find the shoe",
                    choices = listOf(
                        RunChoiceItem("c1", "Shoe", "👟", true),
                        RunChoiceItem("c2", "Hat", "🧢", false),
                        RunChoiceItem("c3", "Bed", "🛏️", false)
                    )
                ),
                RunQuestion(
                    id = "w2_q4",
                    type = RunQuestionType.COLORS,
                    voicePrompt = "Find the red car!",
                    promptText = "Find the red car",
                    choices = listOf(
                        RunChoiceItem("c1", "Red Car", "🚗", true),
                        RunChoiceItem("c2", "Blue Car", "🚙", false),
                        RunChoiceItem("c3", "Green Car", "🚕", false)
                    )
                ),
                RunQuestion(
                    id = "w2_q5",
                    type = RunQuestionType.FIND_THE_PICTURE,
                    voicePrompt = "Find the house!",
                    promptText = "Find the house",
                    choices = listOf(
                        RunChoiceItem("c1", "House", "🏠", true),
                        RunChoiceItem("c2", "Tree", "🌳", false),
                        RunChoiceItem("c3", "Bird", "🐦", false)
                    )
                )
            )

            3 -> listOf(
                RunQuestion(
                    id = "w3_q1",
                    type = RunQuestionType.NUMBERS,
                    voicePrompt = "Which one is three?",
                    promptText = "Which one is 3?",
                    choices = listOf(
                        RunChoiceItem("c1", "1", "1️⃣", false),
                        RunChoiceItem("c2", "3", "3️⃣", true),
                        RunChoiceItem("c3", "5", "5️⃣", false)
                    )
                ),
                RunQuestion(
                    id = "w3_q2",
                    type = RunQuestionType.COUNTING,
                    voicePrompt = "How many apples?",
                    promptText = "How many apples?",
                    promptEmoji = "🍎 🍎 🍎",
                    choices = listOf(
                        RunChoiceItem("c1", "2", "2️⃣", false),
                        RunChoiceItem("c2", "3", "3️⃣", true),
                        RunChoiceItem("c3", "5", "5️⃣", false)
                    )
                ),
                RunQuestion(
                    id = "w3_q3",
                    type = RunQuestionType.FIND_THE_WORD,
                    voicePrompt = "Find the word Happy!",
                    promptText = "Find: Happy",
                    choices = listOf(
                        RunChoiceItem("c1", "Happy", "😀", true),
                        RunChoiceItem("c2", "Sad", "😢", false),
                        RunChoiceItem("c3", "Hot", "☕", false)
                    )
                ),
                RunQuestion(
                    id = "w3_q4",
                    type = RunQuestionType.COLORS,
                    voicePrompt = "Find the blue star!",
                    promptText = "Find the blue star",
                    choices = listOf(
                        RunChoiceItem("c1", "Blue Star", "💙", true),
                        RunChoiceItem("c2", "Red Star", "❤️", false),
                        RunChoiceItem("c3", "Yellow Star", "💛", false)
                    )
                ),
                RunQuestion(
                    id = "w3_q5",
                    type = RunQuestionType.FIND_THE_WORD,
                    voicePrompt = "Which animal is Big?",
                    promptText = "Which animal is Big?",
                    choices = listOf(
                        RunChoiceItem("c1", "Elephant (Big)", "🐘", true),
                        RunChoiceItem("c2", "Mouse (Small)", "🐭", false),
                        RunChoiceItem("c3", "Cat", "🐱", false)
                    )
                )
            )

            else -> listOf(
                RunQuestion(
                    id = "w4_q1",
                    type = RunQuestionType.SIMPLE_ENGLISH,
                    voicePrompt = "Complete the sentence: The cat is...",
                    promptText = "The cat is ___.",
                    choices = listOf(
                        RunChoiceItem("c1", "Big", "🐘", true),
                        RunChoiceItem("c2", "Apple", "🍎", false),
                        RunChoiceItem("c3", "Car", "🚗", false)
                    )
                ),
                RunQuestion(
                    id = "w4_q2",
                    type = RunQuestionType.SIMPLE_ENGLISH,
                    voicePrompt = "Complete: The sun is...",
                    promptText = "The sun is ___.",
                    choices = listOf(
                        RunChoiceItem("c1", "Hot", "🔥", true),
                        RunChoiceItem("c2", "Cold", "🧊", false),
                        RunChoiceItem("c3", "Dog", "🐶", false)
                    )
                ),
                RunQuestion(
                    id = "w4_q3",
                    type = RunQuestionType.COUNTING,
                    voicePrompt = "How many stars?",
                    promptText = "How many stars?",
                    promptEmoji = "⭐ ⭐ ⭐ ⭐ ⭐",
                    choices = listOf(
                        RunChoiceItem("c1", "3", "3️⃣", false),
                        RunChoiceItem("c2", "5", "5️⃣", true),
                        RunChoiceItem("c3", "2", "2️⃣", false)
                    )
                ),
                RunQuestion(
                    id = "w4_q4",
                    type = RunQuestionType.FIND_THE_WORD,
                    voicePrompt = "Find the astronaut!",
                    promptText = "Find the astronaut",
                    choices = listOf(
                        RunChoiceItem("c1", "Astronaut", "🧑‍🚀", true),
                        RunChoiceItem("c2", "Rocket", "🚀", false),
                        RunChoiceItem("c3", "Moon", "🌙", false)
                    )
                ),
                RunQuestion(
                    id = "w4_q5",
                    type = RunQuestionType.PICTURE_QUESTION,
                    voicePrompt = "What is this?",
                    promptText = "What is this?",
                    promptEmoji = "🪐",
                    choices = listOf(
                        RunChoiceItem("c1", "Planet", "🪐", true),
                        RunChoiceItem("c2", "Sun", "☀️", false),
                        RunChoiceItem("c3", "Star", "✨", false)
                    )
                )
            )
        }
    }

    val currentQuestions = remember(selectedWorldId) {
        getQuestionsForWorld(selectedWorldId)
    }

    var questionIndex by remember(selectedWorldId) { mutableIntStateOf(0) }
    val currentQuestion = currentQuestions.getOrElse(questionIndex) { currentQuestions.last() }

    var isRunningFast by remember { mutableStateOf(false) }
    var isRunnerPaused by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }
    var showFeedbackOverlay by remember { mutableStateOf(false) }

    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    // Read question voice prompt when question loads or when play button tapped
    fun speakCurrentPrompt() {
        audioEngine.speak(currentQuestion.voicePrompt)
    }

    LaunchedEffect(questionIndex, selectedWorldId) {
        isRunningFast = false
        isRunnerPaused = false
        showFeedbackOverlay = false
        delay(300)
        speakCurrentPrompt()
    }

    // Handle Choice Selection
    fun handleChoiceSelected(choice: RunChoiceItem) {
        if (isRunningFast) return // Prevent multi-clicks during dash animation

        if (choice.isCorrect) {
            isRunningFast = true
            isRunnerPaused = false
            feedbackMessage = "Excellent! 🎉"
            showFeedbackOverlay = true

            // Award rewards
            repository.rewardCorrectAnswer()
            repository.addStars(1)
            userStars = repository.getStars()

            audioEngine.speak("Excellent! Great job!")

            coroutineScope.launch {
                delay(1200)
                if (questionIndex + 1 < currentQuestions.size) {
                    questionIndex++
                } else {
                    // Level / World Complete!
                    showConfetti = true
                    repository.rewardFinishGame()
                    repository.addStars(5)
                    userStars = repository.getStars()

                    // Unlock next world
                    if (selectedWorldId < 4 && unlockedWorldId <= selectedWorldId) {
                        unlockedWorldId = selectedWorldId + 1
                    }

                    audioEngine.speak("Super Star! You completed ${currentWorld.name}!")
                    showRewardDialog = true
                }
            }
        } else {
            // Incorrect choice - gentle non-harsh feedback
            isRunnerPaused = true
            feedbackMessage = "Try again! 😊"
            showFeedbackOverlay = true

            audioEngine.speak("Try again! ${currentQuestion.voicePrompt}")

            coroutineScope.launch {
                delay(1800)
                isRunnerPaused = false
                showFeedbackOverlay = false
            }
        }
    }

    // Running scenery backdrop animation loop
    val infiniteTransition = rememberInfiniteTransition(label = "runnerScenery")
    val sceneryOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -300f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isRunningFast) 800 else 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sceneryOffset"
    )

    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isRunnerPaused) 0f else -14f,
        animationSpec = infiniteRepeatable(
            animation = tween(280, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounceOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(colors = currentWorld.skyGradient)
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            KkHeader(
                title = "Run & Learn 🏃",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // World Selection Tabs
            WorldSelectionBar(
                worlds = worlds,
                unlockedWorldId = unlockedWorldId,
                selectedWorldId = selectedWorldId,
                onWorldSelect = { worldId ->
                    if (worldId <= unlockedWorldId) {
                        selectedWorldId = worldId
                        questionIndex = 0
                    } else {
                        audioEngine.speak("Complete previous level to unlock ${worlds.find { it.id == worldId }?.name}!")
                    }
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Level & Progress Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${currentWorld.emoji} ${currentWorld.levelName}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = currentWorld.themeColor
                )

                Text(
                    text = "Question ${questionIndex + 1} / ${currentQuestions.size}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B)
                )
            }

            // Progress Bar Indicator
            LinearProgressIndicator(
                progress = { (questionIndex + 1).toFloat() / currentQuestions.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = currentWorld.themeColor,
                trackColor = Color.White.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Question Banner Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White)
                    .border(3.dp, currentWorld.themeColor, RoundedCornerShape(22.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (currentQuestion.promptEmoji.isNotEmpty()) {
                        Text(
                            text = currentQuestion.promptEmoji,
                            fontSize = 32.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentQuestion.promptText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    IconButton(
                        onClick = { speakCurrentPrompt() },
                        modifier = Modifier
                            .size(42.dp)
                            .background(currentWorld.themeColor, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Speak Question",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Running Canvas Field (Middle Section)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Ground / Track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .align(Alignment.BottomCenter)
                        .background(currentWorld.groundColor)
                )

                // Scrolling Scenery Emojis in Background
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 85.dp)
                        .graphicsLayer { translationX = sceneryOffset },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(3) {
                        currentWorld.sceneryEmojis.forEach { emoji ->
                            Text(text = emoji, fontSize = 36.sp, modifier = Modifier.padding(horizontal = 18.dp))
                        }
                    }
                }

                // Runner Character
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 36.dp, bottom = 45.dp)
                        .graphicsLayer { translationY = bounceOffset }
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = currentWorld.runnerName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = currentWorld.themeColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = currentWorld.runnerEmoji,
                        fontSize = 58.sp
                    )
                }

                // Signpost Choices Floating Ahead
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 20.dp, top = 8.dp)
                        .fillMaxHeight(0.85f),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    currentQuestion.choices.forEach { choice ->
                        Button(
                            onClick = { handleChoiceSelected(choice) },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.58f)
                                .height(56.dp)
                                .border(3.dp, currentWorld.themeColor, RoundedCornerShape(20.dp))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = choice.emoji, fontSize = 26.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = choice.text,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                            }
                        }
                    }
                }

                // Interactive Feedback Toast Banner overlay
                if (showFeedbackOverlay) {
                    Box(modifier = Modifier.align(Alignment.Center)) {
                        Surface(
                            color = if (feedbackMessage.contains("Excellent")) Color(0xFF10B981) else Color(0xFFF59E0B),
                            shape = RoundedCornerShape(20.dp),
                            shadowElevation = 10.dp
                        ) {
                            Text(
                                text = feedbackMessage,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                            )
                        }
                    }
                }
            }
        }

        // Confetti Burst Effect
        ConfettiOverlay(isVisible = showConfetti)

        // Level Complete Star Reward Dialog
        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "${currentWorld.name} Cleared! 🎉",
            message = "Awesome running! You earned 5 Gold Stars & 10 Coins!",
            onNext = {
                showRewardDialog = false
                if (selectedWorldId < 4) {
                    selectedWorldId++
                    questionIndex = 0
                } else {
                    questionIndex = 0
                }
            },
            onHome = {
                showRewardDialog = false
                onBackClick()
            }
        )
    }
}

@Composable
fun WorldSelectionBar(
    worlds: List<RunWorld>,
    unlockedWorldId: Int,
    selectedWorldId: Int,
    onWorldSelect: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(worlds) { _, world ->
            val isUnlocked = world.id <= unlockedWorldId
            val isSelected = world.id == selectedWorldId

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (isSelected) world.themeColor else if (isUnlocked) Color.White else Color(0xFFE2E8F0)
                    )
                    .border(
                        width = if (isSelected) 2.5.dp else 1.dp,
                        color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable { onWorldSelect(world.id) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = world.emoji,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = world.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else if (isUnlocked) Color(0xFF1E293B) else Color(0xFF94A3B8)
                    )

                    if (!isUnlocked) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Locked",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
