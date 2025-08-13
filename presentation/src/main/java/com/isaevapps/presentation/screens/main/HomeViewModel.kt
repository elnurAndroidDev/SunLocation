package com.isaevapps.presentation.screens.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaevapps.domain.model.Location
import com.isaevapps.domain.repository.TimeZoneRepository
import com.isaevapps.domain.usecase.CalculateSunPositionUseCase
import com.isaevapps.domain.usecase.GetCurrentLocationUseCase
import com.isaevapps.domain.usecase.GetCurrentWeatherUseCase
import com.isayevapps.domain.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.round
import kotlin.math.roundToLong

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val calculateSunPositionUseCase: CalculateSunPositionUseCase,
    timeZoneRepository: TimeZoneRepository
) : ViewModel() {

    private val systemUTC = timeZoneRepository.getSystemUtc().getUTCDouble()
    private var preciseLocation: Location? = null

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        collectLocation()
    }

    fun collectLocation() {
        viewModelScope.launch {
            getCurrentLocationUseCase()
                .onEach {
                    val lat = it.lat
                    val lon = it.lon
                    preciseLocation = Location(lat, lon)
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
                .map {
                    Location(
                        lat = (it.lat * 1000).roundToLong() / 1000.0,
                        lon = (it.lon * 1000).roundToLong() / 1000.0
                    )
                }
                .distinctUntilChanged()
                .collect {
                    preciseLocation?.let {
                        val weatherResult = getCurrentWeatherUseCase(it.lat, it.lon)
                        if (weatherResult is Result.Success) {
                            val weather = weatherResult.data
                            _uiState.update { state ->
                                state.copy(
                                    city = weather.city,
                                    temp = "${weather.temp}°",
                                    condition = weather.condition
                                )
                            }
                        }
                        if (weatherResult is Result.Error) {
                            Log.d("HomeViewModel", "Error: ${weatherResult.error}")
                        }
                    }
                }
        }
    }
}