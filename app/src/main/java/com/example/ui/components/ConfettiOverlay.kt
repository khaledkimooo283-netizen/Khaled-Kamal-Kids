package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class ConfettiParticle(
    val x: Float,
    var y: Float,
    val size: Float,
    val speed: Float,
    val color: Color,
    val angle: Float
)

@Composable
fun ConfettiOverlay(
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    val particles = remember {
        val colors = listOf(
            Color(0xFFFF5252), Color(0xFFFFB74D), Color(0xFFFFD54F),
            Color(0xFF66BB6A), Color(0xFF42A5F5), Color(0xFFAB47BC)
        )
        List(60) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -0.5f,
                size = Random.nextFloat() * 20f + 10f,
                speed = Random.nextFloat() * 0.015f + 0.008f,
                color = colors.random(),
                angle = Random.nextFloat() * 360f
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "confetti")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        particles.forEach { p ->
            val currentY = ((p.y + progress * p.speed * 100) % 1.2f) * height
            val currentX = p.x * width
            drawRect(
                color = p.color,
                topLeft = Offset(currentX, currentY),
                size = Size(p.size, p.size * 0.6f)
            )
        }
    }
}
