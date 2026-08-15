package com.example.ui.games.learningworld

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.*
import com.example.ui.components.ConfettiOverlay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun WorldActivityScreen(
    world: WorldEnvironment,
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackToMap: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var currentActivity by remember { mutableStateOf(LearningActivityType.EXPLORE) }
    var leoPose by remember { mutableStateOf(LeoPose.HAPPY) }
    var leoSpeechText by remember { mutableStateOf(world.leoGreeting) }

    var showConfetti by remember { mutableStateOf(false) }
    var activityScore by remember { mutableIntStateOf(0) }
    val totalRounds = 5

    // Initial greeting from Leo
    LaunchedEffect(world.id) {
        audioEngine.speak(world.leoGreeting)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(world.themeColorHex).copy(alpha = 0.22f),
                        Color(0xFFF8FAFC),
                        Color(world.accentColorHex).copy(alpha = 0.15f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        audioEngine.playClickSound()
                        onBackToMap()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.5.dp, Color(0xFFCBD5E1), CircleShape)
                        .testTag("back_to_map_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to World Map",
                        tint = Color(0xFF1E293B)
                    )
                }

                // Title Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(world.emoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = world.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                }

                // Stars Display
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFEF3C7),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF59E0B))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⭐", fontSize = 15.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${repository.getLearningWorldStars(world.id)}/3",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        )
                    }
                }
            }

            // Activity Mode Selector Tabs (Scrollable Row of Pills)
            ScrollableTabRow(
                selectedTabIndex = currentActivity.ordinal,
                edgePadding = 12.dp,
                divider = {},
                containerColor = Color.Transparent,
                indicator = {}
            ) {
                LearningActivityType.values().forEach { actType ->
                    val isSelected = currentActivity == actType
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                            .clickable {
                                audioEngine.playClickSound()
                                currentActivity = actType
                                when (actType) {
                                    LearningActivityType.EXPLORE -> {
                                        leoSpeechText = "Tap any object to hear its English name!"
                                        leoPose = LeoPose.HAPPY
                                        audioEngine.speak(leoSpeechText)
                                    }
                                    LearningActivityType.FIND_IT -> {
                                        leoPose = LeoPose.THINKING
                                    }
                                    LearningActivityType.DRAG_INTO_TARGET -> {
                                        leoPose = LeoPose.POINTING
                                    }
                                    LearningActivityType.CHOOSE_ONE -> {
                                        leoPose = LeoPose.THINKING
                                    }
                                    LearningActivityType.LISTEN_CHOOSE -> {
                                        leoPose = LeoPose.TALKING
                                    }
                                    LearningActivityType.ACTION_FUN -> {
                                        leoSpeechText = "Let's practice action words!"
                                        leoPose = LeoPose.EXCITED
                                        audioEngine.speak(leoSpeechText)
                                    }
                                    LearningActivityType.SORTING -> {
                                        leoSpeechText = "Can you sort the items into the right box?"
                                        leoPose = LeoPose.THINKING
                                        audioEngine.speak(leoSpeechText)
                                    }
                                    LearningActivityType.MEMORY_MATCH -> {
                                        leoSpeechText = "Find the matching pictures!"
                                        leoPose = LeoPose.HAPPY
                                        audioEngine.speak(leoSpeechText)
                                    }
                                }
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) Color(world.themeColorHex) else Color.White,
                        shadowElevation = if (isSelected) 4.dp else 1.dp,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(actType.emoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = actType.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF475569)
                            )
                        }
                    }
                }
            }

            // Leo Companion Header Widget
            LeoCompanion(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                pose = leoPose,
                speechText = leoSpeechText,
                onLeoClick = {
                    audioEngine.playClickSound()
                    audioEngine.speak(leoSpeechText)
                },
                onSpeakClick = {
                    audioEngine.speak(leoSpeechText)
                }
            )

            // Dynamic Mini Activity Content Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                when (currentActivity) {
                    LearningActivityType.EXPLORE -> {
                        ExploreActivity(
                            world = world,
                            audioEngine = audioEngine,
                            repository = repository,
                            onItemTapped = { item ->
                                leoPose = LeoPose.TALKING
                                leoSpeechText = "${item.word.replaceFirstChar { it.uppercase() }}! ${item.phrase}"
                                audioEngine.speak(leoSpeechText)
                                repository.addLearnedWords(listOf(item.word))
                            }
                        )
                    }

                    LearningActivityType.FIND_IT -> {
                        FindItActivity(
                            world = world,
                            audioEngine = audioEngine,
                            repository = repository,
                            onUpdateLeo = { text, pose ->
                                leoSpeechText = text
                                leoPose = pose
                            },
                            onRoundSuccess = {
                                activityScore++
                                showConfetti = true
                                repository.rewardCorrectAnswer()
                                repository.addStars(1)
                                repository.setLearningWorldStars(world.id, 2)
                                coroutineScope.launch {
                                    delay(1800)
                                    showConfetti = false
                                }
                            }
                        )
                    }

                    LearningActivityType.DRAG_INTO_TARGET -> {
                        DragIntoTargetActivity(
                            world = world,
                            audioEngine = audioEngine,
                            repository = repository,
                            onUpdateLeo = { text, pose ->
                                leoSpeechText = text
                                leoPose = pose
                            },
                            onDragSuccess = {
                                showConfetti = true
                                repository.rewardCorrectAnswer()
                                repository.addStars(1)
                                repository.setLearningWorldStars(world.id, 3)
                                coroutineScope.launch {
                                    delay(1800)
                                    showConfetti = false
                                }
                            }
                        )
                    }

                    LearningActivityType.CHOOSE_ONE -> {
                        ChooseOneActivity(
                            world = world,
                            audioEngine = audioEngine,
                            repository = repository,
                            onUpdateLeo = { text, pose ->
                                leoSpeechText = text
                                leoPose = pose
                            },
                            onCorrectChoice = {
                                showConfetti = true
                                repository.rewardCorrectAnswer()
                                repository.addStars(1)
                                coroutineScope.launch {
                                    delay(1800)
                                    showConfetti = false
                                }
                            }
                        )
                    }

                    LearningActivityType.LISTEN_CHOOSE -> {
                        ListenAndChooseActivity(
                            world = world,
                            audioEngine = audioEngine,
                            repository = repository,
                            onUpdateLeo = { text, pose ->
                                leoSpeechText = text
                                leoPose = pose
                            },
                            onCorrectChoice = {
                                showConfetti = true
                                repository.rewardCorrectAnswer()
                                repository.addStars(1)
                                coroutineScope.launch {
                                    delay(1800)
                                    showConfetti = false
                                }
                            }
                        )
                    }

                    LearningActivityType.ACTION_FUN -> {
                        ActionFunActivity(
                            world = world,
                            audioEngine = audioEngine,
                            repository = repository,
                            onActionTapped = { verbItem ->
                                leoPose = LeoPose.EXCITED
                                leoSpeechText = "${verbItem.verb.uppercase()}! ${verbItem.phrase}"
                                audioEngine.speak(leoSpeechText)
                                repository.addStars(1)
                            }
                        )
                    }

                    LearningActivityType.SORTING -> {
                        SortingActivity(
                            world = world,
                            audioEngine = audioEngine,
                            repository = repository,
                            onUpdateLeo = { text, pose ->
                                leoSpeechText = text
                                leoPose = pose
                            },
                            onSortComplete = {
                                showConfetti = true
                                repository.rewardFinishGame()
                                repository.addStars(2)
                                coroutineScope.launch {
                                    delay(2000)
                                    showConfetti = false
                                }
                            }
                        )
                    }

                    LearningActivityType.MEMORY_MATCH -> {
                        MemoryMatchActivity(
                            world = world,
                            audioEngine = audioEngine,
                            repository = repository,
                            onUpdateLeo = { text, pose ->
                                leoSpeechText = text
                                leoPose = pose
                            },
                            onGameFinished = {
                                showConfetti = true
                                repository.rewardFinishGame()
                                repository.addStars(2)
                                repository.setLearningWorldStars(world.id, 3)
                                coroutineScope.launch {
                                    delay(2200)
                                    showConfetti = false
                                }
                            }
                        )
                    }
                }
            }
        }

        // Confetti Celebration
        ConfettiOverlay(
            isVisible = showConfetti
        )
    }
}

// -------------------------------------------------------------------------------------------------
// 1. EXPLORE & TAP ACTIVITY
// -------------------------------------------------------------------------------------------------
@Composable
fun ExploreActivity(
    world: WorldEnvironment,
    audioEngine: SpeechAndSoundEngine,
    repository: KkDataRepository,
    onItemTapped: (LearningVocabItem) -> Unit
) {
    var selectedItem by remember { mutableStateOf<LearningVocabItem?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(world.vocabList) { item ->
                val isCurrent = selectedItem?.id == item.id
                val scale by animateFloatAsState(
                    targetValue = if (isCurrent) 1.08f else 1.0f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                    label = "itemScale"
                )

                Surface(
                    modifier = Modifier
                        .scale(scale)
                        .aspectRatio(1f)
                        .clickable {
                            audioEngine.playClickSound()
                            selectedItem = item
                            onItemTapped(item)
                        }
                        .testTag("explore_item_${item.id}"),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = if (isCurrent) 8.dp else 2.dp,
                    border = if (isCurrent) {
                        androidx.compose.foundation.BorderStroke(3.dp, Color(item.colorHex))
                    } else {
                        androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(item.emoji, fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.word,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 2. FIND IT ACTIVITY
// -------------------------------------------------------------------------------------------------
@Composable
fun FindItActivity(
    world: WorldEnvironment,
    audioEngine: SpeechAndSoundEngine,
    repository: KkDataRepository,
    onUpdateLeo: (String, LeoPose) -> Unit,
    onRoundSuccess: () -> Unit
) {
    var roundIndex by remember { mutableIntStateOf(0) }
    val candidateItems = remember(world.id) { world.vocabList.shuffled() }
    val currentTarget = remember(roundIndex, world.id) {
        candidateItems[roundIndex % candidateItems.size]
    }

    // Pick 4 options including the target
    val currentOptions = remember(currentTarget) {
        val others = world.vocabList.filter { it.id != currentTarget.id }.shuffled().take(3)
        (others + currentTarget).shuffled()
    }

    LaunchedEffect(currentTarget) {
        val prompt = "Find the ${currentTarget.word}!"
        onUpdateLeo(prompt, LeoPose.THINKING)
        audioEngine.speak(prompt)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Where is the ${currentTarget.word}?",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(currentOptions) { item ->
                Surface(
                    modifier = Modifier
                        .aspectRatio(1.1f)
                        .clickable {
                            if (item.id == currentTarget.id) {
                                audioEngine.playCorrectSound()
                                onUpdateLeo("Great! That's the ${currentTarget.word}!", LeoPose.CELEBRATING)
                                onRoundSuccess()
                                roundIndex++
                            } else {
                                audioEngine.playWrongSound()
                                onUpdateLeo("That's ${item.word}. Try again to find ${currentTarget.word}!", LeoPose.GENTLE_CORRECTION)
                            }
                        }
                        .testTag("find_it_option_${item.id}"),
                    shape = RoundedCornerShape(22.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(item.colorHex).copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(item.emoji, fontSize = 46.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = item.word,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 3. DRAG INTO TARGET ACTIVITY
// -------------------------------------------------------------------------------------------------
@Composable
fun DragIntoTargetActivity(
    world: WorldEnvironment,
    audioEngine: SpeechAndSoundEngine,
    repository: KkDataRepository,
    onUpdateLeo: (String, LeoPose) -> Unit,
    onDragSuccess: () -> Unit
) {
    var itemIndex by remember { mutableIntStateOf(0) }
    val itemsToDrag = remember(world.id) { world.vocabList.shuffled() }
    val currentDragItem = remember(itemIndex, world.id) {
        itemsToDrag[itemIndex % itemsToDrag.size]
    }

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(currentDragItem) {
        val prompt = "Put the ${currentDragItem.word} into the ${world.targetContainerName}!"
        onUpdateLeo(prompt, LeoPose.POINTING)
        audioEngine.speak(prompt)
        offsetX = 0f
        offsetY = 0f
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Drag the ${currentDragItem.word} into the ${world.targetContainerName} 👇",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Draggable Item
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .size(110.dp)
                .shadow(8.dp, RoundedCornerShape(24.dp))
                .background(Color.White, RoundedCornerShape(24.dp))
                .border(3.dp, Color(currentDragItem.colorHex), RoundedCornerShape(24.dp))
                .pointerInput(currentDragItem.id) {
                    detectDragGestures(
                        onDragEnd = {
                            // If dragged down towards the target container
                            if (offsetY > 120f) {
                                audioEngine.playCorrectSound()
                                onUpdateLeo("Awesome! ${currentDragItem.word} is in the ${world.targetContainerName}!", LeoPose.CELEBRATING)
                                onDragSuccess()
                                itemIndex++
                            } else {
                                // Snap back
                                offsetX = 0f
                                offsetY = 0f
                            }
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(currentDragItem.emoji, fontSize = 42.sp)
                Text(
                    text = currentDragItem.word,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }
        }

        // Target Container Box
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(130.dp)
                .padding(bottom = 12.dp)
                .clickable {
                    // Tap alternative for ease of access
                    audioEngine.playCorrectSound()
                    onUpdateLeo("Great! ${currentDragItem.word} put inside!", LeoPose.CELEBRATING)
                    onDragSuccess()
                    itemIndex++
                },
            shape = RoundedCornerShape(26.dp),
            color = Color(world.themeColorHex).copy(alpha = 0.18f),
            border = androidx.compose.foundation.BorderStroke(3.dp, Color(world.themeColorHex))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(world.targetContainerEmoji, fontSize = 48.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Drop here: ${world.targetContainerName}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 4. CHOOSE ONE ACTIVITY (WHICH ONE IS...)
// -------------------------------------------------------------------------------------------------
@Composable
fun ChooseOneActivity(
    world: WorldEnvironment,
    audioEngine: SpeechAndSoundEngine,
    repository: KkDataRepository,
    onUpdateLeo: (String, LeoPose) -> Unit,
    onCorrectChoice: () -> Unit
) {
    var roundIdx by remember { mutableIntStateOf(0) }
    val vocab = remember(world.id) { world.vocabList.shuffled() }
    val target = remember(roundIdx, world.id) { vocab[roundIdx % vocab.size] }

    val options = remember(target) {
        val others = world.vocabList.filter { it.id != target.id }.shuffled().take(2)
        (others + target).shuffled()
    }

    LaunchedEffect(target) {
        val prompt = "Which one is a ${target.word}?"
        onUpdateLeo(prompt, LeoPose.THINKING)
        audioEngine.speak(prompt)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Which one is a ${target.word}?",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            options.forEach { opt ->
                Surface(
                    modifier = Modifier
                        .size(105.dp)
                        .clickable {
                            if (opt.id == target.id) {
                                audioEngine.playCorrectSound()
                                onUpdateLeo("Yes! ${target.word}!", LeoPose.CELEBRATING)
                                onCorrectChoice()
                                roundIdx++
                            } else {
                                audioEngine.playWrongSound()
                                onUpdateLeo("That is ${opt.word}. Try again!", LeoPose.GENTLE_CORRECTION)
                            }
                        },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 5.dp,
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(opt.colorHex).copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(opt.emoji, fontSize = 44.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 5. LISTEN & CHOOSE ACTIVITY
// -------------------------------------------------------------------------------------------------
@Composable
fun ListenAndChooseActivity(
    world: WorldEnvironment,
    audioEngine: SpeechAndSoundEngine,
    repository: KkDataRepository,
    onUpdateLeo: (String, LeoPose) -> Unit,
    onCorrectChoice: () -> Unit
) {
    var roundIdx by remember { mutableIntStateOf(0) }
    val vocab = remember(world.id) { world.vocabList.shuffled() }
    val target = remember(roundIdx, world.id) { vocab[roundIdx % vocab.size] }

    val options = remember(target) {
        val others = world.vocabList.filter { it.id != target.id }.shuffled().take(2)
        (others + target).shuffled()
    }

    LaunchedEffect(target) {
        val prompt = target.word.replaceFirstChar { it.uppercase() }
        onUpdateLeo("Listen: \"$prompt\" 🎧 Tap the right picture!", LeoPose.TALKING)
        audioEngine.speak(target.audioPrompt)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Big Audio Replay Button
        Button(
            onClick = {
                audioEngine.playClickSound()
                audioEngine.speak(target.audioPrompt)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Play Word")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Listen Again", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            options.forEach { opt ->
                Surface(
                    modifier = Modifier
                        .size(105.dp)
                        .clickable {
                            if (opt.id == target.id) {
                                audioEngine.playCorrectSound()
                                onUpdateLeo("Super! ${target.word}!", LeoPose.CELEBRATING)
                                onCorrectChoice()
                                roundIdx++
                            } else {
                                audioEngine.playWrongSound()
                                onUpdateLeo("That was ${opt.word}. Listen carefully!", LeoPose.GENTLE_CORRECTION)
                                audioEngine.speak(target.audioPrompt)
                            }
                        },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 5.dp,
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(opt.colorHex).copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(opt.emoji, fontSize = 44.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 6. ACTION VERB FUN ACTIVITY
// -------------------------------------------------------------------------------------------------
@Composable
fun ActionFunActivity(
    world: WorldEnvironment,
    audioEngine: SpeechAndSoundEngine,
    repository: KkDataRepository,
    onActionTapped: (ActionVerbItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(world.verbs) { verbItem ->
            Surface(
                modifier = Modifier
                    .aspectRatio(1.3f)
                    .clickable {
                        audioEngine.playClickSound()
                        onActionTapped(verbItem)
                    },
                shape = RoundedCornerShape(22.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF6366F1).copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(verbItem.emoji, fontSize = 38.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = verbItem.verb.uppercase(),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4F46E5)
                    )
                    Text(
                        text = verbItem.phrase,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 7. CATEGORY SORTING ACTIVITY
// -------------------------------------------------------------------------------------------------
@Composable
fun SortingActivity(
    world: WorldEnvironment,
    audioEngine: SpeechAndSoundEngine,
    repository: KkDataRepository,
    onUpdateLeo: (String, LeoPose) -> Unit,
    onSortComplete: () -> Unit
) {
    var roundIdx by remember { mutableIntStateOf(0) }
    val items = remember(world.id) { world.vocabList.shuffled().take(6) }
    val currentItem = remember(roundIdx, world.id) { items.getOrNull(roundIdx) }

    if (currentItem == null) {
        LaunchedEffect(Unit) {
            onUpdateLeo("Fantastic sorting! You completed the world!", LeoPose.CELEBRATING)
            onSortComplete()
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("🎉 All items sorted! Great job!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        return
    }

    LaunchedEffect(currentItem) {
        val prompt = "Sort the ${currentItem.word}: ${world.sortCategoryA} or ${world.sortCategoryB}?"
        onUpdateLeo(prompt, LeoPose.THINKING)
        audioEngine.speak(prompt)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Item to Sort:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B)
        )

        // Current Item Card
        Surface(
            modifier = Modifier.size(110.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 6.dp,
            border = androidx.compose.foundation.BorderStroke(3.dp, Color(currentItem.colorHex))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(currentItem.emoji, fontSize = 46.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentItem.word,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }
        }

        // Two Category Choice Bins
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Bin A
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(110.dp)
                    .padding(horizontal = 8.dp)
                    .clickable {
                        audioEngine.playCorrectSound()
                        onUpdateLeo("Good choice! ${currentItem.word} placed!", LeoPose.HAPPY)
                        roundIdx++
                    },
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFE0E7FF),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF6366F1))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("📦", fontSize = 32.sp)
                    Text(
                        text = world.sortCategoryA,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3730A3)
                    )
                }
            }

            // Bin B
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(110.dp)
                    .padding(horizontal = 8.dp)
                    .clickable {
                        audioEngine.playCorrectSound()
                        onUpdateLeo("Good choice! ${currentItem.word} placed!", LeoPose.HAPPY)
                        roundIdx++
                    },
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFFEF3C7),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFF59E0B))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("🧺", fontSize = 32.sp)
                    Text(
                        text = world.sortCategoryB,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 8. MEMORY MATCH ACTIVITY (4 CARDS / 2 PAIRS)
// -------------------------------------------------------------------------------------------------
data class MemoryCardState(
    val id: Int,
    val vocabId: String,
    val emoji: String,
    val word: String,
    val isRevealed: Boolean = false,
    val isMatched: Boolean = false
)

@Composable
fun MemoryMatchActivity(
    world: WorldEnvironment,
    audioEngine: SpeechAndSoundEngine,
    repository: KkDataRepository,
    onUpdateLeo: (String, LeoPose) -> Unit,
    onGameFinished: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val pickedVocab = remember(world.id) { world.vocabList.shuffled().take(2) }

    var cards by remember(world.id) {
        val deck = (pickedVocab + pickedVocab).shuffled().mapIndexed { idx, item ->
            MemoryCardState(
                id = idx,
                vocabId = item.id,
                emoji = item.emoji,
                word = item.word
            )
        }
        mutableStateOf(deck)
    }

    var selectedFirst by remember { mutableStateOf<Int?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    val allMatched = cards.all { it.isMatched }

    LaunchedEffect(allMatched) {
        if (allMatched && cards.isNotEmpty()) {
            onUpdateLeo("Amazing memory! You matched all pairs!", LeoPose.CELEBRATING)
            audioEngine.playVictorySound()
            onGameFinished()
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(cards) { card ->
            Surface(
                modifier = Modifier
                    .aspectRatio(1.1f)
                    .clickable(enabled = !card.isMatched && !card.isRevealed && !isProcessing) {
                        audioEngine.playClickSound()
                        if (selectedFirst == null) {
                            selectedFirst = card.id
                            cards = cards.map { if (it.id == card.id) it.copy(isRevealed = true) else it }
                            audioEngine.speak(card.word)
                        } else {
                            val firstId = selectedFirst!!
                            val firstCard = cards.find { it.id == firstId }
                            cards = cards.map { if (it.id == card.id) it.copy(isRevealed = true) else it }
                            audioEngine.speak(card.word)

                            if (firstCard != null && firstCard.vocabId == card.vocabId) {
                                // Match!
                                audioEngine.playCorrectSound()
                                onUpdateLeo("Match! ${card.word}!", LeoPose.HAPPY)
                                cards = cards.map {
                                    if (it.vocabId == card.vocabId) it.copy(isMatched = true) else it
                                }
                                selectedFirst = null
                            } else {
                                // Mismatch
                                isProcessing = true
                                onUpdateLeo("Not a match. Try again!", LeoPose.GENTLE_CORRECTION)
                                coroutineScope.launch {
                                    delay(1000)
                                    cards = cards.map {
                                        if (it.id == firstId || it.id == card.id) it.copy(isRevealed = false) else it
                                    }
                                    selectedFirst = null
                                    isProcessing = false
                                }
                            }
                        }
                    },
                shape = RoundedCornerShape(22.dp),
                color = if (card.isRevealed || card.isMatched) Color.White else Color(world.themeColorHex),
                shadowElevation = 5.dp,
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(world.accentColorHex))
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (card.isRevealed || card.isMatched) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(card.emoji, fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(card.word, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text("❓", fontSize = 36.sp)
                    }
                }
            }
        }
    }
}
