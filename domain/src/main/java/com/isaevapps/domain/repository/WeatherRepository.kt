package com.isaevapps.domain.repository

import com.isaevapps.domain.model.Weather
import com.isaevapps.domain.result.NetworkError
import com.isaevapps.domain.result.Result

interface WeatherRepository {
    suspend fun getWeather(lat: Double, lon: Double): Result<Weather, NetworkError>
}