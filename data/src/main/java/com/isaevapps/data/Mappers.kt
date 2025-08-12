package com.isaevapps.data

import com.isaevapps.data.network.WeatherDTO
import com.isaevapps.domain.model.Weather
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun WeatherDTO.toDomain(): Weather {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return Weather(
        city = location.name,
        country = location.country,
        temp = current.temp_c,
        condition = current.condition.text,
        localtime = LocalDateTime.parse(location.localtime, formatter),
        lastUpdated = LocalDateTime.parse(current.last_updated, formatter)
    )
}