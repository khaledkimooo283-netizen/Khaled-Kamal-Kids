package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.*
import com.example.ui.components.*
import com.example.util.Localization
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MemoryGameCard(
    val id: Int,
    val pairId: String,
    val letter: Char,
    val word: String,
    val arabicWord: String,
    val emoji: String,
    val phonetic: String,
    val cardType: MemoryCardType, // PICTURE or WORD
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false
)

enum class MemoryCardType {
    PICTURE,
    WORD
}

@Composable
fun MemoryCardsScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val appLanguage by remember { derivedStateOf { repository.getLanguage() } }

    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var userCoins by remember { mutableIntStateOf(repository.getCoins()) }

    var selectedMode by remember { mutableStateOf(MemoryGameMode.PICTURE_PICTURE) }
    var selectedDifficulty by remember { mutableStateOf(MemoryDifficulty.EASY) }
    var selectedLetterGroup by remember { mutableStateOf("ALL") }

    var cards by remember { mutableStateOf(listOf<MemoryGameCard>()) }
    var firstFlippedIndex by remember { mutableStateOf<Int?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    var totalAttempts by remember { mutableIntStateOf(0) }
    var matchesFound by remember { mutableIntStateOf(0) }
    var totalMistakes by remember { mutableIntStateOf(0) }
    var startTimeSeconds by remember { mutableLongStateOf(System.currentTimeMillis() / 1000) }

    var showPairMatchPopup by remember { mutableStateOf<MemoryPairItem?>(null) }
    var showVictoryDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    // Init level cards
    fun initGame() {
        val filteredList = when (selectedLetterGroup) {
            "A-E" -> MemoryCardsData.allAlphabetPairs.filter { it.letter in 'A'..'E' }
            "F-J" -> MemoryCardsData.allAlphabetPairs.filter { it.letter in 'F'..'J' }
            "K-O" -> MemoryCardsData.allAlphabetPairs.filter { it.letter in 'K'..'O' }
            "P-T" -> MemoryCardsData.allAlphabetPairs.filter { it.letter in 'P'..'T' }
            "U-Z" -> MemoryCardsData.allAlphabetPairs.filter { it.letter in 'U'..'Z' }
            else -> MemoryCardsData.allAlphabetPairs
        }

        val chosenPairs = filteredList.shuffled().take(selectedDifficulty.pairCount)
        val cardList = mutableListOf<MemoryGameCard>()
        var idCounter = 0

        chosenPairs.forEach { item ->
            if (selectedMode == MemoryGameMode.PICTURE_PICTURE) {
                // Card 1: Picture
                cardList.add(
                    MemoryGameCard(
                        id = idCounter++,
                        pairId = item.pairId,
                        letter = item.letter,
                        word = item.word,
                        arabicWord = item.arabicWord,
                        emoji = item.emoji,
                        phonetic = item.phonetic,
                        cardType = MemoryCardType.PICTURE
                    )
                )
                // Card 2: Picture
                cardList.add(
                    MemoryGameCard(
                        id = idCounter++,
                        pairId = item.pairId,
                        letter = item.letter,
                        word = item.word,
                        arabicWord = item.arabicWord,
                        emoji = item.emoji,
                        phonetic = item.phonetic,
                        cardType = MemoryCardType.PICTURE
                    )
                )
            } else {
                // Card 1: Picture
                cardList.add(
                    MemoryGameCard(
                        id = idCounter++,
                        pairId = item.pairId,
                        letter = item.letter,
                        word = item.word,
                        arabicWord = item.arabicWord,
                        emoji = item.emoji,
                        phonetic = item.phonetic,
                        cardType = MemoryCardType.PICTURE
                    )
                )
                // Card 2: Word
                cardList.add(
                    MemoryGameCard(
                        id = idCounter++,
                        pairId = item.pairId,
                        letter = item.letter,
                        word = item.word,
                        arabicWord = item.arabicWord,
                        emoji = item.emoji,
                        phonetic = item.phonetic,
                        cardType = MemoryCardType.WORD
                    )
                )
            }
        }

        cards = cardList.shuffled()
        firstFlippedIndex = null
        isProcessing = false
        totalAttempts = 0
        matchesFound = 0
        totalMistakes = 0
        startTimeSeconds = System.currentTimeMillis() / 1000
        showVictoryDialog = false
        showConfetti = false

        val promptSpeech = if (appLanguage == "Arabic") {
            "اقلب الكروت واكتشف الأزواج المتطابقة! 🧠"
        } else {
            "Flip cards to find matching pairs! 🧠"
        }
        audioEngine.speak(promptSpeech)
    }

    LaunchedEffect(selectedMode, selectedDifficulty, selectedLetterGroup) {
        initGame()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF3E5F5), Color(0xFFEDE7F6), Color(0xFFE1BEE7))
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = if (appLanguage == "Arabic") "لعبة الذاكرة 🧠" else "Memory Cards 🧠",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Top Controls Bar (Mode, Difficulty, Reset)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Game Mode Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FilterChip(
                            selected = selectedMode == MemoryGameMode.PICTURE_PICTURE,
                            onClick = { selectedMode = MemoryGameMode.PICTURE_PICTURE },
                            leadingIcon = { Icon(Icons.Filled.Image, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            label = { Text(if (appLanguage == "Arabic") "صورة ↔ صورة" else "Picture ↔ Picture", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )

                        FilterChip(
                            selected = selectedMode == MemoryGameMode.PICTURE_WORD,
                            onClick = { selectedMode = MemoryGameMode.PICTURE_WORD },
                            leadingIcon = { Icon(Icons.Filled.TextFields, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            label = { Text(if (appLanguage == "Arabic") "صورة ↔ كلمة" else "Picture ↔ Word", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Difficulty Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MemoryDifficulty.values().forEach { difficulty ->
                            val labelText = when (difficulty) {
                                MemoryDifficulty.EASY -> if (appLanguage == "Arabic") "سهل (6)" else "Easy (6)"
                                MemoryDifficulty.MEDIUM -> if (appLanguage == "Arabic") "متوسط (12)" else "Medium (12)"
                                MemoryDifficulty.HARD -> if (appLanguage == "Arabic") "صعب (20)" else "Hard (20)"
                            }
                            FilterChip(
                                selected = selectedDifficulty == difficulty,
                                onClick = { selectedDifficulty = difficulty },
                                label = { Text(labelText, fontSize = 11.sp) }
                            )
                        }

                        IconButton(
                            onClick = { initGame() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Reset Game", tint = Color(0xFF6B21A8))
                        }
                    }

                    // Alphabet Group Filter
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val groups = listOf("ALL", "A-E", "F-J", "K-O", "P-T", "U-Z")
                        items(groups) { group ->
                            val isSel = selectedLetterGroup == group
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSel) Color(0xFF9C27B0) else Color(0xFFF3E5F5))
                                    .clickable { selectedLetterGroup = group }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (group == "ALL") (if (appLanguage == "Arabic") "الكل A-Z" else "All A-Z") else group,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else Color(0xFF6A1B9A)
                                )
                            }
                        }
                    }
                }
            }

            // Stats bar (Matches & Attempts)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (appLanguage == "Arabic") "الأزواج: $matchesFound / ${selectedDifficulty.pairCount}" else "Pairs: $matchesFound / ${selectedDifficulty.pairCount}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A148C)
                )

                Text(
                    text = if (appLanguage == "Arabic") "المحاولات: $totalAttempts" else "Attempts: $totalAttempts",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6A1B9A)
                )
            }

            // Cards Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(selectedDifficulty.columns),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                items(cards.size) { index ->
                    val card = cards[index]

                    FlippableCard(
                        card = card,
                        appLanguage = appLanguage,
                        onClick = {
                            if (isProcessing || card.isFlipped || card.isMatched) return@FlippableCard

                            // Flip current card
                            cards = cards.mapIndexed { idx, c ->
                                if (idx == index) c.copy(isFlipped = true) else c
                            }

                            if (firstFlippedIndex == null) {
                                firstFlippedIndex = index
                                // Speak card content
                                val speakText = if (card.cardType == MemoryCardType.PICTURE) {
                                    if (appLanguage == "Arabic") card.arabicWord else card.word
                                } else {
                                    card.word
                                }
                                audioEngine.speak(speakText)
                            } else {
                                val firstIdx = firstFlippedIndex!!
                                val firstCard = cards[firstIdx]
                                totalAttempts++

                                isProcessing = true

                                if (firstCard.pairId == card.pairId) {
                                    // Match found!
                                    matchesFound++
                                    cards = cards.map { c ->
                                        if (c.pairId == card.pairId) c.copy(isMatched = true, isFlipped = true) else c
                                    }

                                    // Praise & Reward
                                    repository.addStars(2)
                                    repository.addCoins(5)
                                    userStars = repository.getStars()
                                    userCoins = repository.getCoins()

                                    val matchItem = MemoryCardsData.allAlphabetPairs.firstOrNull { it.pairId == card.pairId }
                                    showPairMatchPopup = matchItem

                                    val speechMsg = if (appLanguage == "Arabic") {
                                        "رائع! ${card.arabicWord}، حرف ${card.letter}!"
                                    } else {
                                        "Super! ${card.phonetic}!"
                                    }
                                    audioEngine.speak(speechMsg)

                                    coroutineScope.launch {
                                        delay(1600)
                                        showPairMatchPopup = null
                                        firstFlippedIndex = null
                                        isProcessing = false

                                        // Check if level finished
                                        if (cards.all { it.isMatched }) {
                                            val timeSpentSecs = ((System.currentTimeMillis() / 1000) - startTimeSeconds).toInt()
                                            val learnedList = cards.map { it.word }.distinct()
                                            repository.recordMemorySession(
                                                matches = matchesFound,
                                                attempts = totalAttempts,
                                                timeSpentSecs = timeSpentSecs,
                                                wordsLearned = learnedList
                                            )
                                            showConfetti = true
                                            showVictoryDialog = true
                                        }
                                    }
                                } else {
                                    // Mismatch - Delay 1 second so child can inspect both face-up cards!
                                    totalMistakes++
                                    audioEngine.speak(if (appLanguage == "Arabic") "حاول مرة أخرى! 😊" else "Try again! 😊")

                                    coroutineScope.launch {
                                        delay(1000)
                                        cards = cards.mapIndexed { idx, c ->
                                            if (idx == firstIdx || idx == index) c.copy(isFlipped = false) else c
                                        }
                                        firstFlippedIndex = null
                                        isProcessing = false
                                    }
                                }
                            }
                        }
                    )
                }
            }

            // Mascot Guide Footer
            KkLionMascot(
                state = if (showVictoryDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = if (appLanguage == "Arabic") "طابق جميع الصور والكلمات! 🌟" else "Match all pictures & words! 🌟",
                onClick = {
                    audioEngine.speak(if (appLanguage == "Arabic") "اضغط على كارتين لاكتشاف الصور والكلمات المتطابقة!" else "Tap two cards to discover matching pictures and words!")
                }
            )
        }

        // Match Pair Popup Modal
        showPairMatchPopup?.let { pair ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable { showPairMatchPopup = null },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(16.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "✨ ${if (appLanguage == "Arabic") "زوج متطابق!" else "MATCHED!"} ✨",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF16A34A)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Large Emoji / Image
                        Text(text = pair.emoji, fontSize = 72.sp)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = pair.word,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF6B21A8)
                        )

                        Text(
                            text = pair.arabicWord,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A044E)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Letter Badge
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFF3E5F5))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Letter ${pair.letter}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9C27B0)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        IconButton(
                            onClick = {
                                val speakMsg = if (appLanguage == "Arabic") "${pair.arabicWord}، ${pair.word}" else pair.phonetic
                                audioEngine.speak(speakMsg)
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFE9D5FF), CircleShape)
                        ) {
                            Icon(Icons.Filled.VolumeUp, contentDescription = "Pronounce", tint = Color(0xFF7E22CE))
                        }
                    }
                }
            }
        }

        ConfettiOverlay(isVisible = showConfetti)

        // Victory Level Clear Dialog
        if (showVictoryDialog) {
            val accuracyPct = if (totalAttempts > 0) ((matchesFound.toFloat() / totalAttempts.toFloat()) * 100).toInt().coerceIn(0, 100) else 100
            val timeSpentSecs = ((System.currentTimeMillis() / 1000) - startTimeSeconds).toInt()

            AlertDialog(
                onDismissRequest = { showVictoryDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Celebration, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (appLanguage == "Arabic") "عبقري الذاكرة! 🧠" else "Memory Genius! 🧠",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    }
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (appLanguage == "Arabic") "لقد طابقت جميع الكروت بنجاح!" else "You matched all cards perfectly!",
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🎯 Accuracy", fontSize = 11.sp, color = Color.Gray)
                                Text(text = "$accuracyPct%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "⏱️ Time", fontSize = 11.sp, color = Color.Gray)
                                Text(text = "${timeSpentSecs}s", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "⭐ Reward", fontSize = 11.sp, color = Color.Gray)
                                Text(text = "+10 Stars", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showVictoryDialog = false
                            showConfetti = false
                            initGame()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(if (appLanguage == "Arabic") "المستوى التالي 🚀" else "Next Round 🚀", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = {
                            showVictoryDialog = false
                            showConfetti = false
                            onBackClick()
                        },
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(if (appLanguage == "Arabic") "الرئيسية 🏠" else "Home 🏠")
                    }
                }
            )
        }
    }
}

@Composable
fun FlippableCard(
    card: MemoryGameCard,
    appLanguage: String,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFlipped || card.isMatched) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "cardFlip"
    )

    val isFaceUp = rotation > 90f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(105.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = !card.isFlipped && !card.isMatched) { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = when {
                card.isMatched -> Color(0xFFDCFCE7)
                isFaceUp -> Color.White
                else -> Color(0xFF8E24AA)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = if (card.isMatched) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF22C55E)) else null
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isFaceUp) {
                // Mirror content back so text/emoji is not horizontally inverted due to 180deg Y-rotation
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (card.cardType == MemoryCardType.PICTURE) {
                        Text(text = card.emoji, fontSize = 34.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (appLanguage == "Arabic") card.arabicWord else card.word,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A148C),
                            maxLines = 1
                        )
                    } else {
                        // WORD Card Type
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF3E5F5))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Letter ${card.letter}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8E24AA)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = card.word.uppercase(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF6B21A8)
                        )
                        if (appLanguage == "Arabic") {
                            Text(
                                text = card.arabicWord,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A044E)
                            )
                        }
                    }
                }
            } else {
                // Card Back face
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "❓", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "KK KIDS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}
