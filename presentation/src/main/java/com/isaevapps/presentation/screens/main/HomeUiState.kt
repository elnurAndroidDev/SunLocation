package com.isaevapps.presentation.screens.main

data class HomeUiState(
    val city: String = "",
    val coords: String = "",
    val temp: String = "",
    val condition: String = "",
    val azimuth: String = "",
    val altitude: String = "",
    val updated: String = ""
)
