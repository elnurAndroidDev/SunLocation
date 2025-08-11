package com.isaevapps.domain.model

data class TimeZone(val name: String) {
    fun getUTCDouble(): Double =
        name.substring(3, 6).toInt() + name.substring(7, 9).toInt() / 60.0;
}

