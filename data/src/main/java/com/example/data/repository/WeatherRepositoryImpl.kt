package com.example.data.repository

import android.R.attr.apiKey
import android.util.Log
import com.example.data.BuildConfig
import com.example.data.api.WeatherApi
import com.example.data.model.ForecastItem
import com.example.data.model.WeatherResponse
import com.example.domain.model.CurrentWeather
import com.example.domain.model.DailyWeather
import com.example.domain.model.ThreeHoursWeather
import com.example.domain.model.WeatherData
import com.example.domain.repository.WeatherRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.collections.first
import kotlin.collections.minByOrNull

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
) : WeatherRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val TAG = "WeatherRepository"

    override suspend fun getWeatherData(lat: Double, lon: Double): WeatherData {
        try {
            val apiKey = BuildConfig.OPEN_WEATHER_API_KEY
            val url = "https://api.openweathermap.org/data/2.5/forecast?" +
                    "lat=$lat&lon=$lon&units=metric&lang=ru&appid=$apiKey"

            Log.d(TAG, "Request URL: $url")

            val response = api.getWeatherForecast(lat, lon)
            return mapResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "API error: ${e.message}", e)
            throw e
        }
    }

    private fun mapResponse(response: WeatherResponse): WeatherData {
        return WeatherData(
            location = "${response.city.name}, ${response.city.country}",
            currentWeather = mapCurrentWeather(response.forecastItems.first()),
            dailyForecast = mapDailyForecast(response.forecastItems),
            threeHoursWeather = mapHourlyForecast(response.forecastItems)
        )
    }
    private fun mapHourlyForecast(items: List<ForecastItem>): List<ThreeHoursWeather> {
        return items.map { item ->
            ThreeHoursWeather(
                timestamp = Date(item.timestamp * 1000),
                temperature = item.mainData.temperature,
                feelsLike = item.mainData.feelsLike,
                pressure = item.mainData.pressure,
                humidity = item.mainData.humidity,
                windSpeed = item.wind.speed,
                windDirection = item.wind.direction,
                condition = item.weatherConditions.firstOrNull()?.description ?: "",
                iconCode = item.weatherConditions.firstOrNull()?.iconCode ?: ""
            )
        }
    }

    private fun mapCurrentWeather(item: ForecastItem): CurrentWeather {
        return CurrentWeather(
            temperature = item.mainData.temperature,
            feelsLike = item.mainData.feelsLike,
            pressure = item.mainData.pressure,
            humidity = item.mainData.humidity,
            windSpeed = item.wind.speed,
            windDirection = item.wind.direction,
            condition = item.weatherConditions.firstOrNull()?.description ?: "",
            iconCode = item.weatherConditions.firstOrNull()?.iconCode ?: "",
            timestamp = Date(item.timestamp * 1000)
        )
    }

    private fun mapDailyForecast(items: List<ForecastItem>): List<DailyWeather> {
        val dailyMap = mutableMapOf<String, MutableList<ForecastItem>>()

        items.forEach { item ->
            val date = dateFormat.format(Date(item.timestamp * 1000))
            dailyMap.getOrPut(date) { mutableListOf() }.add(item)
        }

        return dailyMap.map { (date, dailyItems) ->
            val minTemp = dailyItems.minOf { it.mainData.tempMin }
            val maxTemp = dailyItems.maxOf { it.mainData.tempMax }

            val dayItem = dailyItems.minByOrNull {
                val hour = it.dateText.substring(11, 13).toInt()
                kotlin.math.abs(hour - 12)
            } ?: dailyItems.first()

            DailyWeather(
                date = dateFormat.parse(date) ?: Date(),
                minTemp = minTemp,
                maxTemp = maxTemp,
                condition = dayItem.weatherConditions.firstOrNull()?.description ?: "",
                iconCode = dayItem.weatherConditions.firstOrNull()?.iconCode ?: ""
            )
        }.sortedBy { it.date }
    }
}