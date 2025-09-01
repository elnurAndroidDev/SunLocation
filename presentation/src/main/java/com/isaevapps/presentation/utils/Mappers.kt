package com.isaevapps.presentation.utils

import com.isaevapps.domain.model.Weather
import com.isaevapps.presentation.screens.main.models.WeatherUiData

fun Weather.toUiData(): WeatherUiData {
    return WeatherUiData(
        city = city,
        temp = "${temp}°C",
        condition = condition
    )
}