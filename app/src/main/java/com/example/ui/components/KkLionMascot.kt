package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

enum class MascotState {
    IDLE,
    HAPPY,
    CELEBRATE,
    THINKING
}

@Composable
fun KkLionMascot(
    modifier: Modifier = Modifier,
    state: MascotState = MascotState.IDLE,
    speechBubbleText: String? = null,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "lionBounce")
    val bounceScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (state == MascotState.CELEBRATE) 1.15f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (state == MascotState.CELEBRATE) 400 else 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Row(
        modifier = modifier
            .padding(8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .scale(bounceScale)
                .clip(CircleShape)
                .background(Color(0xFFFFD54F))
                .border(3.dp, Color(0xFFFF9800), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.kk_kids_lion),
                contentDescription = "KK Lion Mascot",
                modifier = Modifier.fillMaxSize()
            )
        }

        if (!speechBubbleText.isNullOrEmpty()) {
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xFF42A5F5), shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = speechBubbleText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E88E5),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}
