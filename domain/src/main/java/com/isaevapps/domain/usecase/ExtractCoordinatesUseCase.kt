package com.isaevapps.domain.usecase

import com.isaevapps.domain.model.Coordinates
import com.isaevapps.domain.result.CoordinatesError
import com.isaevapps.domain.utils.CoordinatesParser
import com.isaevapps.domain.result.Result

class ExtractCoordinatesUseCase(
    private val coordinatesParser: CoordinatesParser
) {
    operator fun invoke(input: String): Result<Coordinates, CoordinatesError> {
        return coordinatesParser.parse(input)
    }
}