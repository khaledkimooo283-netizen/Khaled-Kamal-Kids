package com.example.ui.games.learningworld

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.*
import com.example.ui.components.Character3DFigurine
import com.example.ui.components.ConfettiOverlay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*

enum class MoveDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

@Composable
private fun Real3DAdventureArena(
    character: LetterCharacter,
    currentMissionIndex: Int,
    onMissionComplete: (CharacterMission) -> Unit,
    onNextMission: () -> Unit,
    audioEngine: SpeechAndSoundEngine
) {
    val coroutineScope = rememberCoroutineScope()
    val missions = character.missions
    val mission = missions.getOrElse(currentMissionIndex) { missions.first() }

    // Mission Stage State (0: Initial, 1: Secondary/Carrying/Throwing, 2: Pulling/Placed, 3: Completed)
    var missionStage by remember(character.letter, currentMissionIndex) { mutableIntStateOf(0) }
    var hasRope by remember(character.letter, currentMissionIndex) { mutableStateOf(false) }
    var isCarryingItem by remember(character.letter, currentMissionIndex) { mutableStateOf(false) }
    var missionFinished by remember(character.letter, currentMissionIndex) { mutableStateOf(false) }

    // Character State in 3D Arena (normalized coordinates: -1.0 to +1.0)
    var charX by remember(character.letter, currentMissionIndex) { mutableFloatStateOf(0f) }
    var charY by remember(character.letter, currentMissionIndex) { mutableFloatStateOf(0.35f) }
    var facingAngle by remember { mutableFloatStateOf(0f) }
    var actionState by remember { mutableStateOf(CharacterActionState.IDLE) }
    var isJumping by remember { mutableStateOf(false) }

    // Active Directional Button Press State
    var activeDirection by remember { mutableStateOf<MoveDirection?>(null) }

    // Animation & Physics Timers
    val infiniteTransition = rememberInfiniteTransition(label = "arena3DAnim")
    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "animTime"
    )
    val cloudOffset by infiniteTransition.animateFloat(
        initialValue = -40f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloudOffset"
    )
    val waterRipple by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waterRipple"
    )
    val targetPulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "targetPulse"
    )

    // Dynamic Positions for Specific Mission Mechanics
    val ropeOriginX = 0.32f
    val ropeOriginY = 0.26f
    val antWaterX = -0.62f
    val antWaterY = -0.28f
    val waterRescueSpotX = -0.34f
    val waterRescueSpotY = -0.18f

    // Ant Position in Rescue Mission (smooth pull transition from water to safe grass)
    val antPullProgress = remember(character.letter, currentMissionIndex) { Animatable(0f) }
    val currentAntX = antWaterX + (antPullProgress.value * 0.38f)
    val currentAntY = antWaterY + (antPullProgress.value * 0.16f)

    // Rolling Item Position (for ROLLING_BASKET)
    val rollingItemX = if (isCarryingItem || missionStage > 0) -0.15f else (-0.20f + sin(animTime.toDouble()).toFloat() * 0.24f)
    val rollingItemY = if (isCarryingItem || missionStage > 0) -0.18f else (-0.18f + cos(animTime.toDouble() * 0.7).toFloat() * 0.12f)
    val goalBasketX = 0.56f
    val goalBasketY = -0.38f

    // Airplane / Machine Position (for INTERACT_ACTIVATE)
    val machineX = -0.48f
    val machineY = -0.26f
    val airplaneFlyHeight = remember(character.letter, currentMissionIndex) { Animatable(0f) }

    // Animal Bridge Encounter Position (for BRIDGE_ENCOUNTER)
    val animalX = -0.56f
    val animalY = -0.28f

    // Jump Physics Arc
    val jumpHeight = remember { Animatable(0f) }
    fun performJump() {
        if (!isJumping) {
            coroutineScope.launch {
                isJumping = true
                actionState = CharacterActionState.JUMP
                audioEngine.playClickSound()
                jumpHeight.animateTo(
                    targetValue = 42f,
                    animationSpec = tween(220, easing = FastOutSlowInEasing)
                )
                actionState = CharacterActionState.FALL
                jumpHeight.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(200, easing = LinearOutSlowInEasing)
                )
                actionState = CharacterActionState.LAND
                delay(60)
                isJumping = false
                actionState = if (isCarryingItem || hasRope) CharacterActionState.CARRY else CharacterActionState.IDLE
            }
        }
    }

    // Smooth Continuous Movement Loop when D-Pad is pressed/held
    LaunchedEffect(activeDirection, isCarryingItem, hasRope, missionFinished) {
        val dir = activeDirection
        if (dir != null) {
            actionState = if (isCarryingItem || hasRope) CharacterActionState.CARRY else CharacterActionState.RUN
            while (activeDirection == dir) {
                val step = 0.022f
                when (dir) {
                    MoveDirection.UP -> {
                        charY = (charY - step).coerceIn(-0.82f, 0.82f)
                        facingAngle = 270f
                    }
                    MoveDirection.DOWN -> {
                        charY = (charY + step).coerceIn(-0.82f, 0.82f)
                        facingAngle = 90f
                    }
                    MoveDirection.LEFT -> {
                        charX = (charX - step).coerceIn(-0.92f, 0.92f)
                        facingAngle = 180f
                    }
                    MoveDirection.RIGHT -> {
                        charX = (charX + step).coerceIn(-0.92f, 0.92f)
                        facingAngle = 0f
                    }
                }
                delay(16L) // ~60 FPS smooth physics update
            }
            actionState = if (isCarryingItem || hasRope) CharacterActionState.CARRY else CharacterActionState.IDLE
        }
    }

    // Third-Person Camera Dynamic Tracking Follow
    val camOffsetX by animateFloatAsState(
        targetValue = -charX * 70f,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow),
        label = "camOffsetX"
    )
    val camOffsetY by animateFloatAsState(
        targetValue = -charY * 45f,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow),
        label = "camOffsetY"
    )

    // Perspective depth scale based on distance from horizon
    val depthScale = (0.88f + (charY + 0.82f) * 0.18f).coerceIn(0.80f, 1.20f)

    // Check Proximity Distances
    val distToRope = hypot(charX - ropeOriginX, charY - ropeOriginY)
    val distToWaterSpot = hypot(charX - waterRescueSpotX, charY - waterRescueSpotY)
    val distToRollingItem = hypot(charX - rollingItemX, charY - rollingItemY)
    val distToBasket = hypot(charX - goalBasketX, charY - goalBasketY)
    val distToMachine = hypot(charX - machineX, charY - machineY)
    val distToAnimal = hypot(charX - animalX, charY - animalY)

    // Play Mission Voice Intro
    LaunchedEffect(mission.id) {
        val storyVoice = if (mission.storyPrompt.isNotBlank()) mission.storyPrompt else mission.description
        audioEngine.speak(storyVoice)
    }

    val themeColor = Color(character.themeColorHex)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFBAE6FD), // Sky blue
                        Color(0xFFE0F2FE),
                        Color(0xFF86EFAC).copy(alpha = 0.85f), // Meadow green
                        Color(0xFF4ADE80)
                    )
                )
            )
            .border(2.dp, themeColor.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
            .pointerInput(Unit) {
                // Tap-to-move touch anywhere in the arena
                detectTapGestures { offset ->
                    val normX = ((offset.x - size.width / 2f) / (size.width * 0.45f)).coerceIn(-0.9f, 0.9f)
                    val normY = ((offset.y - size.height / 2f) / (size.height * 0.38f)).coerceIn(-0.8f, 0.8f)
                    coroutineScope.launch {
                        val dx = normX - charX
                        val dy = normY - charY
                        facingAngle = (atan2(dy.toDouble(), dx.toDouble()) * 180.0 / Math.PI).toFloat()
                        charX = normX
                        charY = normY
                        audioEngine.playClickSound()
                    }
                }
            }
    ) {
        val arenaWidthPx = constraints.maxWidth.toFloat()
        val arenaHeightPx = constraints.maxHeight.toFloat()

        // 3D Environment Background & Ground Canvas with Camera Follow
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Drifting Sky Clouds
            drawCircle(
                color = Color.White.copy(alpha = 0.75f),
                radius = 35.dp.toPx(),
                center = Offset(w * 0.25f + cloudOffset + camOffsetX * 0.2f, h * 0.10f + camOffsetY * 0.1f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = 45.dp.toPx(),
                center = Offset(w * 0.35f + cloudOffset + camOffsetX * 0.2f, h * 0.09f + camOffsetY * 0.1f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.75f),
                radius = 35.dp.toPx(),
                center = Offset(w * 0.45f + cloudOffset + camOffsetX * 0.2f, h * 0.10f + camOffsetY * 0.1f)
            )

            // Sun glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFEF08A), Color(0xFFFDE047).copy(alpha = 0.25f), Color.Transparent),
                    center = Offset(w * 0.85f + camOffsetX * 0.1f, h * 0.10f + camOffsetY * 0.05f),
                    radius = 50.dp.toPx()
                ),
                center = Offset(w * 0.85f + camOffsetX * 0.1f, h * 0.10f + camOffsetY * 0.05f),
                radius = 50.dp.toPx()
            )

            // 2. 3D Terrain Horizon Ground
            val horizonY = (h * 0.26f) + (camOffsetY * 0.3f)
            val groundPath = Path().apply {
                moveTo(0f, horizonY)
                lineTo(w, horizonY)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = groundPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF86EFAC),
                        Color(0xFF4ADE80),
                        Color(0xFF22C55E)
                    ),
                    startY = horizonY,
                    endY = h
                )
            )

            // 3. Flowing River Stream on Left (moves with camera)
            val riverStartX = (w * 0.22f) + camOffsetX * 0.5f
            val riverPath = Path().apply {
                moveTo(riverStartX, horizonY)
                cubicTo(riverStartX - 50f, h * 0.5f, riverStartX + 60f, h * 0.7f, riverStartX - 20f, h)
                lineTo(0f, h)
                lineTo(0f, horizonY)
                close()
            }
            drawPath(
                path = riverPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7)),
                    start = Offset(0f, horizonY),
                    end = Offset(w * 0.35f, h)
                )
            )

            // River Ripples
            drawCircle(
                color = Color.White.copy(alpha = (1f - waterRipple) * 0.6f),
                radius = (20f + waterRipple * 30f),
                center = Offset(riverStartX - 15f, h * 0.52f + camOffsetY * 0.4f),
                style = Stroke(width = 2.dp.toPx())
            )

            // 4. Wooden Cobblestone Bridge Crossing River
            val bridgeMidY = h * 0.48f + camOffsetY * 0.5f
            val bridgePath = Path().apply {
                moveTo(riverStartX - 70f, bridgeMidY - 18.dp.toPx())
                lineTo(riverStartX + 45f, bridgeMidY - 18.dp.toPx())
                lineTo(riverStartX + 45f, bridgeMidY + 22.dp.toPx())
                lineTo(riverStartX - 70f, bridgeMidY + 22.dp.toPx())
                close()
            }
            drawPath(
                path = bridgePath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFB45309), Color(0xFF78350F), Color(0xFF451A03)),
                    startY = bridgeMidY - 20.dp.toPx(),
                    endY = bridgeMidY + 25.dp.toPx()
                )
            )

            // 5. Cobblestone Path to Goal Basket / Runway
            val pathCobble = Path().apply {
                val pTopX = (w * 0.5f) + camOffsetX * 0.6f
                val pBotX = (w * 0.58f) + camOffsetX * 0.8f
                moveTo(pTopX - 25f, horizonY + 15f)
                cubicTo(pTopX + 20f, h * 0.5f, pBotX - 30f, h * 0.7f, pBotX - 40f, h)
                lineTo(pBotX + 40f, h)
                cubicTo(pBotX + 30f, h * 0.7f, pTopX + 50f, h * 0.5f, pTopX + 25f, horizonY + 15f)
                close()
            }
            drawPath(
                path = pathCobble,
                color = Color(0xFFFDE68A).copy(alpha = 0.55f)
            )

            // 6. Dynamic 3D Rescue Rope Rendering (When thrown or pulled)
            if (mission.gameplayType == MissionGameplayType.ROPE_RESCUE && missionStage in 2..3) {
                val playerScreenX = (arenaWidthPx / 2f) + (charX * arenaWidthPx * 0.45f) + camOffsetX
                val playerScreenY = (arenaHeightPx / 2f) + (charY * arenaHeightPx * 0.38f) + camOffsetY - jumpHeight.value
                val antScreenX = (arenaWidthPx / 2f) + (currentAntX * arenaWidthPx * 0.45f) + camOffsetX
                val antScreenY = (arenaHeightPx / 2f) + (currentAntY * arenaHeightPx * 0.38f) + camOffsetY

                val ropePath = Path().apply {
                    moveTo(playerScreenX, playerScreenY - 15f)
                    val midX = (playerScreenX + antScreenX) / 2f
                    val midY = ((playerScreenY + antScreenY) / 2f) + (if (missionStage == 2) 18f else 5f)
                    quadraticTo(midX, midY, antScreenX, antScreenY)
                }

                // Shadow rope
                drawPath(
                    path = ropePath,
                    color = Color.Black.copy(alpha = 0.25f),
                    style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                )
                // Golden Braided Rope
                drawPath(
                    path = ropePath,
                    color = Color(0xFFD97706),
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
                drawPath(
                    path = ropePath,
                    color = Color(0xFFFDE68A),
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f)))
                )
            }

            // Decorative Flower Tufts
            drawCircle(Color(0xFFF43F5E), 5.dp.toPx(), Offset(w * 0.42f + camOffsetX * 0.7f, h * 0.45f + camOffsetY * 0.7f))
            drawCircle(Color(0xFFEAB308), 4.dp.toPx(), Offset(w * 0.78f + camOffsetX * 0.7f, h * 0.65f + camOffsetY * 0.7f))
            drawCircle(Color(0xFFA855F7), 5.dp.toPx(), Offset(w * 0.85f + camOffsetX * 0.7f, h * 0.42f + camOffsetY * 0.7f))
            drawCircle(Color(0xFF38BDF8), 4.dp.toPx(), Offset(w * 0.35f + camOffsetX * 0.7f, h * 0.75f + camOffsetY * 0.7f))
        }

        // ==========================================
        // MISSION SPECIFIC INTERACTIVE 3D OBJECTS
        // ==========================================

        // 1. ROPE_RESCUE Objects (Ant on Lily Pad & Ground Rope)
        if (mission.gameplayType == MissionGameplayType.ROPE_RESCUE) {
            val antScreenX = (arenaWidthPx / 2f) + (currentAntX * arenaWidthPx * 0.45f) + camOffsetX
            val antScreenY = (arenaHeightPx / 2f) + (currentAntY * arenaHeightPx * 0.38f) + camOffsetY

            // Floating Lily Pad underneath Ant
            Surface(
                shape = CircleShape,
                color = Color(0xFF16A34A).copy(alpha = 0.9f),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF15803D)),
                modifier = Modifier
                    .offset { IntOffset(antScreenX.toInt() - 28.dp.roundToPx(), antScreenY.toInt() - 10.dp.roundToPx()) }
                    .size(56.dp, 28.dp)
            ) {}

            // Ant Character Figurine / Bubble
            Box(
                modifier = Modifier
                    .offset { IntOffset(antScreenX.toInt() - 32.dp.roundToPx(), antScreenY.toInt() - 48.dp.roundToPx()) }
                    .size(64.dp)
                    .scale(if (missionStage < 3) targetPulse else 1.15f),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        width = 2.5.dp,
                        color = if (missionStage >= 2) Color(0xFF10B981) else Color(0xFFEF4444)
                    ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(mission.targetEmoji, fontSize = 32.sp)
                    }
                }
            }

            // Ant Speech Callout
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (missionStage == 3) Color(0xFF10B981) else Color(0xFFEF4444),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .offset { IntOffset(antScreenX.toInt() - 45.dp.roundToPx(), antScreenY.toInt() - 76.dp.roundToPx()) }
            ) {
                Text(
                    text = when (missionStage) {
                        0 -> "HELP ME! 🐜"
                        1 -> "I'M HERE! 🐜"
                        2 -> "I GOT IT! 🤝"
                        else -> "SAFE! YAY! 🎉"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    maxLines = 1,
                    softWrap = false
                )
            }

            // Rope on the Ground (when not yet picked up)
            if (!hasRope && missionStage == 0) {
                val ropeScreenX = (arenaWidthPx / 2f) + (ropeOriginX * arenaWidthPx * 0.45f) + camOffsetX
                val ropeScreenY = (arenaHeightPx / 2f) + (ropeOriginY * arenaHeightPx * 0.38f) + camOffsetY

                Box(
                    modifier = Modifier
                        .offset { IntOffset(ropeScreenX.toInt() - 28.dp.roundToPx(), ropeScreenY.toInt() - 28.dp.roundToPx()) }
                        .size(56.dp)
                        .scale(targetPulse),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFEF3C7),
                        shadowElevation = 6.dp,
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFD97706)),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(mission.ropeItemEmoji, fontSize = 28.sp)
                        }
                    }
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFD97706),
                    modifier = Modifier
                        .offset { IntOffset(ropeScreenX.toInt() - 42.dp.roundToPx(), ropeScreenY.toInt() - 50.dp.roundToPx()) }
                ) {
                    Text(
                        text = "Rescue Rope 🪢",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Water Rescue Target Ring (when carrying rope)
            if (hasRope && missionStage == 1) {
                val spotScreenX = (arenaWidthPx / 2f) + (waterRescueSpotX * arenaWidthPx * 0.45f) + camOffsetX
                val spotScreenY = (arenaHeightPx / 2f) + (waterRescueSpotY * arenaHeightPx * 0.38f) + camOffsetY

                Surface(
                    shape = CircleShape,
                    color = Color(0xFF22C55E).copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(2.5.dp, Color(0xFF16A34A)),
                    modifier = Modifier
                        .offset { IntOffset(spotScreenX.toInt() - 32.dp.roundToPx(), spotScreenY.toInt() - 32.dp.roundToPx()) }
                        .size(64.dp)
                        .scale(targetPulse)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🎯", fontSize = 26.sp)
                    }
                }
            }
        }

        // 2. ROLLING_BASKET Objects (Rolling Apple & Picnic Basket)
        if (mission.gameplayType == MissionGameplayType.ROLLING_BASKET) {
            if (!isCarryingItem && !missionFinished) {
                val itemScreenX = (arenaWidthPx / 2f) + (rollingItemX * arenaWidthPx * 0.45f) + camOffsetX
                val itemScreenY = (arenaHeightPx / 2f) + (rollingItemY * arenaHeightPx * 0.38f) + camOffsetY

                Box(
                    modifier = Modifier
                        .offset { IntOffset(itemScreenX.toInt() - 34.dp.roundToPx(), itemScreenY.toInt() - 34.dp.roundToPx()) }
                        .size(68.dp)
                        .scale(targetPulse),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.95f),
                        shadowElevation = 8.dp,
                        border = androidx.compose.foundation.BorderStroke(2.5.dp, Color(0xFFF59E0B)),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(mission.targetEmoji, fontSize = 34.sp)
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEF4444),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .offset { IntOffset(itemScreenX.toInt() - 48.dp.roundToPx(), itemScreenY.toInt() - 60.dp.roundToPx()) }
                ) {
                    Text(
                        text = "Catch ${mission.targetWord}! 🍎",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        maxLines = 1
                    )
                }
            }

            // Picnic Basket Goal
            val goalScreenX = (arenaWidthPx / 2f) + (goalBasketX * arenaWidthPx * 0.45f) + camOffsetX
            val goalScreenY = (arenaHeightPx / 2f) + (goalBasketY * arenaHeightPx * 0.38f) + camOffsetY

            Box(
                modifier = Modifier
                    .offset { IntOffset(goalScreenX.toInt() - 38.dp.roundToPx(), goalScreenY.toInt() - 38.dp.roundToPx()) }
                    .size(76.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isCarryingItem) Color(0xFFFEF08A) else Color.White.copy(alpha = 0.88f),
                    shadowElevation = 6.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isCarryingItem) 3.dp else 1.5.dp,
                        color = if (isCarryingItem) Color(0xFFEAB308) else Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(mission.goalEmoji.ifBlank { "🧺" }, fontSize = 36.sp)
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isCarryingItem) Color(0xFF10B981) else Color(0xFF475569),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .offset { IntOffset(goalScreenX.toInt() - 50.dp.roundToPx(), goalScreenY.toInt() - 66.dp.roundToPx()) }
            ) {
                Text(
                    text = if (isCarryingItem) "Deliver Here! 🎯" else (mission.goalLocationName.ifBlank { "Picnic Basket" }),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    maxLines = 1
                )
            }
        }

        // 3. INTERACT_ACTIVATE (Airplane on Runway)
        if (mission.gameplayType == MissionGameplayType.INTERACT_ACTIVATE) {
            val planeScreenX = (arenaWidthPx / 2f) + (machineX * arenaWidthPx * 0.45f) + camOffsetX
            val planeScreenY = (arenaHeightPx / 2f) + (machineY * arenaHeightPx * 0.38f) + camOffsetY - airplaneFlyHeight.value

            Box(
                modifier = Modifier
                    .offset { IntOffset(planeScreenX.toInt() - 42.dp.roundToPx(), planeScreenY.toInt() - 42.dp.roundToPx()) }
                    .size(84.dp)
                    .scale(targetPulse),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFEFF6FF),
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(2.5.dp, Color(0xFF3B82F6)),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(mission.targetEmoji, fontSize = 42.sp)
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF2563EB),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .offset { IntOffset(planeScreenX.toInt() - 46.dp.roundToPx(), planeScreenY.toInt() - 66.dp.roundToPx()) }
            ) {
                Text(
                    text = if (missionStage > 0) "ZOOM! Flying High! ✈️" else "Sky Runway 🏁",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // 4. BRIDGE_ENCOUNTER (Alligator / Friendly Animal across Bridge)
        if (mission.gameplayType == MissionGameplayType.BRIDGE_ENCOUNTER) {
            val animalScreenX = (arenaWidthPx / 2f) + (animalX * arenaWidthPx * 0.45f) + camOffsetX
            val animalScreenY = (arenaHeightPx / 2f) + (animalY * arenaHeightPx * 0.38f) + camOffsetY

            Box(
                modifier = Modifier
                    .offset { IntOffset(animalScreenX.toInt() - 38.dp.roundToPx(), animalScreenY.toInt() - 38.dp.roundToPx()) }
                    .size(76.dp)
                    .scale(targetPulse),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFECFDF5),
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(2.5.dp, Color(0xFF059669)),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(mission.targetEmoji, fontSize = 38.sp)
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF059669),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .offset { IntOffset(animalScreenX.toInt() - 50.dp.roundToPx(), animalScreenY.toInt() - 66.dp.roundToPx()) }
            ) {
                Text(
                    text = if (missionStage > 0) "Best Friends! 💚" else "Friendly ${mission.targetWord}! 🐊",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // 3D Active Hero Character Figurine (with camera tracking and perspective depth scaling)
        val charScreenX = (arenaWidthPx / 2f) + (charX * arenaWidthPx * 0.45f) + camOffsetX
        val charScreenY = (arenaHeightPx / 2f) + (charY * arenaHeightPx * 0.38f) + camOffsetY - jumpHeight.value

        Box(
            modifier = Modifier
                .offset { IntOffset(charScreenX.toInt() - 60.dp.roundToPx(), charScreenY.toInt() - 90.dp.roundToPx()) }
                .size(120.dp)
                .scale(depthScale),
            contentAlignment = Alignment.Center
        ) {
            Character3DFigurine(
                character = character,
                actionState = actionState,
                size = 115.dp,
                carriedItemEmoji = when {
                    isCarryingItem -> mission.targetEmoji
                    hasRope -> mission.ropeItemEmoji
                    else -> null
                },
                facingAngle = facingAngle,
                onClick = { performJump() }
            )
        }

        // Mission Narrative & Objective Top Overlay
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White.copy(alpha = 0.95f),
            shadowElevation = 6.dp,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, themeColor.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(character.characterEmoji, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Chapter ${character.letter} • Mission ${currentMissionIndex + 1}/${missions.size}: ${mission.title}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = themeColor,
                        maxLines = 1,
                        softWrap = false
                    )
                    Text(
                        text = when (mission.gameplayType) {
                            MissionGameplayType.ROPE_RESCUE -> when (missionStage) {
                                0 -> mission.step1Prompt.ifBlank { "Walk to the rope and pick it up! 🪢" }
                                1 -> mission.step2Prompt.ifBlank { "Go to water edge and throw rope to Ant! 🌊" }
                                2 -> mission.step3Prompt.ifBlank { "Pull the rope to bring Ant to safety! 💪" }
                                else -> "Ant is rescued and happy! Great job!"
                            }
                            MissionGameplayType.ROLLING_BASKET -> when (missionStage) {
                                0 -> mission.step1Prompt.ifBlank { "Catch the rolling ${mission.targetWord}! 🏃" }
                                1 -> mission.step2Prompt.ifBlank { "Carry to the Picnic Basket! 🧺" }
                                else -> "${mission.targetWord} safely delivered! Fantastic!"
                            }
                            MissionGameplayType.INTERACT_ACTIVATE -> when (missionStage) {
                                0 -> mission.step1Prompt.ifBlank { "Walk over to the Airplane runway! ✈️" }
                                else -> "Airplane launched high in the sky! Zoom!"
                            }
                            MissionGameplayType.BRIDGE_ENCOUNTER -> when (missionStage) {
                                0 -> mission.step1Prompt.ifBlank { "Walk across the bridge to meet Alligator! 🌉" }
                                else -> "Alligator is so happy to meet you! 🐊"
                            }
                        },
                        fontSize = 12.sp,
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        softWrap = false
                    )
                }
                IconButton(
                    onClick = {
                        audioEngine.playClickSound()
                        val promptToSpeak = when (mission.gameplayType) {
                            MissionGameplayType.ROPE_RESCUE -> when (missionStage) {
                                0 -> "Walk to the rescue rope and pick it up!"
                                1 -> "Go to the water edge and throw the rope to Ant!"
                                2 -> "Pull Ant safely to the grass!"
                                else -> "Ant is safe! A is for Ant!"
                            }
                            MissionGameplayType.ROLLING_BASKET -> when (missionStage) {
                                0 -> "Catch the rolling ${mission.targetWord}!"
                                1 -> "Carry it into the Picnic Basket!"
                                else -> "Great job! A is for ${mission.targetWord}!"
                            }
                            MissionGameplayType.INTERACT_ACTIVATE -> "Guide Letter A to the runway and launch the Airplane!"
                            MissionGameplayType.BRIDGE_ENCOUNTER -> "Cross the bridge and greet the friendly Alligator!"
                        }
                        audioEngine.speak(promptToSpeak)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Listen",
                        tint = themeColor
                    )
                }
            }
        }

        // On-Screen Child-Friendly Mobile Controls: Large D-Pad & Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            // Large Child-Friendly Directional Pad (▲, ◀, ●, ▶, ▼)
            ChildFriendlyDPad(
                themeColor = themeColor,
                activeDirection = activeDirection,
                onDirectionChange = { dir ->
                    activeDirection = dir
                },
                onStepMove = { dir ->
                    val step = 0.08f
                    when (dir) {
                        MoveDirection.UP -> {
                            charY = (charY - step).coerceIn(-0.82f, 0.82f)
                            facingAngle = 270f
                        }
                        MoveDirection.DOWN -> {
                            charY = (charY + step).coerceIn(-0.82f, 0.82f)
                            facingAngle = 90f
                        }
                        MoveDirection.LEFT -> {
                            charX = (charX - step).coerceIn(-0.92f, 0.92f)
                            facingAngle = 180f
                        }
                        MoveDirection.RIGHT -> {
                            charX = (charX + step).coerceIn(-0.92f, 0.92f)
                            facingAngle = 0f
                        }
                    }
                    audioEngine.playClickSound()
                }
            )

            // Right Action Controls: INTERACT / JUMP Buttons
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Dynamic Contextual Action Button based on mission and stage
                when (mission.gameplayType) {
                    MissionGameplayType.ROPE_RESCUE -> {
                        when (missionStage) {
                            0 -> {
                                if (distToRope < 0.35f) {
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                actionState = CharacterActionState.PICK_UP
                                                audioEngine.playClickSound()
                                                audioEngine.speak("Picked up the rescue rope! Now walk to the water!")
                                                delay(400)
                                                hasRope = true
                                                missionStage = 1
                                                actionState = CharacterActionState.CARRY
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                        shape = RoundedCornerShape(20.dp),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                                        modifier = Modifier.height(56.dp).testTag("action_pickup_rope_btn")
                                    ) {
                                        Text("🖐️ PICK UP ROPE 🪢", fontSize = 13.sp, fontWeight = FontWeight.Black, maxLines = 1)
                                    }
                                }
                            }
                            1 -> {
                                if (distToWaterSpot < 0.38f) {
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                actionState = CharacterActionState.THROW
                                                audioEngine.playClickSound()
                                                audioEngine.speak("Throwing rope to Ant!")
                                                delay(450)
                                                missionStage = 2
                                                actionState = CharacterActionState.IDLE
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                        shape = RoundedCornerShape(20.dp),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                                        modifier = Modifier.height(56.dp).testTag("action_throw_rope_btn")
                                    ) {
                                        Text("🎯 THROW ROPE TO ANT 🐜", fontSize = 13.sp, fontWeight = FontWeight.Black, maxLines = 1)
                                    }
                                }
                            }
                            2 -> {
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            actionState = CharacterActionState.PULL
                                            audioEngine.playClickSound()
                                            audioEngine.speak("Pulling Ant to safety! Hold tight!")
                                            antPullProgress.animateTo(
                                                targetValue = 1f,
                                                animationSpec = tween(900, easing = FastOutSlowInEasing)
                                            )
                                            missionStage = 3
                                            missionFinished = true
                                            actionState = CharacterActionState.CELEBRATE
                                            audioEngine.speak("ANT! A is for Ant! Ant is safe!")
                                            onMissionComplete(mission)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                    shape = RoundedCornerShape(20.dp),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                                    modifier = Modifier.height(56.dp).testTag("action_pull_rope_btn")
                                ) {
                                    Text("💪 PULL ANT TO SAFETY!", fontSize = 13.sp, fontWeight = FontWeight.Black, maxLines = 1)
                                }
                            }
                            3 -> {
                                Button(
                                    onClick = onNextMission,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                    shape = RoundedCornerShape(20.dp),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                                    modifier = Modifier.height(56.dp).testTag("next_mission_btn")
                                ) {
                                    Text("Next Adventure ➡️", fontSize = 14.sp, fontWeight = FontWeight.Black, maxLines = 1)
                                }
                            }
                        }
                    }

                    MissionGameplayType.ROLLING_BASKET -> {
                        if (!isCarryingItem && missionStage == 0 && distToRollingItem < 0.35f) {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        actionState = CharacterActionState.PICK_UP
                                        audioEngine.playClickSound()
                                        audioEngine.speak("Caught ${mission.targetWord}! Put it in the basket!")
                                        delay(400)
                                        isCarryingItem = true
                                        missionStage = 1
                                        actionState = CharacterActionState.CARRY
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                                shape = RoundedCornerShape(20.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                                modifier = Modifier.height(56.dp).testTag("action_pickup_btn")
                            ) {
                                Text("🖐️ CATCH ${mission.targetWord}! 🍎", fontSize = 13.sp, fontWeight = FontWeight.Black, maxLines = 1)
                            }
                        } else if (isCarryingItem && distToBasket < 0.38f) {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        actionState = CharacterActionState.PLACE
                                        audioEngine.playClickSound()
                                        delay(350)
                                        isCarryingItem = false
                                        missionStage = 2
                                        missionFinished = true
                                        actionState = CharacterActionState.CELEBRATE
                                        audioEngine.speak("APPLE! Placed in basket! A is for Apple!")
                                        onMissionComplete(mission)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                shape = RoundedCornerShape(20.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                                modifier = Modifier.height(56.dp).testTag("action_place_btn")
                            ) {
                                Text("🎯 PLACE IN BASKET!", fontSize = 13.sp, fontWeight = FontWeight.Black, maxLines = 1)
                            }
                        } else if (missionFinished) {
                            Button(
                                onClick = onNextMission,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                shape = RoundedCornerShape(20.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                                modifier = Modifier.height(56.dp).testTag("next_mission_btn")
                            ) {
                                Text("Next Adventure ➡️", fontSize = 14.sp, fontWeight = FontWeight.Black, maxLines = 1)
                            }
                        }
                    }

                    MissionGameplayType.INTERACT_ACTIVATE -> {
                        if (missionStage == 0 && distToMachine < 0.38f) {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        actionState = CharacterActionState.REACT
                                        audioEngine.playClickSound()
                                        audioEngine.speak("AIRPLANE! Propeller spinning! 3, 2, 1, Lift off!")
                                        airplaneFlyHeight.animateTo(
                                            targetValue = 60f,
                                            animationSpec = tween(1200, easing = FastOutSlowInEasing)
                                        )
                                        missionStage = 1
                                        missionFinished = true
                                        actionState = CharacterActionState.CELEBRATE
                                        onMissionComplete(mission)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                shape = RoundedCornerShape(20.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                                modifier = Modifier.height(56.dp).testTag("action_activate_btn")
                            ) {
                                Text("🚀 LAUNCH AIRPLANE! ✈️", fontSize = 13.sp, fontWeight = FontWeight.Black, maxLines = 1)
                            }
                        } else if (missionFinished) {
                            Button(
                                onClick = onNextMission,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                shape = RoundedCornerShape(20.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                                modifier = Modifier.height(56.dp).testTag("next_mission_btn")
                            ) {
                                Text("Next Adventure ➡️", fontSize = 14.sp, fontWeight = FontWeight.Black, maxLines = 1)
                            }
                        }
                    }

                    MissionGameplayType.BRIDGE_ENCOUNTER -> {
                        if (missionStage == 0 && distToAnimal < 0.38f) {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        actionState = CharacterActionState.WAVE
                                        audioEngine.playClickSound()
                                        audioEngine.speak("ALLIGATOR! Hello friendly Alligator! A is for Alligator!")
                                        delay(600)
                                        missionStage = 1
                                        missionFinished = true
                                        actionState = CharacterActionState.CELEBRATE
                                        onMissionComplete(mission)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                shape = RoundedCornerShape(20.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                                modifier = Modifier.height(56.dp).testTag("action_greet_btn")
                            ) {
                                Text("👋 GREET ALLIGATOR! 🐊", fontSize = 13.sp, fontWeight = FontWeight.Black, maxLines = 1)
                            }
                        } else if (missionFinished) {
                            Button(
                                onClick = onNextMission,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                shape = RoundedCornerShape(20.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                                modifier = Modifier.height(56.dp).testTag("next_mission_btn")
                            ) {
                                Text("Next Adventure ➡️", fontSize = 14.sp, fontWeight = FontWeight.Black, maxLines = 1)
                            }
                        }
                    }
                }

                // Large Dedicated JUMP Button
                Surface(
                    shape = CircleShape,
                    color = themeColor,
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(3.dp, Color.White),
                    modifier = Modifier
                        .size(62.dp)
                        .clickable { performJump() }
                        .testTag("btn_jump_action")
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🦘", fontSize = 24.sp)
                        Text(
                            text = "JUMP",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// CHILD-FRIENDLY DIRECTIONAL D-PAD COMPONENT
// ----------------------------------------------------
@Composable
private fun ChildFriendlyDPad(
    themeColor: Color,
    activeDirection: MoveDirection?,
    onDirectionChange: (MoveDirection?) -> Unit,
    onStepMove: (MoveDirection) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color.White.copy(alpha = 0.92f),
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(2.5.dp, Color(0xFFCBD5E1)),
        modifier = modifier.padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            // Forward / UP Button (▲)
            DPadButton(
                direction = MoveDirection.UP,
                symbol = "▲",
                label = "UP",
                themeColor = themeColor,
                isActive = activeDirection == MoveDirection.UP,
                onPressChange = { isPressed ->
                    onDirectionChange(if (isPressed) MoveDirection.UP else null)
                },
                onClick = { onStepMove(MoveDirection.UP) },
                testTag = "dpad_up"
            )

            // Middle Row: LEFT (◀), CENTER (●), RIGHT (▶)
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Button (◀)
                DPadButton(
                    direction = MoveDirection.LEFT,
                    symbol = "◀",
                    label = "LEFT",
                    themeColor = themeColor,
                    isActive = activeDirection == MoveDirection.LEFT,
                    onPressChange = { isPressed ->
                        onDirectionChange(if (isPressed) MoveDirection.LEFT else null)
                    },
                    onClick = { onStepMove(MoveDirection.LEFT) },
                    testTag = "dpad_left"
                )

                // Center Neutral Pivot (●)
                Surface(
                    shape = CircleShape,
                    color = themeColor.copy(alpha = 0.20f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, themeColor.copy(alpha = 0.5f)),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "●",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColor
                        )
                    }
                }

                // Right Button (▶)
                DPadButton(
                    direction = MoveDirection.RIGHT,
                    symbol = "▶",
                    label = "RIGHT",
                    themeColor = themeColor,
                    isActive = activeDirection == MoveDirection.RIGHT,
                    onPressChange = { isPressed ->
                        onDirectionChange(if (isPressed) MoveDirection.RIGHT else null)
                    },
                    onClick = { onStepMove(MoveDirection.RIGHT) },
                    testTag = "dpad_right"
                )
            }

            // Backward / DOWN Button (▼)
            DPadButton(
                direction = MoveDirection.DOWN,
                symbol = "▼",
                label = "DOWN",
                themeColor = themeColor,
                isActive = activeDirection == MoveDirection.DOWN,
                onPressChange = { isPressed ->
                    onDirectionChange(if (isPressed) MoveDirection.DOWN else null)
                },
                onClick = { onStepMove(MoveDirection.DOWN) },
                testTag = "dpad_down"
            )
        }
    }
}

@Composable
private fun DPadButton(
    direction: MoveDirection,
    symbol: String,
    label: String,
    themeColor: Color,
    isActive: Boolean,
    onPressChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    testTag: String
) {
    val buttonBgColor = if (isActive) themeColor else Color(0xFFF1F5F9)
    val contentColor = if (isActive) Color.White else Color(0xFF1E293B)
    val scale by animateFloatAsState(targetValue = if (isActive) 0.92f else 1.0f, label = "dpadScale")

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = buttonBgColor,
        shadowElevation = if (isActive) 2.dp else 4.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isActive) 2.5.dp else 1.5.dp,
            color = if (isActive) themeColor else Color(0xFFCBD5E1)
        ),
        modifier = Modifier
            .size(46.dp)
            .scale(scale)
            .pointerInput(direction) {
                detectTapGestures(
                    onPress = {
                        onPressChange(true)
                        val released = tryAwaitRelease()
                        onPressChange(false)
                        if (released) {
                            onClick()
                        }
                    },
                    onTap = {
                        onClick()
                    }
                )
            }
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = symbol,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = contentColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

enum class CharacterTab {
    ADVENTURE_3D,
    EXPLORE,
    VOCABULARY,
    SPEAKING
}

@Composable
fun LetterCharacterAdventureScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    initialLetter: Char = 'A',
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val allCharacters = remember { LetterCharacterData.characters }
    var selectedLetter by remember { mutableStateOf(initialLetter) }
    val currentCharacter: LetterCharacter = remember(selectedLetter) {
        LetterCharacterData.getCharacterByLetter(selectedLetter)
    }

    var activeTab by remember { mutableStateOf(CharacterTab.ADVENTURE_3D) }
    var actionState by remember { mutableStateOf(CharacterActionState.IDLE) }
    var speechBubbleText by remember { mutableStateOf(currentCharacter.greetingSpeech) }
    var showSpeechBubble by remember { mutableStateOf(true) }

    // Mission Game States
    var currentMissionIndex by remember { mutableIntStateOf(0) }
    var missionCompleted by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    // Speaking state
    var isSpeakingEvaluated by remember { mutableStateOf(false) }
    var showSpeakingDialog by remember { mutableStateOf(false) }
    var speakingWordTarget by remember { mutableStateOf("") }

    // Initial greeting on letter switch
    LaunchedEffect(selectedLetter) {
        actionState = CharacterActionState.WAVE
        speechBubbleText = currentCharacter.greetingSpeech
        showSpeechBubble = true
        currentMissionIndex = 0
        missionCompleted = false
        isSpeakingEvaluated = false
        audioEngine.speak(currentCharacter.greetingSpeech)
        delay(2500)
        actionState = CharacterActionState.IDLE
    }

    val primaryColor = Color(currentCharacter.themeColorHex)
    val secondaryColor = Color(currentCharacter.secondaryColorHex)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.16f),
                        Color(0xFFF8FAFC),
                        secondaryColor.copy(alpha = 0.10f)
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
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        audioEngine.playClickSound()
                        onBackClick()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.5.dp, Color(0xFFCBD5E1), CircleShape)
                        .testTag("char_adv_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
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
                        Text(currentCharacter.characterEmoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${currentCharacter.name} Adventure",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryColor,
                            maxLines = 1,
                            softWrap = false,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Stars & Coins Counter
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFEF3C7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⭐", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${repository.getStars()}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFEF9C3),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAB308))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🪙", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${repository.getCoins()}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF854D0E),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }

            // A-Z Character Horizontal Ribbon Selector
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allCharacters) { charItem ->
                    val isSelected = charItem.letter == selectedLetter
                    val itemColor = Color(charItem.themeColorHex)

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) itemColor else Color.White,
                        shadowElevation = if (isSelected) 4.dp else 1.dp,
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) itemColor else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier
                            .clickable {
                                if (selectedLetter != charItem.letter) {
                                    audioEngine.playClickSound()
                                    selectedLetter = charItem.letter
                                }
                            }
                            .testTag("char_ribbon_${charItem.letter}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${charItem.letter}${charItem.letter.lowercaseChar()}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) Color.White else Color(0xFF1E293B),
                                maxLines = 1,
                                softWrap = false,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(charItem.characterEmoji, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Mode Navigation Tabs
            TabRow(
                selectedTabIndex = activeTab.ordinal,
                containerColor = Color.Transparent,
                contentColor = primaryColor,
                divider = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                CharacterTab.entries.forEach { tab ->
                    val isSelected = activeTab == tab
                    val tabTitle = when (tab) {
                        CharacterTab.ADVENTURE_3D -> "🎮 3D World"
                        CharacterTab.EXPLORE -> "🌟 Meet"
                        CharacterTab.VOCABULARY -> "📖 Words"
                        CharacterTab.SPEAKING -> "🗣️ Speak"
                    }

                    Tab(
                        selected = isSelected,
                        onClick = {
                            audioEngine.playClickSound()
                            activeTab = tab
                        },
                        text = {
                            Text(
                                text = tabTitle,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) primaryColor else Color(0xFF64748B),
                                maxLines = 1,
                                softWrap = false,
                                textAlign = TextAlign.Center
                            )
                        }
                    )
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeTab) {
                    CharacterTab.ADVENTURE_3D -> {
                        Real3DAdventureArena(
                            character = currentCharacter,
                            currentMissionIndex = currentMissionIndex,
                            onMissionComplete = { mission ->
                                audioEngine.playVictorySound()
                                repository.addStars(1)
                                repository.addCoins(10)
                                showConfetti = true
                                speakingWordTarget = mission.targetWord
                                showSpeakingDialog = true
                                audioEngine.speak("A is for ${mission.targetWord}! Let's say ${mission.targetWord}!")
                            },
                            onNextMission = {
                                val missions = currentCharacter.missions
                                if (currentMissionIndex + 1 < missions.size) {
                                    currentMissionIndex++
                                } else {
                                    missionCompleted = true
                                    showConfetti = true
                                    repository.addStars(3)
                                    repository.addCoins(20)
                                    audioEngine.speak("Incredible! You completed all adventures in ${currentCharacter.name} Land! You earned the ${currentCharacter.unlockBadgeName} badge!")
                                    // Unlock next letter
                                    val nextIndex = (allCharacters.indexOfFirst { it.letter == selectedLetter } + 1) % allCharacters.size
                                    selectedLetter = allCharacters[nextIndex].letter
                                }
                            },
                            audioEngine = audioEngine
                        )
                    }
                    CharacterTab.EXPLORE -> {
                        CharacterExploreTab(
                            character = currentCharacter,
                            actionState = actionState,
                            showSpeechBubble = showSpeechBubble,
                            speechBubbleText = speechBubbleText,
                            onTriggerAction = { action, text ->
                                coroutineScope.launch {
                                    actionState = action
                                    speechBubbleText = text
                                    showSpeechBubble = true
                                    audioEngine.speak(text)
                                    delay(2200)
                                    actionState = CharacterActionState.IDLE
                                }
                            },
                            audioEngine = audioEngine
                        )
                    }
                    CharacterTab.VOCABULARY -> {
                        CharacterVocabularyTab(
                            character = currentCharacter,
                            audioEngine = audioEngine
                        )
                    }
                    CharacterTab.SPEAKING -> {
                        CharacterSpeakingTab(
                            character = currentCharacter,
                            isEvaluated = isSpeakingEvaluated,
                            onSpeakEvaluated = {
                                isSpeakingEvaluated = true
                                repository.addStars(1)
                                repository.addCoins(5)
                                audioEngine.playCorrectSound()
                                audioEngine.speak("Fantastic pronunciation! You said ${currentCharacter.name} so clearly!")
                            },
                            audioEngine = audioEngine
                        )
                    }
                }
            }
        }

        // Speaking & Pronunciation Post-Mission Modal
        if (showSpeakingDialog && speakingWordTarget.isNotBlank()) {
            SpeakingPracticeModal(
                word = speakingWordTarget,
                character = currentCharacter,
                onDismiss = { showSpeakingDialog = false },
                onPronouncedCorrect = {
                    repository.addStars(1)
                    repository.addCoins(5)
                    audioEngine.playCorrectSound()
                    audioEngine.speak("Great job! ${speakingWordTarget}!")
                    showSpeakingDialog = false
                },
                audioEngine = audioEngine
            )
        }

        // Confetti Celebration Overlay
        ConfettiOverlay(
            isVisible = showConfetti,
            modifier = Modifier.fillMaxSize()
        )
    }
}

// ----------------------------------------------------
// 2. SPEAKING & PRONUNCIATION POST-MISSION MODAL
// ----------------------------------------------------
@Composable
private fun SpeakingPracticeModal(
    word: String,
    character: LetterCharacter,
    onDismiss: () -> Unit,
    onPronouncedCorrect: () -> Unit,
    audioEngine: SpeechAndSoundEngine
) {
    var isListening by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onPronouncedCorrect,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Continue 🌟", fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color(0xFF64748B), maxLines = 1, softWrap = false)
            }
        },
        icon = {
            Text("🗣️", fontSize = 36.sp)
        },
        title = {
            Text(
                text = "Say \"${word}\"",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(character.themeColorHex),
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Letter ${character.letter} says ${character.phonicsSound}\n${character.letter} is for ${word}!",
                    fontSize = 14.sp,
                    color = Color(0xFF334155),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Listen Audio Button
                OutlinedButton(
                    onClick = {
                        audioEngine.playClickSound()
                        audioEngine.speak(word)
                    },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Listen \"${word}\"", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mic Recording Simulation / Evaluation Button
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isListening = true
                            resultText = "Listening..."
                            audioEngine.playClickSound()
                            delay(1600)
                            isListening = false
                            resultText = "Great pronunciation! ⭐"
                            onPronouncedCorrect()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isListening) Color(0xFFE11D48) else Color(character.themeColorHex)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic, contentDescription = "Mic")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isListening) "Listening..." else "Tap to Speak \"${word}\"",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                if (resultText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = resultText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF16A34A)
                    )
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp)
    )
}

// ----------------------------------------------------
// 3. EXPLORE & INTERACT WITH 3D CHARACTER FIGURINE
// ----------------------------------------------------
@Composable
private fun CharacterExploreTab(
    character: LetterCharacter,
    actionState: CharacterActionState,
    showSpeechBubble: Boolean,
    speechBubbleText: String,
    onTriggerAction: (CharacterActionState, String) -> Unit,
    audioEngine: SpeechAndSoundEngine
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 3D Figurine Center Stage
        Character3DFigurine(
            character = character,
            actionState = actionState,
            size = 180.dp,
            showSpeechBubble = showSpeechBubble,
            speechText = speechBubbleText,
            onClick = {
                onTriggerAction(
                    CharacterActionState.REACT,
                    "Hi friend! I'm ${character.name}! Letter ${character.letter} says ${character.phonicsSound}!"
                )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Phonics Badge & Pronunciation Hero Card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 3.dp,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(character.themeColorHex).copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${character.letter} ${character.letter.lowercaseChar()}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(character.themeColorHex),
                        maxLines = 1,
                        softWrap = false
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(character.themeColorHex).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Sound: ${character.phonicsSound}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(character.themeColorHex),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "\"${character.personality}\"",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF475569),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Phonics Sound Listen Button
                Button(
                    onClick = {
                        audioEngine.playClickSound()
                        audioEngine.speak("Letter ${character.letter} says ${character.phonicsSound}. ${character.phonicsExample}")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(character.themeColorHex)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Listen Phonics & Letter",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive Character Action Triggers (Wave, Jump, Walk, Celebrate)
        Text(
            text = "Play with ${character.name}!",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            maxLines = 1,
            softWrap = false,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionPillButton(
                label = "Wave 👋",
                modifier = Modifier.weight(1f),
                onClick = {
                    onTriggerAction(CharacterActionState.WAVE, "Hello! Let's be best friends!")
                }
            )
            ActionPillButton(
                label = "Jump 🦘",
                modifier = Modifier.weight(1f),
                onClick = {
                    onTriggerAction(CharacterActionState.JUMP, "Boing! Look how high I jump!")
                }
            )
            ActionPillButton(
                label = "Walk 🚶",
                modifier = Modifier.weight(1f),
                onClick = {
                    onTriggerAction(CharacterActionState.WALK, "Step, step, let's explore together!")
                }
            )
            ActionPillButton(
                label = "Cheer 🏆",
                modifier = Modifier.weight(1f),
                onClick = {
                    onTriggerAction(CharacterActionState.CELEBRATE, "Yay! You are amazing and super smart!")
                }
            )
        }
    }
}

@Composable
private fun ActionPillButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ----------------------------------------------------
// 4. VOCABULARY TAB (4 to 5 Words per letter)
// ----------------------------------------------------
@Composable
private fun CharacterVocabularyTab(
    character: LetterCharacter,
    audioEngine: SpeechAndSoundEngine
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${character.name}'s Vocabulary Words",
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(character.themeColorHex),
            maxLines = 1,
            softWrap = false,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Tap any card to hear pronunciation and example phrase!",
            fontSize = 13.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp),
            maxLines = 1,
            softWrap = false
        )

        character.vocabulary.forEach { vocab ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(character.themeColorHex).copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable {
                        audioEngine.playClickSound()
                        audioEngine.speak("${vocab.word}. ${vocab.sentence}")
                    }
                    .testTag("vocab_card_${vocab.word}")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Big Emoji Bubble
                    Surface(
                        shape = CircleShape,
                        color = Color(character.themeColorHex).copy(alpha = 0.12f),
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(vocab.emoji, fontSize = 28.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = vocab.word,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                maxLines = 1,
                                softWrap = false
                            )
                            if (vocab.phoneticSpelling.isNotBlank()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${vocab.phoneticSpelling})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(character.themeColorHex),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = vocab.sentence,
                            fontSize = 13.sp,
                            color = Color(0xFF475569),
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    IconButton(
                        onClick = {
                            audioEngine.playClickSound()
                            audioEngine.speak("${vocab.word}. ${vocab.sentence}")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Speak",
                            tint = Color(character.themeColorHex)
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 5. SPEAKING PRACTICE LAB
// ----------------------------------------------------
@Composable
private fun CharacterSpeakingTab(
    character: LetterCharacter,
    isEvaluated: Boolean,
    onSpeakEvaluated: () -> Unit,
    audioEngine: SpeechAndSoundEngine
) {
    val coroutineScope = rememberCoroutineScope()
    var isRecording by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Hero Card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 3.dp,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(character.themeColorHex).copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🗣️ Speak Like ${character.name}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(character.themeColorHex),
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Say: \"Letter ${character.letter} says ${character.phonicsSound} as in ${character.name}!\"",
                    fontSize = 15.sp,
                    color = Color(0xFF334155),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Center Mic Pulsing Target
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${character.characterEmoji} ${character.name}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color(character.themeColorHex),
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                shape = CircleShape,
                color = if (isRecording) Color(0xFFE11D48) else Color(character.themeColorHex),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .size(96.dp)
                    .clickable {
                        coroutineScope.launch {
                            isRecording = true
                            audioEngine.playClickSound()
                            delay(1800)
                            isRecording = false
                            onSpeakEvaluated()
                        }
                    }
                    .testTag("char_speak_mic_btn")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.GraphicEq else Icons.Default.Mic,
                        contentDescription = "Speak Mic",
                        tint = Color.White,
                        modifier = Modifier.size(46.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isRecording) "Listening to your voice..." else "Tap the microphone and speak!",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isRecording) Color(0xFFE11D48) else Color(0xFF64748B),
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center
            )
        }

        // Listen Model Pronunciation
        Button(
            onClick = {
                audioEngine.playClickSound()
                audioEngine.speak("${character.letter}. ${character.phonicsSound}. ${character.name}.")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(character.themeColorHex)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Listen",
                tint = Color(character.themeColorHex)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Listen Pronunciation",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(character.themeColorHex),
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center
            )
        }
    }
}
