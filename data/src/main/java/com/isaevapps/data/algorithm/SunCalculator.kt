package com.isaevapps.data.algorithm

import com.isaevapps.domain.model.SunPosition
import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import java.time.LocalDateTime
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

object SunCalculator {
    fun calculateSunPosition(
        latitude: Double,
        longitude: Double,
        dateTime: LocalDateTime,
        utcOffset: Double
    ): SunPosition {
        val year = dateTime.year
        val month = dateTime.monthValue
        val day = dateTime.dayOfMonth
        val hour = dateTime.hour
        val minute = dateTime.minute
        val d: Int =
            367 * year - (7 * (year + ((month + 9) / 12))) / 4 + (275 * month) / 9 + day - 730530 //преобразование даты в нужное числовое значение
        val w = rev(282.9404 + 4.70935E-5 * d) //долгота перигелия
        val e = 0.016709 - 1.151E-9 * d //эксцентриситет
        val M = rev(356.0470 + 0.9856002585 * d) //средняя аномалия
        val oblecl = rev(23.4393 - 3.563E-7 * d) //наклон эклиптики
        val L = rev(w + M) //средняя долгота Солнца
        val E = M + (180 / PI) * e * sin(toRadians(M)) * (1 + e * cos(toRadians(M))) //эксцентрическая аномалия
        var x = cos(toRadians(E)) - e
        var y = sin(toRadians(E)) * sqrt(1 - e * e)
        val r = sqrt(x * x + y * y) //расстояние
        val v = rev(toDegrees(atan2(y, x))) //истинная аномалия
        val lon = rev(v + w)
        x = r * cos(toRadians(lon))
        y = r * sin(toRadians(lon))
        var z = 0.0
        val xequat = x
        val yequat = y * cos(toRadians(oblecl)) - z * sin(toRadians(oblecl))
        val zequat = y * sin(toRadians(oblecl)) + z * cos(toRadians(oblecl))

        //r = sqrt(xequat * xequat + yequat * yequat + zequat * zequat);
        val RA = atan2(yequat, xequat)
        val Decl = atan2(zequat, sqrt(xequat * xequat + yequat * yequat))

        /**/////////////////Звездное время. Высота и азимут///////////////////// */
        var UT: Double = hour + minute / 60.0 - utcOffset //время в UT
        if (UT < 0) UT = 24 + UT
        if (UT > 24) UT = UT - 24
        if (UT == 24.0) UT = 0.0
        val GMST0 = L / 15 + 12 //звездное время на гринвичском меридиане в 00:00 прямо сейчас
        var SIDTIME: Double = GMST0 + UT + longitude / 15 //местное звездное время
        SIDTIME -= 24 * floor(SIDTIME / 24)
        val HA = SIDTIME * 15 - toDegrees(RA) //часовой угол
        x = cos(toRadians(HA)) * cos(Decl)
        y = sin(toRadians(HA)) * cos(Decl)
        z = sin(Decl)
        val xhor = x * sin(toRadians(latitude)) - z * cos(toRadians(latitude))
        val yhor = y
        val zhor = x * cos(toRadians(latitude)) + z * sin(toRadians(latitude))
        val azimuth = round((toDegrees(atan2(yhor, xhor)) + 180) * 100) / 100.0
        val altitude = round(toDegrees(asin(zhor)) * 100) / 100.0
        return SunPosition(azimuth, altitude)
    }

    private fun rev(num: Double): Double {
        return num - floor(num / 360.0) * 360.0
    }
}