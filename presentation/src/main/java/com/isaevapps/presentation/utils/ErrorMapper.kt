package com.isaevapps.presentation.utils

import com.isaevapps.domain.result.CoordinatesError
import com.isaevapps.domain.result.LocationError
import com.isaevapps.domain.result.NetworkError
import com.isaevapps.presentation.R
import com.isaevapps.presentation.utils.UiText.StringResource

fun CoordinatesError.toUiText(): UiText {
    return when (this) {
        CoordinatesError.TWO_COORDINATES -> UiText.StringResource(R.string.two_values_error)
        CoordinatesError.INVALID_LATITUDE -> UiText.StringResource(R.string.invalid_lat)
        CoordinatesError.INVALID_LONGITUDE -> UiText.StringResource(R.string.invalid_lon)
    }
}

fun NetworkError.toUiText(): UiText {
    return when (this) {
        NetworkError.NO_INTERNET -> UiText.StringResource(R.string.no_internet)
        NetworkError.SERVER_ERROR -> UiText.StringResource(R.string.server_error)
        NetworkError.TIMEOUT -> UiText.StringResource(R.string.timeout)
        NetworkError.UNKNOWN -> UiText.StringResource(R.string.unknown_error)
    }
}

fun LocationError.toUiText(): UiText {
    return when (this) {
        LocationError.NOT_AVAILABLE -> StringResource(R.string.no_location)
    }

}