package com.isaevapps.data.repository

import com.isaevapps.data.network.WeatherApi
import com.isaevapps.data.network.safeApiCall
import com.isaevapps.data.toDomain
import com.isaevapps.domain.model.Weather
import com.isaevapps.domain.repository.WeatherRepository
import com.isaevapps.domain.result.NetworkError
import com.isaevapps.domain.result.Result
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi
) : WeatherRepository {
    override suspend fun getWeather(
        lat: Double,
        lon: Double
    ): Result<Weather, NetworkError> {
        val latLon = "$lat,$lon"
        val response = safeApiCall(
            apiCall = { weatherApi.getWeather(latLon) },
            map = { it.toDomain() }
        )
        return response
    }
}