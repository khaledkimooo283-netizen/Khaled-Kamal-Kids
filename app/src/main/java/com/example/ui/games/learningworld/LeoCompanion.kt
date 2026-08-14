package com.example.ui.games.learningworld

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

enum class LeoPose {
    IDLE,
    HAPPY,
    EXCITED,
    THINKING,
    GENTLE_CORRECTION,
    CELEBRATING,
    POINTING,
    TALKING
}

@Composable
fun LeoCompanion(
    modifier: Modifier = Modifier,
    pose: LeoPose = LeoPose.IDLE,
    speechText: String = "",
    onLeoClick: () -> Unit = {},
    onSpeakClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "leoMotion")

    // Smooth breathing bounce
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = when (pose) {
            LeoPose.EXCITED, LeoPose.CELEBRATING -> -12f
            LeoPose.TALKING -> -6f
            else -> -4f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (pose) {
                    LeoPose.CELEBRATING, LeoPose.EXCITED -> 350
                    LeoPose.TALKING -> 500
                    else -> 1200
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "leoBounce"
    )

    // Gentle head tilt / sway
    val headRotation by infiniteTransition.animateFloat(
        initialValue = when (pose) {
            LeoPose.THINKING -> -8f
            LeoPose.POINTING -> 5f
            else -> -2f
        },
        targetValue = when (pose) {
            LeoPose.THINKING -> 8f
            LeoPose.CELEBRATING -> 6f
            LeoPose.POINTING -> 7f
            else -> 2f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (pose == LeoPose.CELEBRATING) 400 else 1400,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "leoRotation"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Speech Bubble
        AnimatedVisibility(
            visible = speechText.isNotEmpty(),
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200))
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFFFFFFF), Color(0xFFF0FDF4))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(
                            colors = when (pose) {
                                LeoPose.CELEBRATING -> listOf(Color(0xFF10B981), Color(0xFFF59E0B))
                                LeoPose.GENTLE_CORRECTION -> listOf(Color(0xFFF59E0B), Color(0xFF3B82F6))
                                else -> listOf(Color(0xFF38BDF8), Color(0xFF818CF8))
                            }
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onSpeakClick()
                    }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = speechText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0F2FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speak Prompt",
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Leo Mascot 3D Cartoon Avatar Container
        Box(
            modifier = Modifier
                .offset(y = bounceOffset.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onLeoClick()
                },
            contentAlignment = Alignment.Center
        ) {
            // Soft Radial Glow Shadow Behind Leo
            Canvas(modifier = Modifier.size(105.dp)) {
                drawCircle(
                    color = when (pose) {
                        LeoPose.CELEBRATING -> Color(0xFFFFD54F).copy(alpha = 0.5f)
                        LeoPose.EXCITED -> Color(0xFFFDE68A).copy(alpha = 0.4f)
                        else -> Color(0xFFE2E8F0).copy(alpha = 0.6f)
                    },
                    radius = size.minDimension / 2f
                )
            }

            // Outer 3D Golden Fur Ring
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .shadow(elevation = 8.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFE082),
                                Color(0xFFFFB300),
                                Color(0xFFF57C00)
                            )
                        )
                    )
                    .border(
                        width = 3.5.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFFFF9C4), Color(0xFFFF8F00))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.kk_kids_lion),
                    contentDescription = "Leo the Lion Companion",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp)
                        .clip(CircleShape)
                )

                // Expression Overlay Badge
                when (pose) {
                    LeoPose.CELEBRATING -> {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-2).dp)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFD700))
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⭐", fontSize = 15.sp)
                        }
                    }
                    LeoPose.THINKING -> {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-2).dp)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8))
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("❓", fontSize = 14.sp)
                        }
                    }
                    LeoPose.TALKING -> {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 2.dp, y = 2.dp)
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💬", fontSize = 13.sp)
                        }
                    }
                    LeoPose.GENTLE_CORRECTION -> {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-2).dp)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF97316))
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💡", fontSize = 14.sp)
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}
