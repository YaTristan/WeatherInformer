package com.example.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("list") val forecastItems: List<ForecastItem>,
    @SerializedName("city") val city: City
)

data class ForecastItem(
    @SerializedName("dt") val timestamp: Long,
    @SerializedName("main") val mainData: MainData,
    @SerializedName("weather") val weatherConditions: List<WeatherCondition>,
    @SerializedName("wind") val wind: Wind,
    @SerializedName("dt_txt") val dateText: String
)

data class City(
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String
)

data class MainData(
    @SerializedName("temp") val temperature: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    @SerializedName("pressure") val pressure: Int,
    @SerializedName("humidity") val humidity: Int
)

data class WeatherCondition(
    @SerializedName("description") val description: String,
    @SerializedName("icon") val iconCode: String
)

data class Wind(
    @SerializedName("speed") val speed: Double,
    @SerializedName("deg") val direction: Int
)