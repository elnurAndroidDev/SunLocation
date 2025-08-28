package com.isaevapps.data.repository

import com.isaevapps.domain.model.TimeZone
import com.isaevapps.domain.repository.TimeZoneRepository
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs
import java.util.TimeZone as SystemTimeZome

class ResourceTimeZoneRepository @Inject constructor() : TimeZoneRepository {
    override val timeZones = utcList.map { TimeZone(it) }

    override fun getSystemUtc(): TimeZone {
        val timeZone = SystemTimeZome.getDefault()
            val offsetInMillis = timeZone.rawOffset
            val hours = offsetInMillis / (1000 * 60 * 60)
            val minutes = (offsetInMillis / (1000 * 60)) % 60
            val systemUTC = String.format(Locale.getDefault(), "UTC%+03d:%02d", hours, abs(minutes))
            val systemOffset = systemUTC.take(9) // "UTC±HH:MM"
            return timeZones.firstOrNull { it.name.startsWith(systemOffset) } ?: timeZones[0]
    }

    override fun getTimeZoneByDouble(utcOffset: Double): TimeZone {
        val timeZone = timeZones.firstOrNull { it.getUTCDouble() == utcOffset }
        return timeZone ?: timeZones[0]
    }

    companion object {
        val utcList = listOf(
            "UTC-12:00",
            "UTC-11:00 Midway Islands",
            "UTC-10:00 Honolulu, Hawaii",
            "UTC-09:30 Marquesas Islands",
            "UTC-09:00 Anchorage, Alaska",
            "UTC-08:00 Los Angeles",
            "UTC-07:00 Denver, Phoenix",
            "UTC-07:00 Arizona",
            "UTC-06:00 Costa Rica, Mexico City",
            "UTC-06:00 Regina",
            "UTC-05:00 Bogotá, New York",
            "UTC-04:00 Asunción, Caracas",
            "UTC-04:00 Santiago",
            "UTC-03:30 St. John's",
            "UTC-03:30 Newfoundland",
            "UTC-03:00 Brasília",
            "UTC-03:00 Buenos Aires",
            "UTC-03:00 Greenland",
            "UTC-03:00 Montevideo",
            "UTC-02:00 South Georgia",
            "UTC-01:00 Cape Verde, Azores",
            "UTC+00:00 London, Lisbon",
            "UTC+00:00 Reykjavik",
            "UTC+01:00 Amsterdam, Berlin",
            "UTC+01:00 Vienna, Rome, Bern",
            "UTC+01:00 Belgrade, Budapest",
            "UTC+01:00 Madrid, Paris",
            "UTC+01:00 Warsaw, Zagreb",
            "UTC+02:00 Amman",
            "UTC+02:00 Athens, Bucharest",
            "UTC+02:00 Beirut",
            "UTC+02:00 Kyiv, Riga, Sofia",
            "UTC+02:00 Jerusalem",
            "UTC+02:00 Cairo",
            "UTC+02:00 Tripoli",
            "UTC+02:00 Pretoria",
            "UTC+03:00 Baghdad",
            "UTC+03:00 Kuwait, Riyadh",
            "UTC+03:00 Minsk",
            "UTC+03:00 Moscow, Saint Petersburg",
            "UTC+03:00 Nairobi",
            "UTC+03:00 Istanbul",
            "UTC+03:30 Tehran",
            "UTC+04:00 Abu Dhabi",
            "UTC+04:00 Astrakhan, Ulyanovsk",
            "UTC+04:00 Baku, Yerevan",
            "UTC+04:00 Tbilisi",
            "UTC+04:30 Kabul",
            "UTC+05:00 Ashgabat, Tashkent",
            "UTC+05:00 Yekaterinburg",
            "UTC+05:00 Islamabad, Karachi",
            "UTC+05:30 Mumbai, New Delhi",
            "UTC+05:45 Kathmandu",
            "UTC+06:00 Nur-Sultan (Astana)",
            "UTC+06:00 Dhaka",
            "UTC+06:00 Omsk",
            "UTC+06:30 Yangon",
            "UTC+07:00 Bangkok, Jakarta, Hanoi",
            "UTC+07:00 Novosibirsk, Tomsk",
            "UTC+08:00 Hong Kong, Beijing",
            "UTC+08:00 Kuala Lumpur, Singapore",
            "UTC+08:00 Ulaanbaatar",
            "UTC+08:45 Eucla",
            "UTC+09:00 Osaka, Tokyo",
            "UTC+09:00 Pyongyang, Seoul",
            "UTC+09:00 Chita, Yakutsk",
            "UTC+09:30 Adelaide, Darwin",
            "UTC+10:00 Vladivostok",
            "UTC+10:00 Canberra, Sydney",
            "UTC+10:30 Lord Howe",
            "UTC+11:00 Magadan, Sakhalin",
            "UTC+11:00 Solomon Islands",
            "UTC+12:00 Wellington, Auckland",
            "UTC+12:00 Fiji",
            "UTC+12:45 Chatham",
            "UTC+13:00 Nukuʻalofa, Samoa",
            "UTC+14:00 Kiritimati Island"
        )
    }
}