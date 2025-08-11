package com.isaevapps.presentation.utils

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun LocalDate.toStringFormatted(): String {
    return this.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
}

fun String.toLocalDateOrNull(): LocalDate? {
    return try {
        LocalDate.parse(this, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    } catch (_: Exception) {
        null
    }
}

fun LocalTime.toStringFormatted(): String {
    return this.format(DateTimeFormatter.ofPattern("HH:mm"))
}

fun String.toLocalTimeOrNull(): LocalTime? {
    return try {
        LocalTime.parse(this, DateTimeFormatter.ofPattern("HH:mm"))
    } catch (_: Exception) {
        null
    }
}