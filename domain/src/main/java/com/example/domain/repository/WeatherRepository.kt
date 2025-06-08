package com.example.domain.repository

import com.example.domain.model.WeatherData

interface WeatherRepository {
    suspend fun getWeatherData(lat: Double, lon: Double): WeatherData
}