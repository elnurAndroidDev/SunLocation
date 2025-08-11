package com.isaevapps.domain.result

sealed interface Error

enum class CoordinatesError : Error {
    TWO_COORDINATES,
    INVALID_LATITUDE,
    INVALID_LONGITUDE
}