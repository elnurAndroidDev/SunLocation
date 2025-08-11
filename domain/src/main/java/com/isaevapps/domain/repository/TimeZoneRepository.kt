package com.isaevapps.domain.repository

import com.isaevapps.domain.model.TimeZone

interface TimeZoneRepository {
    val timeZones: List<TimeZone>
    fun getSystemUtc(): TimeZone
    fun getTimeZoneByDouble(utcOffset: Double): TimeZone
}