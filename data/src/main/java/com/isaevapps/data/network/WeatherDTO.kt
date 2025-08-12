package com.isaevapps.data.network

data class WeatherDTO(
    val location: LocationDTO,
    val current: CurrentDTO
)

data class LocationDTO(
    val name: String,
    val country: String,
    val localtime: String,
)

data class CurrentDTO(
    val last_updated: String,
    val temp_c: Double,
    val condition: ConditionDTO
)

data class ConditionDTO(
    val text: String
)