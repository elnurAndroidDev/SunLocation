package com.isaevapps.presentation.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
data class Gradients(
    val background: Brush,
    val primary: Brush,
    val accent: Brush
)

val LocalGradients = staticCompositionLocalOf<Gradients> {
    error("No Gradients provided")
}

val DarkBackgroundGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF0D1B2A),
        Color(0xFF1B263B),
        Color(0xFF243B55),
        Color(0xFF141E30)
    ),
    start = Offset(0f, 0f),
    end = Offset(1000f, 1500f)
)

val DarkAccentGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF1B263B),
        Color(0xFF243B55)
    )
)

val DarkPrimaryGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF141E30),
        Color(0xFF243B55)
    )
)

val LightBackgroundGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF9BD4F5), // мягкий голубой
        Color(0xFFA2D4E6), // бирюзовый пастель
        Color(0xFF9AC2D5), // серо-голубой
        Color(0xFFBFD9E3)  // светлый ледяной оттенок
    ),
    start = Offset(0f, 0f),
    end = Offset(1000f, 1500f)
)

val LightAccentGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF78C6E8), // бирюзовый пастель
        Color(0xFF99BFDE)  // мягкий голубой
    )
)

val LightPrimaryGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF8DC4D7), // серо-голубой
        Color(0xFFB9D3DA)  // пастельный бирюзовый
    )
)