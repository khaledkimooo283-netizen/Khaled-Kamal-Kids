package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.KkDataRepository
import com.example.ui.components.*

data class GroceryItem(
    val id: String,
    val name: String,
    val emoji: String,
    val category: String,
    val color: Color
)

@Composable
fun ShoppingGameScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var currentScore by remember { mutableIntStateOf(0) }

    val allGroceryItems = remember {
        listOf(
            GroceryItem("apple", "Apple", "🍎", "Fruit", Color(0xFFF87171)),
            GroceryItem("banana", "Banana", "🍌", "Fruit", Color(0xFFFDE047)),
            GroceryItem("milk", "Milk", "🥛", "Dairy", Color(0xFF60A5FA)),
            GroceryItem("cheese", "Cheese", "🧀", "Dairy", Color(0xFFFBBF24)),
            GroceryItem("bread", "Bread", "🍞", "Bakery", Color(0xFFD97706)),
            GroceryItem("carrot", "Carrot", "🥕", "Veggies", Color(0xFFFB923C)),
            GroceryItem("cookie", "Cookie", "🍪", "Snacks", Color(0xFFB45309)),
            GroceryItem("juice", "Juice", "🧃", "Drinks", Color(0xFF34D399)),
            GroceryItem("pizza", "Pizza", "🍕", "Food", Color(0xFFEF4444)),
            GroceryItem("icecream", "Ice Cream", "🍦", "Dessert", Color(0xFFEC4899)),
            GroceryItem("grape", "Grape", "🍇", "Fruit", Color(0xFFA855F7)),
            GroceryItem("watermelon", "Watermelon", "🍉", "Fruit", Color(0xFF10B981))
        )
    }

    var currentTarget by remember { mutableStateOf(allGroceryItems.random()) }
    var cartItems by remember { mutableStateOf(listOf<GroceryItem>()) }
    var showRewardDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    val displayOptions = remember(currentTarget) {
        val distractors = allGroceryItems.filter { it.id != currentTarget.id }.shuffled().take(5)
        (distractors + currentTarget).shuffled()
    }

    LaunchedEffect(currentTarget) {
        audioEngine.speak("Find the ${currentTarget.name}! Put it in the cart 🛒")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBEB))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Supermarket Shopping 🛒",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Target Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Shopping List Item:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        )
                        Text(
                            text = "Find the ${currentTarget.name}!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF78350F)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(3.dp, Color(0xFFF59E0B), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🛒", fontSize = 32.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Supermarket Shelves (Items Grid)
            Text(
                text = "Supermarket Shelves 🏪 (Tap item to add to Cart)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF78350F)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayOptions) { item ->
                    Card(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable {
                                audioEngine.speak(item.name)
                                if (item.id == currentTarget.id) {
                                    audioEngine.playCorrectSound()
                                    cartItems = cartItems + item
                                    repository.addStars(2)
                                    userStars = repository.getStars()
                                    currentScore++

                                    if (currentScore >= 4) {
                                        showRewardDialog = true
                                        showConfetti = true
                                    } else {
                                        currentTarget = allGroceryItems.filter { g -> cartItems.none { it.id == g.id } }.random()
                                    }
                                } else {
                                    audioEngine.playWrongSound()
                                    audioEngine.speak("That's ${item.name}! Find the ${currentTarget.name}!")
                                }
                            },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = item.color.copy(alpha = 0.2f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, item.color)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = item.emoji, fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = item.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        }
                    }
                }
            }

            // Shopping Cart Basket Footer
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(12.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🛒 Cart:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        if (cartItems.isEmpty()) {
                            Text("Empty", color = Color(0xFF94A3B8), fontSize = 13.sp)
                        } else {
                            cartItems.forEach { g ->
                                Text(g.emoji + " ", fontSize = 20.sp)
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF10B981)
                    ) {
                        Text(
                            text = "${cartItems.size}/4 Items",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            KkLionMascot(
                state = if (showRewardDialog) MascotState.CELEBRATE else MascotState.HAPPY,
                speechBubbleText = "Help me collect groceries!",
                onClick = { audioEngine.speak("Find the ${currentTarget.name} on the shelf!") }
            )
        }

        StarRewardDialog(
            isVisible = showRewardDialog,
            title = "Super Shopper! 🛒",
            message = "You collected all 4 supermarket items!",
            onNext = {
                showRewardDialog = false
                showConfetti = false
                cartItems = emptyList()
                currentScore = 0
                currentTarget = allGroceryItems.random()
            },
            onHome = onBackClick
        )

        ConfettiOverlay(isVisible = showConfetti)
    }
}
