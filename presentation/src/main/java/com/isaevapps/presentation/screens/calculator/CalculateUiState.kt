package com.isaevapps.presentation.screens.calculator

import com.isaevapps.presentation.utils.UiText


data class CalculateUiState(
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
