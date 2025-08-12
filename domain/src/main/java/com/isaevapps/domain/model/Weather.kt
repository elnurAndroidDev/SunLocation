package com.isaevapps.domain.model

import java.time.LocalDateTime

data class Weather(
    val city: String,
    val country: String,
    val temp: Double,
    val condition: String,
    val localtime: LocalDateTime,
    val lastUpdated: LocalDateTime
)
