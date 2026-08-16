package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CharacterActionState
import com.example.data.LetterCharacter
import kotlin.math.sin

/**
 * 3D Cartoon Figurine Renderer
 * Renders high-fidelity, polished, cute 3D character figurines matching the reference design:
 * - Glossy / clay lighting, ambient occlusion, bevel highlights, cute eyes, and distinctive letter-animal morphology
 * - Animations: Idle float/breathe, Walk steps, Bouncy jumps, Friendly wave, 360 twirl celebration, Speaking pulse
 */
@Composable
fun Character3DFigurine(
    character: LetterCharacter,
    modifier: Modifier = Modifier,
    actionState: CharacterActionState = CharacterActionState.IDLE,
    size: Dp = 160.dp,
    showSpeechBubble: Boolean = false,
    speechText: String = "",
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "char3DAnimation")

    // Idle Breathing & Float
    val idleFloat by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idleFloat"
    )

    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breatheScale"
    )

    // Walking cadence
    val walkSway by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "walkSway"
    )

    // Jumping
    val jumpBounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -35f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "jumpBounce"
    )

    // Waving
    val waveRotation by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waveRotation"
    )

    // Celebrate twirl
    val celebrateScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "celebrateScale"
    )

    // Speaking pulse
    val speakPulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(220, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "speakPulse"
    )

    val currentOffsetY = when (actionState) {
        CharacterActionState.IDLE -> idleFloat
        CharacterActionState.WALK -> sin(walkSway * 0.1f) * 6f
        CharacterActionState.JUMP -> jumpBounce
        CharacterActionState.WAVE -> idleFloat * 0.5f
        CharacterActionState.CELEBRATE -> jumpBounce * 0.7f
        CharacterActionState.REACT -> -10f
        CharacterActionState.SPEAK -> idleFloat * 0.4f
    }

    val currentScale = when (actionState) {
        CharacterActionState.IDLE -> breatheScale
        CharacterActionState.WALK -> 1.0f
        CharacterActionState.JUMP -> if (jumpBounce < -15f) 1.05f else 0.95f
        CharacterActionState.WAVE -> 1.02f
        CharacterActionState.CELEBRATE -> celebrateScale
        CharacterActionState.REACT -> 1.12f
        CharacterActionState.SPEAK -> speakPulse
    }

    val currentRotation = when (actionState) {
        CharacterActionState.IDLE -> 0f
        CharacterActionState.WALK -> walkSway
        CharacterActionState.JUMP -> walkSway * 0.3f
        CharacterActionState.WAVE -> waveRotation
        CharacterActionState.CELEBRATE -> waveRotation * 1.5f
        CharacterActionState.REACT -> 0f
        CharacterActionState.SPEAK -> waveRotation * 0.3f
    }

    Column(
        modifier = modifier.width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Speech Bubble if active
        if (showSpeechBubble && speechText.isNotBlank()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(character.themeColorHex)),
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .widthIn(max = 220.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(character.characterEmoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = speechText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 3D Canvas Figurine
        Box(
            modifier = Modifier
                .size(size)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            // Ground Shadow
            Canvas(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(18.dp)
                    .align(Alignment.BottomCenter)
            ) {
                val shadowWidth = size.toPx() * 0.65f * (1f - (currentOffsetY / -50f).coerceIn(0f, 0.4f))
                drawOval(
                    color = Color.Black.copy(alpha = 0.18f * (1f - (currentOffsetY / -60f).coerceIn(0f, 0.5f))),
                    topLeft = Offset((this.size.width - shadowWidth) / 2f, this.size.height * 0.3f),
                    size = Size(shadowWidth, this.size.height * 0.7f)
                )
            }

            // Main 3D Figurine Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = currentOffsetY
                        scaleX = currentScale
                        scaleY = currentScale
                        rotationZ = currentRotation
                    }
            ) {
                draw3DLetterCharacter(character, this.size)
            }
        }
    }
}

/**
 * Procedural 3D clay/toy rendering for each of the 26 canonical characters.
 * Accurately implements character morphology from reference designs.
 */
private fun DrawScope.draw3DLetterCharacter(character: LetterCharacter, canvasSize: Size) {
    val w = canvasSize.width
    val h = canvasSize.height
    val primaryColor = Color(character.themeColorHex)
    val secondaryColor = Color(character.secondaryColorHex)

    // Base Lighting Gradients
    val bodyBrush = Brush.radialGradient(
        colors = listOf(
            secondaryColor.copy(alpha = 0.95f),
            primaryColor,
            Color(0xFF0F172A).copy(alpha = 0.85f)
        ),
        center = Offset(w * 0.38f, h * 0.35f),
        radius = w * 0.65f
    )

    val specularBrush = Brush.radialGradient(
        colors = listOf(Color.White.copy(alpha = 0.65f), Color.Transparent),
        center = Offset(w * 0.35f, h * 0.28f),
        radius = w * 0.32f
    )

    when (character.letter) {
        'A' -> draw3DApple(w, h, bodyBrush, specularBrush)
        'B' -> draw3DBear(w, h, bodyBrush, specularBrush)
        'C' -> draw3DCat(w, h, bodyBrush, specularBrush)
        'D' -> draw3DDuck(w, h, bodyBrush, specularBrush)
        'E' -> draw3DElephant(w, h, bodyBrush, specularBrush)
        'F' -> draw3DFish(w, h, bodyBrush, specularBrush)
        'G' -> draw3DGiraffe(w, h, bodyBrush, specularBrush)
        'H' -> draw3DHorse(w, h, bodyBrush, specularBrush)
        'I' -> draw3DIceCream(w, h, bodyBrush, specularBrush)
        'J' -> draw3DJellyfish(w, h, bodyBrush, specularBrush)
        'K' -> draw3DKoala(w, h, bodyBrush, specularBrush)
        'L' -> draw3DLion(w, h, bodyBrush, specularBrush)
        'M' -> draw3DMonkey(w, h, bodyBrush, specularBrush)
        'N' -> draw3DNest(w, h, bodyBrush, specularBrush)
        'O' -> draw3DOwl(w, h, bodyBrush, specularBrush)
        'P' -> draw3DPenguin(w, h, bodyBrush, specularBrush)
        'Q' -> draw3DQueen(w, h, bodyBrush, specularBrush)
        'R' -> draw3DRabbit(w, h, bodyBrush, specularBrush)
        'S' -> draw3DSnake(w, h, bodyBrush, specularBrush)
        'T' -> draw3DTiger(w, h, bodyBrush, specularBrush)
        'U' -> draw3DUmbrella(w, h, bodyBrush, specularBrush)
        'V' -> draw3DViolin(w, h, bodyBrush, specularBrush)
        'W' -> draw3DWhale(w, h, bodyBrush, specularBrush)
        'X' -> draw3DXylophone(w, h, bodyBrush, specularBrush)
        'Y' -> draw3DYak(w, h, bodyBrush, specularBrush)
        'Z' -> draw3DZebra(w, h, bodyBrush, specularBrush)
        else -> drawGenericLetter(w, h, character, bodyBrush, specularBrush)
    }
}

// ----------------------------------------------------
// INDIVIDUAL 3D CANONICAL CHARACTERS (A to Z)
// ----------------------------------------------------

private fun DrawScope.drawCuteEyesAndMouth(
    eyeLeft: Offset,
    eyeRight: Offset,
    mouthCenter: Offset,
    eyeRadius: Float = 6f,
    blush: Boolean = true
) {
    // Left Eye
    drawCircle(Color(0xFF1E293B), radius = eyeRadius, center = eyeLeft)
    drawCircle(Color.White, radius = eyeRadius * 0.4f, center = Offset(eyeLeft.x - eyeRadius * 0.25f, eyeLeft.y - eyeRadius * 0.25f))

    // Right Eye
    drawCircle(Color(0xFF1E293B), radius = eyeRadius, center = eyeRight)
    drawCircle(Color.White, radius = eyeRadius * 0.4f, center = Offset(eyeRight.x - eyeRadius * 0.25f, eyeRight.y - eyeRadius * 0.25f))

    // Rosy Cheeks
    if (blush) {
        drawCircle(Color(0xFFFF8DA1).copy(alpha = 0.55f), radius = eyeRadius * 0.9f, center = Offset(eyeLeft.x - eyeRadius * 1.3f, eyeLeft.y + eyeRadius * 0.9f))
        drawCircle(Color(0xFFFF8DA1).copy(alpha = 0.55f), radius = eyeRadius * 0.9f, center = Offset(eyeRight.x + eyeRadius * 1.3f, eyeRight.y + eyeRadius * 0.9f))
    }

    // Smiling Mouth Arc
    val mouthPath = Path().apply {
        moveTo(mouthCenter.x - eyeRadius * 1.2f, mouthCenter.y)
        quadraticTo(mouthCenter.x, mouthCenter.y + eyeRadius * 1.1f, mouthCenter.x + eyeRadius * 1.2f, mouthCenter.y)
    }
    drawPath(mouthPath, Color(0xFF1E293B), style = Stroke(width = 2.5f, cap = StrokeCap.Round))
}

// A -> Apple (Red glossy 3D apple character with green leaf & stem)
private fun DrawScope.draw3DApple(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    // Stem
    val stemPath = Path().apply {
        moveTo(w * 0.5f, h * 0.18f)
        quadraticTo(w * 0.54f, h * 0.08f, w * 0.58f, h * 0.06f)
    }
    drawPath(stemPath, Color(0xFF78350F), style = Stroke(width = 6f, cap = StrokeCap.Round))

    // Green Leaf
    val leafPath = Path().apply {
        moveTo(w * 0.53f, h * 0.14f)
        quadraticTo(w * 0.68f, h * 0.08f, w * 0.72f, h * 0.16f)
        quadraticTo(w * 0.62f, h * 0.22f, w * 0.53f, h * 0.14f)
    }
    drawPath(leafPath, Color(0xFF22C55E))
    drawPath(leafPath, Color(0xFF16A34A), style = Stroke(width = 2f))

    // Letter 'A' shaped Glossy Apple Body
    val applePath = Path().apply {
        // Left Leg
        moveTo(w * 0.22f, h * 0.88f)
        cubicTo(w * 0.15f, h * 0.88f, w * 0.18f, h * 0.35f, w * 0.44f, h * 0.18f)
        // Top Apple Dimple
        quadraticTo(w * 0.5f, h * 0.22f, w * 0.56f, h * 0.18f)
        // Right Leg
        cubicTo(w * 0.82f, h * 0.35f, w * 0.85f, h * 0.88f, w * 0.78f, h * 0.88f)
        // Right Bottom curve
        quadraticTo(w * 0.66f, h * 0.88f, w * 0.64f, h * 0.72f)
        // Crossbar bottom
        lineTo(w * 0.36f, h * 0.72f)
        quadraticTo(w * 0.34f, h * 0.88f, w * 0.22f, h * 0.88f)
        close()
    }
    drawPath(applePath, bodyBrush)
    drawPath(applePath, specularBrush)

    // Inner Triangle of 'A'
    val innerHole = Path().apply {
        moveTo(w * 0.5f, h * 0.42f)
        lineTo(w * 0.60f, h * 0.60f)
        lineTo(w * 0.40f, h * 0.60f)
        close()
    }
    drawPath(innerHole, Color(0xFF7F1D1D).copy(alpha = 0.8f))

    // Cute Face on Top Bar
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.44f, h * 0.34f),
        eyeRight = Offset(w * 0.56f, h * 0.34f),
        mouthCenter = Offset(w * 0.5f, h * 0.38f),
        eyeRadius = w * 0.038f
    )
}

// B -> Bear (Honey warm brown 3D cuddly bear figurine)
private fun DrawScope.draw3DBear(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    // Bear Ears
    drawCircle(Color(0xFF854D0E), radius = w * 0.12f, center = Offset(w * 0.32f, h * 0.22f))
    drawCircle(Color(0xFFFDE68A), radius = w * 0.06f, center = Offset(w * 0.32f, h * 0.22f))

    drawCircle(Color(0xFF854D0E), radius = w * 0.12f, center = Offset(w * 0.68f, h * 0.22f))
    drawCircle(Color(0xFFFDE68A), radius = w * 0.06f, center = Offset(w * 0.68f, h * 0.22f))

    // B Body (Two Rounded Bumps)
    val bPath = Path().apply {
        moveTo(w * 0.28f, h * 0.18f)
        lineTo(w * 0.52f, h * 0.18f)
        cubicTo(w * 0.82f, h * 0.18f, w * 0.82f, h * 0.52f, w * 0.54f, h * 0.52f)
        cubicTo(w * 0.88f, h * 0.52f, w * 0.88f, h * 0.88f, w * 0.52f, h * 0.88f)
        lineTo(w * 0.28f, h * 0.88f)
        close()
    }
    drawPath(bPath, bodyBrush)
    drawPath(bPath, specularBrush)

    // Inner Holes
    drawRoundRect(Color(0xFF451A03), topLeft = Offset(w * 0.42f, h * 0.28f), size = Size(w * 0.18f, h * 0.16f), cornerRadius = CornerRadius(14f, 14f))
    drawRoundRect(Color(0xFF451A03), topLeft = Offset(w * 0.42f, h * 0.60f), size = Size(w * 0.20f, h * 0.18f), cornerRadius = CornerRadius(14f, 14f))

    // Bear Snout & Face on Top Loop
    drawOval(Color(0xFFFDE68A), topLeft = Offset(w * 0.24f, h * 0.32f), size = Size(w * 0.22f, h * 0.16f))
    drawCircle(Color(0xFF1E293B), radius = w * 0.035f, center = Offset(w * 0.35f, h * 0.38f))

    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.26f, h * 0.28f),
        eyeRight = Offset(w * 0.40f, h * 0.28f),
        mouthCenter = Offset(w * 0.35f, h * 0.44f),
        eyeRadius = w * 0.032f
    )
}

// C -> Cat (Orange striped ginger 3D cat curved into C)
private fun DrawScope.draw3DCat(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    // Cat Ears
    val leftEar = Path().apply {
        moveTo(w * 0.60f, h * 0.14f)
        lineTo(w * 0.68f, h * 0.04f)
        lineTo(w * 0.74f, h * 0.18f)
        close()
    }
    drawPath(leftEar, Color(0xFFEA580C))
    drawPath(leftEar, Color(0xFFFDE047), style = Stroke(width = 2f))

    val rightEar = Path().apply {
        moveTo(w * 0.74f, h * 0.18f)
        lineTo(w * 0.84f, h * 0.08f)
        lineTo(w * 0.85f, h * 0.24f)
        close()
    }
    drawPath(rightEar, Color(0xFFEA580C))

    // C Arch Body
    val cPath = Path().apply {
        moveTo(w * 0.78f, h * 0.26f)
        cubicTo(w * 0.32f, h * 0.10f, w * 0.12f, h * 0.40f, w * 0.24f, h * 0.70f)
        cubicTo(w * 0.32f, h * 0.92f, w * 0.76f, h * 0.88f, w * 0.78f, h * 0.76f)
        cubicTo(w * 0.52f, h * 0.74f, w * 0.42f, h * 0.58f, w * 0.44f, h * 0.42f)
        cubicTo(w * 0.48f, h * 0.28f, w * 0.68f, h * 0.26f, w * 0.78f, h * 0.26f)
        close()
    }
    drawPath(cPath, bodyBrush)
    drawPath(cPath, specularBrush)

    // Ginger Stripes
    val stripe1 = Path().apply {
        moveTo(w * 0.22f, h * 0.48f)
        quadraticTo(w * 0.34f, h * 0.50f, w * 0.26f, h * 0.56f)
    }
    drawPath(stripe1, Color(0xFF9A3412), style = Stroke(width = 4f, cap = StrokeCap.Round))

    // Cat Face on top Right Head
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.68f, h * 0.22f),
        eyeRight = Offset(w * 0.78f, h * 0.24f),
        mouthCenter = Offset(w * 0.74f, h * 0.29f),
        eyeRadius = w * 0.035f
    )
}

// D -> Duck (Sunny yellow 3D rubber duckling formed into D)
private fun DrawScope.draw3DDuck(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    // D Arch Body
    val dPath = Path().apply {
        moveTo(w * 0.32f, h * 0.18f)
        lineTo(w * 0.54f, h * 0.18f)
        cubicTo(w * 0.88f, h * 0.20f, w * 0.88f, h * 0.84f, w * 0.54f, h * 0.84f)
        lineTo(w * 0.32f, h * 0.84f)
        close()
    }
    drawPath(dPath, bodyBrush)
    drawPath(dPath, specularBrush)

    // Inner Hole
    drawRoundRect(Color(0xFF854D0E).copy(alpha = 0.5f), topLeft = Offset(w * 0.45f, h * 0.34f), size = Size(w * 0.22f, h * 0.36f), cornerRadius = CornerRadius(16f, 16f))

    // Duck Bill & Head
    drawOval(Color(0xFFF97316), topLeft = Offset(w * 0.66f, h * 0.28f), size = Size(w * 0.22f, h * 0.12f))

    // Cute Eye
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.52f, h * 0.26f),
        eyeRight = Offset(w * 0.62f, h * 0.26f),
        mouthCenter = Offset(w * 0.72f, h * 0.34f),
        eyeRadius = w * 0.035f,
        blush = false
    )
}

// E -> Elephant (Pastel baby blue 3D elephant with ear & trunk as E)
private fun DrawScope.draw3DElephant(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    // Elephant Big Ear
    drawOval(Color(0xFF38BDF8), topLeft = Offset(w * 0.16f, h * 0.20f), size = Size(w * 0.26f, h * 0.38f))
    drawOval(Color(0xFFF472B6).copy(alpha = 0.4f), topLeft = Offset(w * 0.20f, h * 0.26f), size = Size(w * 0.16f, h * 0.24f))

    // E Letter Body with 3 prongs
    val ePath = Path().apply {
        moveTo(w * 0.32f, h * 0.18f)
        lineTo(w * 0.76f, h * 0.18f)
        lineTo(w * 0.76f, h * 0.34f)
        lineTo(w * 0.48f, h * 0.34f)
        lineTo(w * 0.48f, h * 0.45f)
        lineTo(w * 0.70f, h * 0.45f)
        lineTo(w * 0.70f, h * 0.58f)
        lineTo(w * 0.48f, h * 0.58f)
        lineTo(w * 0.48f, h * 0.72f)
        lineTo(w * 0.76f, h * 0.72f)
        lineTo(w * 0.76f, h * 0.86f)
        lineTo(w * 0.32f, h * 0.86f)
        close()
    }
    drawPath(ePath, bodyBrush)
    drawPath(ePath, specularBrush)

    // Elephant Trunk Curved on Middle Prong
    val trunk = Path().apply {
        moveTo(w * 0.70f, h * 0.52f)
        quadraticTo(w * 0.86f, h * 0.50f, w * 0.84f, h * 0.62f)
    }
    drawPath(trunk, Color(0xFF0284C7), style = Stroke(width = 8f, cap = StrokeCap.Round))

    // Eye
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.46f, h * 0.26f),
        eyeRight = Offset(w * 0.58f, h * 0.26f),
        mouthCenter = Offset(w * 0.52f, h * 0.32f),
        eyeRadius = w * 0.035f
    )
}

// F -> Fish (Orange and white clownfish formed into F)
private fun DrawScope.draw3DFish(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    val fPath = Path().apply {
        moveTo(w * 0.26f, h * 0.18f)
        lineTo(w * 0.78f, h * 0.18f)
        lineTo(w * 0.78f, h * 0.34f)
        lineTo(w * 0.44f, h * 0.34f)
        lineTo(w * 0.44f, h * 0.48f)
        lineTo(w * 0.68f, h * 0.48f)
        lineTo(w * 0.68f, h * 0.62f)
        lineTo(w * 0.44f, h * 0.62f)
        lineTo(w * 0.44f, h * 0.88f)
        lineTo(w * 0.26f, h * 0.88f)
        close()
    }
    drawPath(fPath, bodyBrush)
    drawPath(fPath, specularBrush)

    // Clownfish White Stripes
    drawRoundRect(Color.White, topLeft = Offset(w * 0.52f, h * 0.18f), size = Size(w * 0.08f, h * 0.16f), cornerRadius = CornerRadius(4f, 4f))
    drawRoundRect(Color.White, topLeft = Offset(w * 0.26f, h * 0.40f), size = Size(w * 0.18f, h * 0.08f), cornerRadius = CornerRadius(4f, 4f))

    // Cute Fish Eye & Mouth on Top
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.36f, h * 0.25f),
        eyeRight = Offset(w * 0.44f, h * 0.25f),
        mouthCenter = Offset(w * 0.72f, h * 0.25f),
        eyeRadius = w * 0.035f
    )
}

// G -> Giraffe (Golden spotted giraffe formed into G)
private fun DrawScope.draw3DGiraffe(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    // Giraffe Horns / Ossicones
    drawCircle(Color(0xFF78350F), radius = w * 0.04f, center = Offset(w * 0.68f, h * 0.12f))
    drawCircle(Color(0xFF78350F), radius = w * 0.04f, center = Offset(w * 0.78f, h * 0.14f))

    // G Body
    val gPath = Path().apply {
        moveTo(w * 0.78f, h * 0.28f)
        cubicTo(w * 0.32f, h * 0.12f, w * 0.14f, h * 0.40f, w * 0.24f, h * 0.70f)
        cubicTo(w * 0.32f, h * 0.90f, w * 0.78f, h * 0.90f, w * 0.78f, h * 0.58f)
        lineTo(w * 0.54f, h * 0.58f)
        lineTo(w * 0.54f, h * 0.68f)
        lineTo(w * 0.68f, h * 0.68f)
        cubicTo(w * 0.68f, h * 0.78f, w * 0.40f, h * 0.78f, w * 0.38f, h * 0.65f)
        cubicTo(w * 0.36f, h * 0.45f, w * 0.52f, h * 0.28f, w * 0.74f, h * 0.28f)
        close()
    }
    drawPath(gPath, bodyBrush)
    drawPath(gPath, specularBrush)

    // Brown Spots
    drawCircle(Color(0xFF78350F).copy(alpha = 0.7f), radius = w * 0.04f, center = Offset(w * 0.32f, h * 0.48f))
    drawCircle(Color(0xFF78350F).copy(alpha = 0.7f), radius = w * 0.05f, center = Offset(w * 0.35f, h * 0.75f))

    // Eyes
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.65f, h * 0.24f),
        eyeRight = Offset(w * 0.74f, h * 0.24f),
        mouthCenter = Offset(w * 0.70f, h * 0.32f),
        eyeRadius = w * 0.035f
    )
}

// H -> Horse (Brown horse figurine formed into H)
private fun DrawScope.draw3DHorse(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    // Horse Mane
    val mane = Path().apply {
        moveTo(w * 0.32f, h * 0.12f)
        lineTo(w * 0.36f, h * 0.04f)
        lineTo(w * 0.42f, h * 0.14f)
        close()
    }
    drawPath(mane, Color(0xFF451A03))

    // H Shape
    drawRoundRect(bodyBrush, topLeft = Offset(w * 0.24f, h * 0.18f), size = Size(w * 0.18f, h * 0.68f), cornerRadius = CornerRadius(14f, 14f))
    drawRoundRect(bodyBrush, topLeft = Offset(w * 0.58f, h * 0.18f), size = Size(w * 0.18f, h * 0.68f), cornerRadius = CornerRadius(14f, 14f))
    drawRoundRect(bodyBrush, topLeft = Offset(w * 0.36f, h * 0.48f), size = Size(w * 0.28f, h * 0.16f), cornerRadius = CornerRadius(10f, 10f))

    // Horse Head on Left Bar
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.28f, h * 0.26f),
        eyeRight = Offset(w * 0.38f, h * 0.26f),
        mouthCenter = Offset(w * 0.33f, h * 0.36f),
        eyeRadius = w * 0.032f
    )
}

// I -> Ice Cream (Waffle cone with pink strawberry scoop formed into I)
private fun DrawScope.draw3DIceCream(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    // Waffle Cone (Bottom of I)
    val cone = Path().apply {
        moveTo(w * 0.32f, h * 0.48f)
        lineTo(w * 0.68f, h * 0.48f)
        lineTo(w * 0.50f, h * 0.88f)
        close()
    }
    drawPath(cone, Color(0xFFD97706))

    // Strawberry Scoop (Top of I)
    drawCircle(bodyBrush, radius = w * 0.26f, center = Offset(w * 0.50f, h * 0.34f))
    drawCircle(specularBrush, radius = w * 0.24f, center = Offset(w * 0.50f, h * 0.34f))

    // Sprinkles
    drawCircle(Color(0xFF38BDF8), radius = 3f, center = Offset(w * 0.42f, h * 0.22f))
    drawCircle(Color(0xFFFACC15), radius = 3f, center = Offset(w * 0.58f, h * 0.24f))
    drawCircle(Color(0xFF22C55E), radius = 3f, center = Offset(w * 0.50f, h * 0.18f))

    // Face
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.42f, h * 0.32f),
        eyeRight = Offset(w * 0.58f, h * 0.32f),
        mouthCenter = Offset(w * 0.50f, h * 0.39f),
        eyeRadius = w * 0.035f
    )
}

// J -> Jellyfish (Lavender pastel jellyfish with tentacles formed into J)
private fun DrawScope.draw3DJellyfish(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    // Dome Head (Top of J)
    drawArc(bodyBrush, startAngle = 180f, sweepAngle = 180f, useCenter = true, topLeft = Offset(w * 0.30f, h * 0.18f), size = Size(w * 0.48f, h * 0.34f))

    // J Curved Hook Body
    val jPath = Path().apply {
        moveTo(w * 0.62f, h * 0.34f)
        lineTo(w * 0.62f, h * 0.68f)
        cubicTo(w * 0.62f, h * 0.90f, w * 0.26f, h * 0.90f, w * 0.24f, h * 0.68f)
        lineTo(w * 0.38f, h * 0.68f)
        cubicTo(w * 0.38f, h * 0.78f, w * 0.48f, h * 0.78f, w * 0.48f, h * 0.68f)
        lineTo(w * 0.48f, h * 0.34f)
        close()
    }
    drawPath(jPath, bodyBrush)
    drawPath(jPath, specularBrush)

    // Cute Face on Dome
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.45f, h * 0.28f),
        eyeRight = Offset(w * 0.58f, h * 0.28f),
        mouthCenter = Offset(w * 0.52f, h * 0.33f),
        eyeRadius = w * 0.035f
    )
}

// K -> Koala (Grey koala hugging letter K)
private fun DrawScope.draw3DKoala(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    // Koala Ears
    drawCircle(Color(0xFF64748B), radius = w * 0.09f, center = Offset(w * 0.24f, h * 0.22f))
    drawCircle(Color(0xFFE2E8F0), radius = w * 0.05f, center = Offset(w * 0.24f, h * 0.22f))

    // K Shape
    drawRoundRect(bodyBrush, topLeft = Offset(w * 0.26f, h * 0.18f), size = Size(w * 0.18f, h * 0.68f), cornerRadius = CornerRadius(14f, 14f))

    val topDiagonal = Path().apply {
        moveTo(w * 0.40f, h * 0.50f)
        lineTo(w * 0.74f, h * 0.18f)
        lineTo(w * 0.84f, h * 0.28f)
        lineTo(w * 0.50f, h * 0.58f)
        close()
    }
    drawPath(topDiagonal, bodyBrush)

    val bottomDiagonal = Path().apply {
        moveTo(w * 0.48f, h * 0.52f)
        lineTo(w * 0.82f, h * 0.86f)
        lineTo(w * 0.70f, h * 0.88f)
        lineTo(w * 0.38f, h * 0.60f)
        close()
    }
    drawPath(bottomDiagonal, bodyBrush)

    // Nose & Face on Main Pillar
    drawOval(Color(0xFF1E293B), topLeft = Offset(w * 0.30f, h * 0.32f), size = Size(w * 0.10f, h * 0.12f))
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.28f, h * 0.26f),
        eyeRight = Offset(w * 0.40f, h * 0.26f),
        mouthCenter = Offset(w * 0.35f, h * 0.46f),
        eyeRadius = w * 0.03f
    )
}

// L -> Lion (Golden lion with mane formed into L)
private fun DrawScope.draw3DLion(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    // Mane on Top
    drawCircle(Color(0xFFB45309), radius = w * 0.22f, center = Offset(w * 0.36f, h * 0.32f))

    // L Body
    val lPath = Path().apply {
        moveTo(w * 0.26f, h * 0.22f)
        lineTo(w * 0.46f, h * 0.22f)
        lineTo(w * 0.46f, h * 0.70f)
        lineTo(w * 0.82f, h * 0.70f)
        lineTo(w * 0.82f, h * 0.86f)
        lineTo(w * 0.26f, h * 0.86f)
        close()
    }
    drawPath(lPath, bodyBrush)
    drawPath(lPath, specularBrush)

    // Lion Face
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.32f, h * 0.30f),
        eyeRight = Offset(w * 0.42f, h * 0.30f),
        mouthCenter = Offset(w * 0.37f, h * 0.38f),
        eyeRadius = w * 0.035f
    )
}

// M -> Monkey (Brown monkey with round ears formed into M)
private fun DrawScope.draw3DMonkey(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    // Monkey Ears
    drawCircle(Color(0xFFD97706), radius = w * 0.08f, center = Offset(w * 0.18f, h * 0.26f))
    drawCircle(Color(0xFFD97706), radius = w * 0.08f, center = Offset(w * 0.82f, h * 0.26f))

    // M Shape
    val mPath = Path().apply {
        moveTo(w * 0.20f, h * 0.86f)
        lineTo(w * 0.20f, h * 0.22f)
        lineTo(w * 0.38f, h * 0.22f)
        lineTo(w * 0.50f, h * 0.52f)
        lineTo(w * 0.62f, h * 0.22f)
        lineTo(w * 0.80f, h * 0.22f)
        lineTo(w * 0.80f, h * 0.86f)
        lineTo(w * 0.66f, h * 0.86f)
        lineTo(w * 0.66f, h * 0.48f)
        lineTo(w * 0.56f, h * 0.70f)
        lineTo(w * 0.44f, h * 0.70f)
        lineTo(w * 0.34f, h * 0.48f)
        lineTo(w * 0.34f, h * 0.86f)
        close()
    }
    drawPath(mPath, bodyBrush)
    drawPath(mPath, specularBrush)

    // Face in Center
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.44f, h * 0.32f),
        eyeRight = Offset(w * 0.56f, h * 0.32f),
        mouthCenter = Offset(w * 0.50f, h * 0.40f),
        eyeRadius = w * 0.035f
    )
}

// N -> Nest (Twiggy nest with blue eggs formed into N)
private fun DrawScope.draw3DNest(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    // N Shape
    val nPath = Path().apply {
        moveTo(w * 0.22f, h * 0.86f)
        lineTo(w * 0.22f, h * 0.18f)
        lineTo(w * 0.40f, h * 0.18f)
        lineTo(w * 0.66f, h * 0.68f)
        lineTo(w * 0.66f, h * 0.18f)
        lineTo(w * 0.80f, h * 0.18f)
        lineTo(w * 0.80f, h * 0.86f)
        lineTo(w * 0.62f, h * 0.86f)
        lineTo(w * 0.36f, h * 0.36f)
        lineTo(w * 0.36f, h * 0.86f)
        close()
    }
    drawPath(nPath, bodyBrush)
    drawPath(nPath, specularBrush)

    // Shiny Blue Eggs in Center
    drawOval(Color(0xFF38BDF8), topLeft = Offset(w * 0.46f, h * 0.52f), size = Size(w * 0.12f, h * 0.18f))
    drawOval(Color(0xFF0284C7), topLeft = Offset(w * 0.56f, h * 0.56f), size = Size(w * 0.10f, h * 0.16f))

    // Eyes
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.26f, h * 0.28f),
        eyeRight = Offset(w * 0.34f, h * 0.28f),
        mouthCenter = Offset(w * 0.30f, h * 0.36f),
        eyeRadius = w * 0.03f
    )
}

// O -> Owl (Sky blue feathered owl formed into O)
private fun DrawScope.draw3DOwl(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    // O Ring
    drawCircle(bodyBrush, radius = w * 0.35f, center = Offset(w * 0.50f, h * 0.50f))
    drawCircle(Color(0xFF0F172A).copy(alpha = 0.5f), radius = w * 0.18f, center = Offset(w * 0.50f, h * 0.50f))

    // Big Owl Eyes
    drawCircle(Color.White, radius = w * 0.12f, center = Offset(w * 0.38f, h * 0.34f))
    drawCircle(Color(0xFF1E293B), radius = w * 0.06f, center = Offset(w * 0.38f, h * 0.34f))
    drawCircle(Color.White, radius = w * 0.02f, center = Offset(w * 0.36f, h * 0.32f))

    drawCircle(Color.White, radius = w * 0.12f, center = Offset(w * 0.62f, h * 0.34f))
    drawCircle(Color(0xFF1E293B), radius = w * 0.06f, center = Offset(w * 0.62f, h * 0.34f))
    drawCircle(Color.White, radius = w * 0.02f, center = Offset(w * 0.60f, h * 0.32f))

    // Yellow Beak
    val beak = Path().apply {
        moveTo(w * 0.46f, h * 0.40f)
        lineTo(w * 0.54f, h * 0.40f)
        lineTo(w * 0.50f, h * 0.48f)
        close()
    }
    drawPath(beak, Color(0xFFFBBF24))
}

// P -> Penguin (Tuxedo black and white penguin formed into P)
private fun DrawScope.draw3DPenguin(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    val pPath = Path().apply {
        moveTo(w * 0.28f, h * 0.88f)
        lineTo(w * 0.28f, h * 0.18f)
        lineTo(w * 0.58f, h * 0.18f)
        cubicTo(w * 0.86f, h * 0.18f, w * 0.86f, h * 0.56f, w * 0.58f, h * 0.56f)
        lineTo(w * 0.46f, h * 0.56f)
        lineTo(w * 0.46f, h * 0.88f)
        close()
    }
    drawPath(pPath, bodyBrush)

    // White Penguin Belly
    drawRoundRect(Color.White, topLeft = Offset(w * 0.32f, h * 0.24f), size = Size(w * 0.16f, h * 0.30f), cornerRadius = CornerRadius(14f, 14f))

    // Orange Beak
    val beak = Path().apply {
        moveTo(w * 0.64f, h * 0.34f)
        lineTo(w * 0.74f, h * 0.36f)
        lineTo(w * 0.64f, h * 0.40f)
        close()
    }
    drawPath(beak, Color(0xFFF97316))

    // Eyes
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.50f, h * 0.30f),
        eyeRight = Offset(w * 0.58f, h * 0.30f),
        mouthCenter = Offset(w * 0.54f, h * 0.38f),
        eyeRadius = w * 0.035f,
        blush = false
    )
}

// Q -> Queen (Royal purple queen with crown formed into Q)
private fun DrawScope.draw3DQueen(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    // Golden Crown on Top
    val crown = Path().apply {
        moveTo(w * 0.36f, h * 0.16f)
        lineTo(w * 0.42f, h * 0.06f)
        lineTo(w * 0.50f, h * 0.12f)
        lineTo(w * 0.58f, h * 0.06f)
        lineTo(w * 0.64f, h * 0.16f)
        close()
    }
    drawPath(crown, Color(0xFFFBBF24))

    // Q Ring Body
    drawCircle(bodyBrush, radius = w * 0.34f, center = Offset(w * 0.50f, h * 0.48f))
    drawCircle(Color(0xFF3B0764), radius = w * 0.17f, center = Offset(w * 0.50f, h * 0.48f))

    // Q Tail with Pearl
    val tail = Path().apply {
        moveTo(w * 0.55f, h * 0.62f)
        lineTo(w * 0.82f, h * 0.86f)
        lineTo(w * 0.72f, h * 0.88f)
        lineTo(w * 0.48f, h * 0.66f)
        close()
    }
    drawPath(tail, bodyBrush)

    // Face
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.44f, h * 0.30f),
        eyeRight = Offset(w * 0.56f, h * 0.30f),
        mouthCenter = Offset(w * 0.50f, h * 0.36f),
        eyeRadius = w * 0.035f
    )
}

// R -> Rabbit (White bunny with pink ears formed into R)
private fun DrawScope.draw3DRabbit(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    // Bunny Ears
    drawOval(Color.White, topLeft = Offset(w * 0.34f, h * 0.02f), size = Size(w * 0.12f, h * 0.24f))
    drawOval(Color(0xFFF472B6), topLeft = Offset(w * 0.37f, h * 0.06f), size = Size(w * 0.06f, h * 0.16f))

    drawOval(Color.White, topLeft = Offset(w * 0.54f, h * 0.02f), size = Size(w * 0.12f, h * 0.24f))
    drawOval(Color(0xFFF472B6), topLeft = Offset(w * 0.57f, h * 0.06f), size = Size(w * 0.06f, h * 0.16f))

    // R Body
    val rPath = Path().apply {
        moveTo(w * 0.28f, h * 0.88f)
        lineTo(w * 0.28f, h * 0.20f)
        lineTo(w * 0.58f, h * 0.20f)
        cubicTo(w * 0.84f, h * 0.20f, w * 0.84f, h * 0.54f, w * 0.58f, h * 0.54f)
        lineTo(w * 0.46f, h * 0.54f)
        lineTo(w * 0.78f, h * 0.88f)
        lineTo(w * 0.62f, h * 0.88f)
        lineTo(w * 0.44f, h * 0.66f)
        lineTo(w * 0.44f, h * 0.88f)
        close()
    }
    drawPath(rPath, bodyBrush)
    drawPath(rPath, specularBrush)

    // Face on Top Loop
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.44f, h * 0.30f),
        eyeRight = Offset(w * 0.56f, h * 0.30f),
        mouthCenter = Offset(w * 0.50f, h * 0.38f),
        eyeRadius = w * 0.035f
    )
}

// S -> Snake (Green spotted friendly snake curved into S)
private fun DrawScope.draw3DSnake(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    val sPath = Path().apply {
        moveTo(w * 0.72f, h * 0.22f)
        cubicTo(w * 0.38f, h * 0.10f, w * 0.16f, h * 0.42f, w * 0.50f, h * 0.52f)
        cubicTo(w * 0.84f, h * 0.62f, w * 0.64f, h * 0.90f, w * 0.28f, h * 0.84f)
    }
    drawPath(sPath, bodyBrush, style = Stroke(width = w * 0.18f, cap = StrokeCap.Round))

    // Eyes on Top Head
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.64f, h * 0.18f),
        eyeRight = Offset(w * 0.74f, h * 0.22f),
        mouthCenter = Offset(w * 0.72f, h * 0.28f),
        eyeRadius = w * 0.035f
    )
}

// T -> Tiger (Orange tiger with black stripes formed into T)
private fun DrawScope.draw3DTiger(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    // Tiger Ears
    drawCircle(Color(0xFFEA580C), radius = w * 0.08f, center = Offset(w * 0.22f, h * 0.14f))
    drawCircle(Color(0xFFEA580C), radius = w * 0.08f, center = Offset(w * 0.78f, h * 0.14f))

    // T Shape
    drawRoundRect(bodyBrush, topLeft = Offset(w * 0.18f, h * 0.18f), size = Size(w * 0.64f, h * 0.18f), cornerRadius = CornerRadius(14f, 14f))
    drawRoundRect(bodyBrush, topLeft = Offset(w * 0.41f, h * 0.34f), size = Size(w * 0.18f, h * 0.54f), cornerRadius = CornerRadius(12f, 12f))

    // Black Tiger Stripes
    drawRoundRect(Color(0xFF1E293B), topLeft = Offset(w * 0.44f, h * 0.45f), size = Size(w * 0.12f, h * 0.05f), cornerRadius = CornerRadius(4f, 4f))
    drawRoundRect(Color(0xFF1E293B), topLeft = Offset(w * 0.44f, h * 0.60f), size = Size(w * 0.12f, h * 0.05f), cornerRadius = CornerRadius(4f, 4f))

    // Face in Top Bar Center
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.42f, h * 0.24f),
        eyeRight = Offset(w * 0.58f, h * 0.24f),
        mouthCenter = Offset(w * 0.50f, h * 0.30f),
        eyeRadius = w * 0.035f
    )
}

// U -> Umbrella (Rainbow umbrella formed into U)
private fun DrawScope.draw3DUmbrella(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    val uPath = Path().apply {
        moveTo(w * 0.26f, h * 0.18f)
        lineTo(w * 0.42f, h * 0.18f)
        lineTo(w * 0.42f, h * 0.62f)
        cubicTo(w * 0.42f, h * 0.76f, w * 0.58f, h * 0.76f, w * 0.58f, h * 0.62f)
        lineTo(w * 0.58f, h * 0.18f)
        lineTo(w * 0.74f, h * 0.18f)
        lineTo(w * 0.74f, h * 0.64f)
        cubicTo(w * 0.74f, h * 0.90f, w * 0.26f, h * 0.90f, w * 0.26f, h * 0.64f)
        close()
    }
    drawPath(uPath, bodyBrush)
    drawPath(uPath, specularBrush)

    // Eyes
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.44f, h * 0.48f),
        eyeRight = Offset(w * 0.56f, h * 0.48f),
        mouthCenter = Offset(w * 0.50f, h * 0.56f),
        eyeRadius = w * 0.035f
    )
}

// V -> Violin (Wood violin formed into V)
private fun DrawScope.draw3DViolin(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    val vPath = Path().apply {
        moveTo(w * 0.22f, h * 0.18f)
        lineTo(w * 0.38f, h * 0.18f)
        lineTo(w * 0.50f, h * 0.68f)
        lineTo(w * 0.62f, h * 0.18f)
        lineTo(w * 0.78f, h * 0.18f)
        lineTo(w * 0.58f, h * 0.88f)
        lineTo(w * 0.42f, h * 0.88f)
        close()
    }
    drawPath(vPath, bodyBrush)
    drawPath(vPath, specularBrush)

    // Golden Strings
    drawLine(Color(0xFFFBBF24), start = Offset(w * 0.47f, h * 0.25f), end = Offset(w * 0.49f, h * 0.80f), strokeWidth = 2f)
    drawLine(Color(0xFFFBBF24), start = Offset(w * 0.53f, h * 0.25f), end = Offset(w * 0.51f, h * 0.80f), strokeWidth = 2f)

    // Face
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.42f, h * 0.45f),
        eyeRight = Offset(w * 0.58f, h * 0.45f),
        mouthCenter = Offset(w * 0.50f, h * 0.54f),
        eyeRadius = w * 0.03f
    )
}

// W -> Whale (Ocean blue whale with water spout formed into W)
private fun DrawScope.draw3DWhale(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    // Water Spout on Top
    val spout = Path().apply {
        moveTo(w * 0.50f, h * 0.16f)
        quadraticTo(w * 0.44f, h * 0.04f, w * 0.38f, h * 0.06f)
        moveTo(w * 0.50f, h * 0.16f)
        quadraticTo(w * 0.56f, h * 0.04f, w * 0.62f, h * 0.06f)
    }
    drawPath(spout, Color(0xFF38BDF8), style = Stroke(width = 4f, cap = StrokeCap.Round))

    // W Shape
    val wPath = Path().apply {
        moveTo(w * 0.16f, h * 0.22f)
        lineTo(w * 0.32f, h * 0.22f)
        lineTo(w * 0.44f, h * 0.64f)
        lineTo(w * 0.56f, h * 0.22f)
        lineTo(w * 0.68f, h * 0.22f)
        lineTo(w * 0.80f, h * 0.64f)
        lineTo(w * 0.92f, h * 0.22f)
        lineTo(w * 0.84f, h * 0.86f)
        lineTo(w * 0.70f, h * 0.86f)
        lineTo(w * 0.56f, h * 0.46f)
        lineTo(w * 0.42f, h * 0.86f)
        lineTo(w * 0.28f, h * 0.86f)
        close()
    }
    drawPath(wPath, bodyBrush)
    drawPath(wPath, specularBrush)

    // Face in Center
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.44f, h * 0.38f),
        eyeRight = Offset(w * 0.56f, h * 0.38f),
        mouthCenter = Offset(w * 0.50f, h * 0.45f),
        eyeRadius = w * 0.035f
    )
}

// X -> Xylophone (Rainbow chime bars with mallets formed into X)
private fun DrawScope.draw3DXylophone(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    val diag1 = Path().apply {
        moveTo(w * 0.22f, h * 0.18f)
        lineTo(w * 0.38f, h * 0.18f)
        lineTo(w * 0.78f, h * 0.86f)
        lineTo(w * 0.62f, h * 0.86f)
        close()
    }
    drawPath(diag1, bodyBrush)

    val diag2 = Path().apply {
        moveTo(w * 0.62f, h * 0.18f)
        lineTo(w * 0.78f, h * 0.18f)
        lineTo(w * 0.38f, h * 0.86f)
        lineTo(w * 0.22f, h * 0.86f)
        close()
    }
    drawPath(diag2, bodyBrush)

    // Rainbow Keys
    drawRoundRect(Color(0xFFEF4444), topLeft = Offset(w * 0.36f, h * 0.28f), size = Size(w * 0.28f, h * 0.08f), cornerRadius = CornerRadius(6f, 6f))
    drawRoundRect(Color(0xFFFBBF24), topLeft = Offset(w * 0.38f, h * 0.46f), size = Size(w * 0.24f, h * 0.08f), cornerRadius = CornerRadius(6f, 6f))
    drawRoundRect(Color(0xFF22C55E), topLeft = Offset(w * 0.36f, h * 0.64f), size = Size(w * 0.28f, h * 0.08f), cornerRadius = CornerRadius(6f, 6f))

    // Face
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.42f, h * 0.40f),
        eyeRight = Offset(w * 0.58f, h * 0.40f),
        mouthCenter = Offset(w * 0.50f, h * 0.48f),
        eyeRadius = w * 0.035f
    )
}

// Y -> Yak (Horned shaggy brown yak formed into Y)
private fun DrawScope.draw3DYak(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    // Yak Horns
    val leftHorn = Path().apply {
        moveTo(w * 0.28f, h * 0.22f)
        quadraticTo(w * 0.12f, h * 0.08f, w * 0.20f, h * 0.04f)
    }
    drawPath(leftHorn, Color(0xFFFDE68A), style = Stroke(width = 8f, cap = StrokeCap.Round))

    val rightHorn = Path().apply {
        moveTo(w * 0.72f, h * 0.22f)
        quadraticTo(w * 0.88f, h * 0.08f, w * 0.80f, h * 0.04f)
    }
    drawPath(rightHorn, Color(0xFFFDE68A), style = Stroke(width = 8f, cap = StrokeCap.Round))

    // Y Shape
    val yPath = Path().apply {
        moveTo(w * 0.24f, h * 0.18f)
        lineTo(w * 0.40f, h * 0.18f)
        lineTo(w * 0.50f, h * 0.48f)
        lineTo(w * 0.60f, h * 0.18f)
        lineTo(w * 0.76f, h * 0.18f)
        lineTo(w * 0.58f, h * 0.56f)
        lineTo(w * 0.58f, h * 0.88f)
        lineTo(w * 0.42f, h * 0.88f)
        lineTo(w * 0.42f, h * 0.56f)
        close()
    }
    drawPath(yPath, bodyBrush)
    drawPath(yPath, specularBrush)

    // Snout & Face
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.42f, h * 0.32f),
        eyeRight = Offset(w * 0.58f, h * 0.32f),
        mouthCenter = Offset(w * 0.50f, h * 0.40f),
        eyeRadius = w * 0.035f
    )
}

// Z -> Zebra (Black and white striped zebra cub formed into Z)
private fun DrawScope.draw3DZebra(w: Float, h: Float, bodyBrush: Brush, specularBrush: Brush) {
    val zPath = Path().apply {
        moveTo(w * 0.22f, h * 0.18f)
        lineTo(w * 0.78f, h * 0.18f)
        lineTo(w * 0.78f, h * 0.32f)
        lineTo(w * 0.42f, h * 0.72f)
        lineTo(w * 0.78f, h * 0.72f)
        lineTo(w * 0.78f, h * 0.88f)
        lineTo(w * 0.22f, h * 0.88f)
        lineTo(w * 0.22f, h * 0.74f)
        lineTo(w * 0.58f, h * 0.34f)
        lineTo(w * 0.22f, h * 0.34f)
        close()
    }
    drawPath(zPath, Color.White)
    drawPath(zPath, Color(0xFF1E293B), style = Stroke(width = 3f))

    // Black Zebra Stripes
    drawRoundRect(Color(0xFF1E293B), topLeft = Offset(w * 0.35f, h * 0.22f), size = Size(w * 0.10f, h * 0.08f), cornerRadius = CornerRadius(4f, 4f))
    drawRoundRect(Color(0xFF1E293B), topLeft = Offset(w * 0.55f, h * 0.22f), size = Size(w * 0.10f, h * 0.08f), cornerRadius = CornerRadius(4f, 4f))
    drawRoundRect(Color(0xFF1E293B), topLeft = Offset(w * 0.44f, h * 0.50f), size = Size(w * 0.14f, h * 0.07f), cornerRadius = CornerRadius(4f, 4f))
    drawRoundRect(Color(0xFF1E293B), topLeft = Offset(w * 0.35f, h * 0.76f), size = Size(w * 0.12f, h * 0.08f), cornerRadius = CornerRadius(4f, 4f))

    // Face on Top Bar
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.62f, h * 0.25f),
        eyeRight = Offset(w * 0.72f, h * 0.25f),
        mouthCenter = Offset(w * 0.67f, h * 0.31f),
        eyeRadius = w * 0.035f
    )
}

// Fallback generic 3D letter
private fun DrawScope.drawGenericLetter(w: Float, h: Float, character: LetterCharacter, bodyBrush: Brush, specularBrush: Brush) {
    drawCircle(bodyBrush, radius = w * 0.38f, center = Offset(w * 0.5f, h * 0.5f))
    drawCircle(specularBrush, radius = w * 0.36f, center = Offset(w * 0.5f, h * 0.5f))
    drawCuteEyesAndMouth(
        eyeLeft = Offset(w * 0.40f, h * 0.40f),
        eyeRight = Offset(w * 0.60f, h * 0.40f),
        mouthCenter = Offset(w * 0.50f, h * 0.55f),
        eyeRadius = w * 0.04f
    )
}
