package com.isaevapps.presentation.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaevapps.domain.model.Location
import com.isaevapps.domain.repository.TimeZoneRepository
import com.isaevapps.domain.result.Result
import com.isaevapps.domain.usecase.CalculateSunPositionUseCase
import com.isaevapps.domain.usecase.GetCurrentLocationUseCase
import com.isaevapps.domain.usecase.GetCurrentWeatherUseCase
import com.isaevapps.domain.usecase.ObserveCompassUseCase
import com.isaevapps.presentation.utils.toUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.roundToLong

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val calculateSunPositionUseCase: CalculateSunPositionUseCase,
    private val observeCompassUseCase: ObserveCompassUseCase,
    timeZoneRepository: TimeZoneRepository
) : ViewModel() {

    private val systemUTC = timeZoneRepository.getSystemUtc().getUTCDouble()
    private var preciseLocation: Location? = null

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        collectLocation()
        collectCompass()
    }

    fun collectLocation() {
        viewModelScope.launch {
            getCurrentLocationUseCase()
                .onEach {
                    val lat = it.lat
                    val lon = it.lon
                    preciseLocation = Location(lat, lon)
                    updateSunPosition(lat, lon)
                }
                .map {
                    Location(
                        lat = (it.lat * 1000).roundToLong() / 1000.0,
                        lon = (it.lon * 1000).roundToLong() / 1000.0
                    )
                }
                .distinctUntilChanged()
                .collect {
                    preciseLocation?.let {
                        updateWeather(it.lat, it.lon)
                    }
                }
        }
    }

    private fun updateSunPosition(lat: Double, lon: Double) = viewModelScope.launch {
        val sunPosition = calculateSunPositionUseCase(
            latitude = lat,
            longitude = lon,
            dateTime = LocalDateTime.now(),
            utcOffset = systemUTC
        )
        _uiState.update { state ->
            state.copy(
                coords = "$lat, $lon",
                azimuth = "${sunPosition.azimuth}°",
                altitude = "${sunPosition.altitude}°"
            )
        }
    }

    private fun updateWeather(lat: Double, lon: Double) = viewModelScope.launch {
        val weatherResult = getCurrentWeatherUseCase(lat, lon)
        if (weatherResult is Result.Success) {
            val weather = weatherResult.data
            _uiState.update { state ->
                state.copy(
                    city = weather.city,
                    temp = "${weather.temp}°",
                    condition = weather.condition,
                    weatherError = null
                )
            }
        }
        if (weatherResult is Result.Error) {
            _uiState.update { state ->
                state.copy(
                    weatherError = weatherResult.error.toUiText()
                )
            }
        }
    }

    private fun collectCompass() = viewModelScope.launch {
        observeCompassUseCase().collect { compass ->
            _uiState.update {
                it.copy(compassAzimuth = compass.azimuth)
            }
        }
    }
}