package com.isaevapps.data.network

import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherApi {
    @GET("v1/current.json")
    suspend fun getWeather(
        @Query("q") coordinates: String
    ): WeatherDTO
}