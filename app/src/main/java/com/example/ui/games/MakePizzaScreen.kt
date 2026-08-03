package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.ui.components.*
import kotlin.random.Random

data class PizzaIngredient(
    val id: String,
    val singularName: String,
    val pluralName: String,
    val emoji: String,
    val colorHex: Long = 0xFFEF4444
)

data class PlacedTopping(
    val ingredientId: String,
    val emoji: String,
    val offsetXFraction: Float,
    val offsetYFraction: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakePizzaScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var userCoins by remember { mutableIntStateOf(repository.getCoins()) }

    var selectedDifficulty by remember { mutableStateOf("Easy") } // "Easy" (1-5), "Medium" (1-10), "Hard" (1-20)

    val ingredients = remember {
        listOf(
            PizzaIngredient("tomato", "Tomato", "Tomatoes", "🍅", 0xFFEF4444),
            PizzaIngredient("cheese", "Cheese", "Cheese", "🧀", 0xFFEAB308),
            PizzaIngredient("mushroom", "Mushroom", "Mushrooms", "🍄", 0xFF8D6E63),
            PizzaIngredient("pepper", "Pepper", "Peppers", "🫑", 0xFF22C55E),
            PizzaIngredient("olive", "Olive", "Olives", "🫒", 0xFF15803D),
            PizzaIngredient("onion", "Onion", "Onions", "🧅", 0xFFA855F7),
            PizzaIngredient("corn", "Corn", "Corn", "🌽", 0xFFF59E0B),
            PizzaIngredient("chicken", "Chicken", "Chicken", "🍗", 0xFFD97706),
            PizzaIngredient("pepperoni", "Pepperoni", "Pepperoni", "🍕", 0xFFDC2626),
            PizzaIngredient("pineapple", "Pineapple", "Pineapples", "🍍", 0xFFFACC15)
        )
    }

    var targetIngredient by remember { mutableStateOf(ingredients[0]) }
    var targetCount by remember { mutableIntStateOf(2) }

    var placedToppings by remember { mutableStateOf(listOf<PlacedTopping>()) }
    var ordersCompleted by remember { mutableIntStateOf(0) }

    var showConfetti by remember { mutableStateOf(false) }
    var showVictoryDialog by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }

    val numberWords = remember {
        mapOf(
            1 to "ONE", 2 to "TWO", 3 to "THREE", 4 to "FOUR", 5 to "FIVE",
            6 to "SIX", 7 to "SEVEN", 8 to "EIGHT", 9 to "NINE", 10 to "TEN",
            11 to "ELEVEN", 12 to "TWELVE", 13 to "THIRTEEN", 14 to "FOURTEEN", 15 to "FIFTEEN",
            16 to "SIXTEEN", 17 to "SEVENTEEN", 18 to "EIGHTEEN", 19 to "NINETEEN", 20 to "TWENTY"
        )
    }

    fun numberToWord(num: Int): String {
        return numberWords[num] ?: num.toString()
    }

    fun generateNewOrder() {
        targetIngredient = ingredients.random()
        targetCount = when (selectedDifficulty) {
            "Medium" -> (1..10).random()
            "Hard" -> (1..20).random()
            else -> (1..5).random()
        }
        placedToppings = emptyList()

        val ingredientLabel = if (targetCount == 1) targetIngredient.singularName else targetIngredient.pluralName
        val orderPrompt = "Put ${numberToWord(targetCount)} $ingredientLabel on the pizza!"
        feedbackMessage = orderPrompt

        audioEngine.speak(orderPrompt)
    }

    LaunchedEffect(selectedDifficulty) {
        generateNewOrder()
    }

    // Current placed count of target ingredient
    val currentTargetPlacedCount = placedToppings.count { it.ingredientId == targetIngredient.id }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF7ED),
                        Color(0xFFFFEDD5),
                        Color(0xFFFED7AA)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = "Make Your Pizza 🍕",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Difficulty Selection Bar
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Difficulty:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFFC2410C)
                )
                listOf("Easy", "Medium", "Hard").forEach { diff ->
                    val isSelected = selectedDifficulty == diff
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedDifficulty = diff },
                        label = {
                            Text(
                                text = diff,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFEA580C),
                            selectedLabelColor = Color.White,
                            containerColor = Color.White.copy(alpha = 0.8f),
                            labelColor = Color(0xFFC2410C)
                        )
                    )
                }
            }

            // Chef Order Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Chef Avatar
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFEDD5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👨‍🍳", fontSize = 34.sp)
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "Chef's Order:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEA580C)
                        )
                        val nameStr = if (targetCount == 1) targetIngredient.singularName else targetIngredient.pluralName
                        Text(
                            text = "Put ${targetCount} ${targetIngredient.emoji} ${nameStr}!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF9A3412)
                        )
                    }

                    // Replay Audio Order
                    IconButton(
                        onClick = {
                            val nameStr = if (targetCount == 1) targetIngredient.singularName else targetIngredient.pluralName
                            audioEngine.speak("Put ${numberToWord(targetCount)} $nameStr!")
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFFFEDD5), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speak Order",
                            tint = Color(0xFFEA580C)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pizza Crust Interactive Canvas Container
            Box(
                modifier = Modifier
                    .size(270.dp)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFFD97706)), // Pizza Crust Outer Edge
                contentAlignment = Alignment.Center
            ) {
                // Pizza Sauce & Cheese Layer
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.88f)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFACC15), // Yellow Cheese Center
                                    Color(0xFFF59E0B), // Melted Cheese
                                    Color(0xFFEF4444)  // Tomato Sauce Rim
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Render Toppings Placed on Pizza
                    placedToppings.forEach { topping ->
                        val xOffset = topping.offsetXFraction * 100.dp.value
                        val yOffset = topping.offsetYFraction * 100.dp.value

                        Box(
                            modifier = Modifier
                                .offset(x = xOffset.dp, y = yOffset.dp)
                                .size(36.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(topping.emoji, fontSize = 24.sp)
                        }
                    }
                }

                // Pizza Topping Counter Badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.92f),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${targetIngredient.emoji} $currentTargetPlacedCount / $targetCount",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = if (currentTargetPlacedCount == targetCount) Color(0xFF16A34A) else Color(0xFFEA580C)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Topping Tray Bar
            Text(
                text = "Tap ingredients to add to Pizza:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9A3412)
            )

            Spacer(modifier = Modifier.height(4.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ingredients) { ing ->
                    val isTarget = ing.id == targetIngredient.id
                    Card(
                        onClick = {
                            // Add Topping with Random Pizza Offset
                            val angle = Random.nextFloat() * 2 * Math.PI
                            val radius = Random.nextFloat() * 0.7f
                            val xFrac = (radius * Math.cos(angle)).toFloat()
                            val yFrac = (radius * Math.sin(angle)).toFloat()

                            placedToppings = placedToppings + PlacedTopping(ing.id, ing.emoji, xFrac, yFrac)

                            val countForIng = placedToppings.count { it.ingredientId == ing.id }
                            val nameSpoken = if (countForIng == 1) ing.singularName else ing.pluralName
                            audioEngine.speak("${numberToWord(countForIng)} $nameSpoken!")
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isTarget) Color(0xFFFFEDD5) else Color.White
                        ),
                        border = if (isTarget) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFEA580C)) else null,
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(ing.emoji, fontSize = 28.sp)
                            Text(
                                ing.singularName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF431407)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons: Clear & Bake / Serve
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        placedToppings = emptyList()
                        audioEngine.speak("Cleared toppings!")
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear Pizza", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        val countOnPizza = placedToppings.count { it.ingredientId == targetIngredient.id }
                        if (countOnPizza == targetCount) {
                            showConfetti = true
                            repository.rewardCorrectAnswer()
                            repository.addStars(2)
                            userStars = repository.getStars()
                            userCoins = repository.getCoins()

                            ordersCompleted++
                            val nameSpoken = if (targetCount == 1) targetIngredient.singularName else targetIngredient.pluralName
                            audioEngine.speak("Excellent! $targetCount $nameSpoken! Delicious pizza!")
                            feedbackMessage = "🎉 Chef says: Excellent! $targetCount $nameSpoken!"

                            if (ordersCompleted >= 5) {
                                repository.rewardFinishGame()
                                userStars = repository.getStars()
                                userCoins = repository.getCoins()
                                showVictoryDialog = true
                            } else {
                                generateNewOrder()
                            }
                        } else {
                            val nameSpoken = if (targetCount == 1) targetIngredient.singularName else targetIngredient.pluralName
                            audioEngine.speak("Try again! Chef asked for ${numberToWord(targetCount)} $nameSpoken!")
                            feedbackMessage = "❌ Try again! Put exactly $targetCount $nameSpoken!"
                        }
                    },
                    modifier = Modifier.weight(1.3f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C))
                ) {
                    Icon(imageVector = Icons.Default.LocalPizza, contentDescription = "Serve")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Bake & Serve 🍕", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }

        ConfettiOverlay(isVisible = showConfetti)

        StarRewardDialog(
            isVisible = showVictoryDialog,
            title = "Master Pizza Chef! 🍕👨‍🍳",
            message = "You cooked 5 delicious pizzas! You earned 10 Coins and 5 Stars!",
            onNext = {
                showVictoryDialog = false
                ordersCompleted = 0
                generateNewOrder()
            },
            onHome = onBackClick
        )
    }
}
