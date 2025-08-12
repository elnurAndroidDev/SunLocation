package com.isaevapps.domain.usecase

import com.isaevapps.domain.repository.WeatherRepository

class GetCurrentWeatherUseCase(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(lat: Double, lon: Double) = weatherRepository.getWeather(lat, lon)
}