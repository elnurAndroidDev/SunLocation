package com.isaevapps.presentation.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color


val Dark = Color(0xFF0D1B2A)
val DarkAccent = Color(0xFF243B55)
val DarkPrimary = Color(0xFF141E30)


@Immutable
data class AppColors(
    val onBackground: Color,
    val onSurface: Color,
    val primary: Color
)

val LocalAppColors = staticCompositionLocalOf<AppColors> {
    error("No colors provided")
}