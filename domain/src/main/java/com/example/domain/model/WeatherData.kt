package com.example.domain.model

import java.util.Date

data class WeatherData(
    val location: String,
    val currentWeather: CurrentWeather,
    val dailyForecast: List<DailyWeather>,
    val threeHoursWeather: List<ThreeHoursWeather>
)

data class ThreeHoursWeather(
    val timestamp: Date,
    val temperature: Double,
    val feelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    val windSpeed: Double,
    val windDirection: Int,
    val condition: String,
    val iconCode: String
)

data class CurrentWeather(
    val temperature: Double,
    val feelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    val windSpeed: Double,
    val windDirection: Int,
    val condition: String,
    val iconCode: String,
    val timestamp: Date
)

data class DailyWeather(
    val date: Date,
    val minTemp: Double,
    val maxTemp: Double,
    val condition: String,
    val iconCode: String
)