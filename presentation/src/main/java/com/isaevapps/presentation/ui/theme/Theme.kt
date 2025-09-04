package com.isaevapps.presentation.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun SunLocationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val appColors = if (darkTheme) darkColorScheme else lightColorScheme
    val appGradients = if (darkTheme) darkGradients else lightGradients
    CompositionLocalProvider(
        LocalGradients provides appGradients,
        LocalAppColors provides appColors,
        LocalAppTypography provides appTypography
    ) {
        MaterialTheme(
            content = {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(appGradients.background),
                    color = Color.Transparent,
                    content = content
                )
            }
        )
    }
}

val MaterialTheme.gradients: Gradients
    @Composable
    get() = LocalGradients.current

val MaterialTheme.appColors: AppColors
    @Composable
    get() = LocalAppColors.current

val MaterialTheme.appTypography: AppTypography
    @Composable
    get() = LocalAppTypography.current