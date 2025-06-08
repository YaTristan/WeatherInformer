package com.example.data.api

import com.example.data.BuildConfig
import com.example.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "ru",
        @Query("appid") apiKey: String = BuildConfig.OPEN_WEATHER_API_KEY
    ): WeatherResponse
}