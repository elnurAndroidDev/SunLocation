package com.isaevapps.presentation.screens.main

import com.isaevapps.presentation.screens.main.models.WeatherUiData
import com.isaevapps.presentation.utils.UiText

data class HomeUiState(
    val weather: WeatherUiState = WeatherUiState(),
    val sun: SunUiState = SunUiState(),
    val compass: CompassUiState = CompassUiState()
)

data class WeatherUiState(
    val weatherUiData: WeatherUiData = WeatherUiData(),
    val isLoading: Boolean = true,
    val error: UiText? = null
)

data class SunUiState(
    val coordinates: String = "-",
    val azimuth: String = "-°",
    val altitude: String = "-°",
    val error: UiText? = null
)

data class CompassUiState(
    val azimuth: Int = 0,
    val rotation: Int = 0
)
