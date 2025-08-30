package com.isaevapps.presentation.screens.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Modifier.shimmer(
    visible: Boolean = true,
    baseColor: Color = Color.Gray.copy(alpha = 0.3f),
    highlightColor: Color = Color.White.copy(alpha = 0.6f)
): Modifier = composed {
    if (!visible) return@composed this

    val transition = rememberInfiniteTransition()
    val xShimmer by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val brush = Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset(xShimmer - 200f, 0f),
        end = Offset(xShimmer, 0f)
    )

    drawWithContent {
        if (visible) {
            drawRect(brush = brush, size = size)
        } else {
            drawContent()
        }
    }
}