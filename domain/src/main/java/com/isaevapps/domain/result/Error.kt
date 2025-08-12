package com.isaevapps.domain.result

sealed interface Error

enum class CoordinatesError : Error {
    TWO_COORDINATES,
    INVALID_LATITUDE,
    INVALID_LONGITUDE
}

enum class NetworkError : Error {
    NO_INTERNET,
    SERVER_ERROR,
    TIMEOUT,
    UNKNOWN
}