package com.isaevapps.presentation.screens.main

import com.isaevapps.presentation.utils.UiText

data class HomeUiState(
    val city: String = "",
    val coords: String = "",
    val temp: String = "",
    val condition: String = "",
    val azimuth: String = "",
    val altitude: String = "",
    val updated: String = "",
    val weatherError: UiText? = null
)
