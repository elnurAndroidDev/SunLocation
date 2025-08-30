package com.isaevapps.presentation.screens.main

import com.isaevapps.presentation.utils.UiText

data class HomeUiState(
    val weather: WeatherUiState = WeatherUiState(isLoading = true),
    val sun: SunUiState = SunUiState(),
    val compass: CompassUiState = CompassUiState()
)

data class WeatherUiState(
    val city: String = "-",
    val temp: String = "-°C",
    val condition: String = "-",
    val isLoading: Boolean = false,
    val error: UiText? = null
)

data class SunUiState(
    val coordinates: String = "",
    val azimuth: String = "",
    val altitude: String = "",
    val error: UiText? = null
)

data class CompassUiState(
    val azimuth: Int = 0,
    val rotation: Int = 0
)
