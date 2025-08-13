package com.isaevapps.domain.utils

import com.isaevapps.domain.model.Coordinates
import com.isaevapps.domain.result.CoordinatesError
import com.isayevapps.domain.result.Result
import kotlin.Result as KotlinResult

interface CoordinatesParser {
    fun parse(input: String): Result<Coordinates, CoordinatesError>
}

class DefaultCoordinatesParser : CoordinatesParser {
    override fun parse(input: String): Result<Coordinates, CoordinatesError> {
        if (input.isBlank()) {
            return Result.Error(CoordinatesError.TWO_COORDINATES)
        }
        val parts = input.split(",", " ").filter { it.isNotBlank() }
        if (parts.size != 2) {
            return Result.Error(CoordinatesError.TWO_COORDINATES)
        }
        val latResult = parseCoordinate(parts[0], true)
        val lonResult = parseCoordinate(parts[1], false)
        if (latResult.isFailure) return Result.Error(CoordinatesError.INVALID_LATITUDE)
        if (lonResult.isFailure) return Result.Error(CoordinatesError.INVALID_LONGITUDE)

        val latitude = latResult.getOrThrow()
        val longitude = lonResult.getOrThrow()

        if (latitude !in -90.0..90.0) {
            return Result.Error(CoordinatesError.INVALID_LATITUDE)
        }
        if (longitude !in -180.0..180.0) {
            return Result.Error(CoordinatesError.INVALID_LONGITUDE)
        }
        return Result.Success(Coordinates(latitude, longitude))
    }

    fun parseCoordinate(
        part: String, isLatitude: Boolean
    ): KotlinResult<Double> {
        return try {
            // Попробовать числовой формат (например, "55.7558")
            val number = part.toDoubleOrNull()
            if (number != null) {
                return KotlinResult.success(number)
            }
            // Попробовать угловой формат (например, "55°45'0"N")
            val regex = Regex("""^(\d+)°(\d+)?'(\d+(?:\.\d+)?)?"([NSEW])$""")
            val match = regex.matchEntire(part)
                ?: return KotlinResult.failure(IllegalArgumentException("Invalid coordinate format: $part"))

            val (degrees, minutes, seconds, direction) = match.destructured
            val deg = degrees.toDouble()
            val min = minutes.takeIf { it.isNotEmpty() }?.toDouble() ?: 0.0
            val sec = seconds.takeIf { it.isNotEmpty() }?.toDouble() ?: 0.0

            var decimal = deg + min / 60.0 + sec / 3600.0
            if ((isLatitude && direction == "S") || (!isLatitude && direction == "W")) {
                decimal = -decimal
            }
            KotlinResult.success(decimal)
        } catch (_: Exception) {
            KotlinResult.failure(IllegalArgumentException("Failed to parse coordinate: $part"))
        }
    }
}