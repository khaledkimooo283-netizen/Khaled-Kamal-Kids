package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.HandwritingData
import com.example.data.KkDataRepository
import com.example.data.TracingGuideItem
import com.example.ui.components.*

@Composable
fun DictionaryScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var selectedFilterCategory by remember { mutableStateOf("All (A-Z)") }
    var searchQuery by remember { mutableStateOf("") }

    val allVocabulary = remember { HandwritingData.uppercaseLetters }

    val filteredList = remember(searchQuery, selectedFilterCategory) {
        allVocabulary.filter { item ->
            val matchesSearch = searchQuery.isEmpty() ||
                    item.word.contains(searchQuery, ignoreCase = true) ||
                    item.character.contains(searchQuery, ignoreCase = true)
            matchesSearch
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBEB)) // Warm Cozy Ivory
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "My Dictionary 📖",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Search Bar & Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search word or letter (e.g. Apple, A)...", fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFFD97706),
                        unfocusedBorderColor = Color(0xFFFDE68A)
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Vocabulary Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .weight(1f)
            ) {
                items(filteredList) { item ->
                    DictionaryCard(
                        item = item,
                        audioEngine = audioEngine
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            KkLionMascot(
                state = MascotState.HAPPY,
                speechBubbleText = "Tap any word card to hear pronunciation!",
                onClick = {
                    audioEngine.speak("Welcome to your English Dictionary! Tap any word to learn!")
                }
            )
        }
    }
}

@Composable
fun DictionaryCard(
    item: TracingGuideItem,
    audioEngine: SpeechAndSoundEngine
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(135.dp)
            .clickable {
                audioEngine.speak("${item.character}... ${item.word}!")
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.character,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFB45309)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Speak",
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = item.emoji,
                fontSize = 42.sp
            )

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
