package com.explorova.cardiocareful.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text

/**
 * Animated heart rate display with pulsing effect.
 */
@Composable
fun AnimatedHeartRateDisplay(
    heartRate: Double,
    modifier: Modifier = Modifier,
    targetColor: Color = Color.Red,
    isAlertActive: Boolean = false
) {
    val scale by animateFloatAsState(
        targetValue = if (isAlertActive) 1.1f else 1.0f,
        animationSpec = tween(durationMillis = 500)
    )

    val color by animateColorAsState(
        targetValue = if (isAlertActive) Color.Red else targetColor,
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = modifier
            .scale(scale)
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = heartRate.toInt().toString(),
            color = color,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}

/**
 * Pulsing indicator for alert state.
 */
@Composable
fun PulsingIndicator(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    inactiveColor: Color = Color.Gray,
    activeColor: Color = Color.Red
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.2f else 0.8f,
        animationSpec = tween(durationMillis = 600)
    )

    val alpha by animateFloatAsState(
        targetValue = if (isActive) 0.7f else 0.3f,
        animationSpec = tween(durationMillis = 600)
    )

    val color by animateColorAsState(
        targetValue = if (isActive) activeColor else inactiveColor,
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = modifier
            .size(16.dp)
            .scale(scale)
            .alpha(alpha)
            .background(color, CircleShape)
    )
}

/**
 * Smooth transition between screen states.
 */
@Composable
fun FadeInAnimation(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    Box(modifier = Modifier.alpha(alpha)) {
        content()
    }
}

/**
 * Animated cooldown progress indicator.
 */
@Composable
fun CooldownProgressIndicator(
    remainingSeconds: Int,
    totalSeconds: Int = 30,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        targetValue = remainingSeconds.toFloat() / totalSeconds.toFloat(),
        animationSpec = tween(durationMillis = 500)
    )

    val color = when {
        remainingSeconds > totalSeconds / 2 -> Color.Yellow
        remainingSeconds > totalSeconds / 4 -> Color.Yellow
        else -> Color.Green
    }

    Box(
        modifier = modifier
            .size(40.dp)
            .background(Color.DarkGray, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Circular progress
        Text(
            text = remainingSeconds.toString(),
            color = color
        )
    }
}

/**
 * Alert notification animation.
 */
@Composable
fun AlertNotificationAnimation(
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 400)
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 400)
    )

    Box(
        modifier = modifier
            .size(60.dp)
            .scale(scale)
            .alpha(alpha)
            .background(Color.Red.copy(alpha = 0.7f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Alert",
            tint = Color.White,
            modifier = Modifier.size(40.dp)
        )
    }
}

/**
 * Number transition animation for smooth HR value changes.
 */
@Composable
fun AnimatedNumber(
    value: Int,
    modifier: Modifier = Modifier
) {
    val animatedValue by animateFloatAsState(
        targetValue = value.toFloat(),
        animationSpec = tween(durationMillis = 300)
    )

    Text(
        text = animatedValue.toInt().toString(),
        modifier = modifier
    )
}
