package com.isaevapps.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFF213654),
    onPrimary = Color(0xFFFFFFFF)
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    onBackground = Color(0xFF0D1B2A),
    onSurface = Color(0xFF213654),
    onPrimary = Color(0xFF323C7A)
)

private val lightGradients = Gradients(
    background = LightBackgroundGradient,
    primary = LightPrimaryGradient,
    accent = LightAccentGradient
)

private val darkGradients = Gradients(
    background = DarkBackgroundGradient,
    primary = DarkPrimaryGradient,
    accent = DarkAccentGradient
)

@Composable
fun SunLocationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val gradients = if (darkTheme) darkGradients else lightGradients
    CompositionLocalProvider(LocalGradients provides gradients) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content =  {
                Surface(
                    modifier = Modifier.fillMaxSize().background(gradients.background),
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