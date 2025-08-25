package com.isaevapps.domain.utils

import com.isaevapps.domain.model.Coordinates
import com.isaevapps.domain.result.CoordinatesError
import com.isaevapps.domain.result.Result

interface CoordinatesParser {
    fun parse(input: String): Result<Coordinates, CoordinatesError>
}