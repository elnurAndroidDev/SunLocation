package com.isaevapps.presentation.utils

import com.isaevapps.domain.result.CoordinatesError
import com.isaevapps.presentation.R

fun CoordinatesError.toUiText(): UiText {
    return when (this) {
        CoordinatesError.TWO_COORDINATES -> UiText.StringResource(R.string.two_values_error)
        CoordinatesError.INVALID_LATITUDE -> UiText.StringResource(R.string.invalid_lat)
        CoordinatesError.INVALID_LONGITUDE -> UiText.StringResource(R.string.invalid_lon)
    }
}