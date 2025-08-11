package com.isaevapps.presentation.screens.home

import com.isaevapps.presentation.utils.UiText


data class HomeUiState(
    val coordinates: String = "",
    val azimuth: String = "",
    val altitude: String = "",
    val invalidCoordinates: UiText? = null,
    val timeZones: List<String> = emptyList(),
    val date: String = "",
    val time: String = "",
    val timeZone: String = "",
    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false,
)
