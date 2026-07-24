package com.railwaypingtester.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.railwaypingtester.ui.theme.BlueAccent

@Composable
fun PulseAnimation(
    isActive: Boolean,
    color: Color = BlueAccent,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    if (isActive) {
        Box(
            modifier = modifier
                .size((40 * scale).dp)
                .clip(CircleShape)
                .graphicsLayer(alpha = alpha)
                .background(color.copy(alpha = 0.4f), CircleShape)
        )
    }
}

@Composable
fun LiveProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp)),
        color = BlueAccent,
        trackColor = Color(0xFF2D333B),
        strokeCap = StrokeCap.Round,
    )
}

@Composable
fun ScanningText(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")

    val dots by infiniteTransition.animateInt(
        initialValue = 0,
        targetValue = 4,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "dots"
    )

    val dotsText = ".".repeat(dots)

    Text(
        text = "Scanning$dotsText",
        style = TextStyle(
            color = BlueAccent,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace
        ),
        modifier = modifier
    )
}