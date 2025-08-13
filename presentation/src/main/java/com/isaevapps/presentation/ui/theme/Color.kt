package com.isaevapps.presentation.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val PrimaryTextColor = Color(0xFFFFFFFF)
val SecondaryTextColor = Color(0xFF999999)

val BackgroundGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF0D1B2A),
        Color(0xFF1B263B),
        Color(0xFF5C7AEA),
        Color(0xFFFAC86B)
    ),
    start = Offset(0f, 0f),
    end = Offset(1000f, 1500f)
)

val ButtonGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF5C7AEA), // голубой из фона
        Color(0xFFFAC86B)  // жёлто-оранжевый акцент
    )
)