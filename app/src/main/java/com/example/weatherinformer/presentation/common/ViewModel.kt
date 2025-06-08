package com.example.weatherinformer.presentation.common

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.location.Location
import com.example.domain.location.LocationClient
import com.example.domain.model.CurrentWeather
import com.example.domain.model.DailyWeather
import com.example.domain.model.ThreeHoursWeather
import com.example.domain.model.WeatherData
import com.example.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor(
    private val repository: WeatherRepository ,
    private val locationClient: LocationClient
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<WeatherData>>(UiState.Loading)
    val uiState: StateFlow<UiState<WeatherData>> = _uiState

    private val _dailyForecast = MutableStateFlow<List<DailyWeather>>(emptyList())
    private val _threeHoursWeather = MutableStateFlow<List<ThreeHoursWeather>>(emptyList())
    private val _allThreeHoursWeather = MutableStateFlow<List<ThreeHoursWeather>>(emptyList())

    val threeHoursGroupedByDate = _threeHoursWeather
        .map { list ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd" ,Locale.getDefault())
            list.groupBy { dateFormat.format(it.timestamp) }
        }
        .stateIn(viewModelScope , SharingStarted.Companion.Eagerly, emptyMap())

    val allThreeHoursGroupedByDate = _allThreeHoursWeather
        .map { list ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd" ,Locale.getDefault())
            list.groupBy { dateFormat.format(it.timestamp) }
        }
        .stateIn(viewModelScope, SharingStarted.Companion.Eagerly, emptyMap())

    private var refreshJob: Job? = null
    private var currentLocation: Location? = null

    private companion object {
        const val TAG = "MainViewModel"
    }

    init {
        Log.d(TAG, "ViewModel initialized")
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        refreshJob = viewModelScope.launch {
            Log.d(TAG, "Auto-refresh started")
            while (true) {
                fetchLocationAndWeather()
                delay(60_000)
            }
        }
    }

    fun fetchLocationAndWeather() {
        Log.d(TAG, "Starting location and weather fetch")
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                Log.d(TAG, "Requesting location from LocationClient")
                val location = getLocation()
                if (location != null) {
                    Log.d(TAG, "Location obtained: lat=${location.latitude}, lon=${location.longitude}")
                    currentLocation = location
                    fetchWeather(location.latitude, location.longitude)
                } else {
                    Log.w(TAG, "Location unavailable (null)")
                    _uiState.value = UiState.Error("Location unavailable")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while fetching location: ${e.message}", e)
                _uiState.value = UiState.Error(e.message ?: "Location error")
            }
        }
    }

    private suspend fun getLocation(): Location? {
        Log.d(TAG, "Requesting location from LocationClient...")
        return try {
            val loc = locationClient.getLocation()
            if (loc == null) {
                Log.w(TAG, "LocationClient returned null location. Trying to wait and retry...")
                delay(1000) // Подожди 1 секунду
                val retryLoc = locationClient.getLocation()
                if (retryLoc == null) {
                    Log.w(TAG, "Retry also returned null location.")
                } else {
                    Log.d(TAG, "Retry succeeded: lat=${retryLoc.latitude}, lon=${retryLoc.longitude}")
                }
                retryLoc
            } else {
                Log.d(TAG, "Location received: lat=${loc.latitude}, lon=${loc.longitude}")
                loc
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception getting location: ${e.message}", e)
            null
        }
    }


    private suspend fun fetchWeather(lat: Double, lon: Double) {
        try {
            Log.d(TAG, "Fetching weather data for lat=$lat, lon=$lon")
            val weatherData = repository.getWeatherData(lat, lon)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd" ,Locale.getDefault())
            val todayString = dateFormat.format(Date())

            val filteredThreeHours = weatherData.threeHoursWeather.filter { threeHoursWeather ->
                dateFormat.format(threeHoursWeather.timestamp) == todayString
            }

            Log.d(TAG, "Weather data received: " +
                    "\n  Location: ${weatherData.location}" +
                    "\n  Temperature: ${weatherData.currentWeather.temperature}°C" +
                    "\n  Forecast days: ${weatherData.dailyForecast.size}" +
                    "\n  Filtered three hours count: ${filteredThreeHours.size}")

            _uiState.value = UiState.Success(weatherData)

            _dailyForecast.value = weatherData.dailyForecast
            _threeHoursWeather.value = filteredThreeHours
            _allThreeHoursWeather.value = weatherData.threeHoursWeather

        } catch (e: Exception) {
            Log.e(TAG, "Weather data fetch error: ${e.message}", e)
            _uiState.value = UiState.Error(e.message ?: "Weather data error")
        }
    }

    fun refresh() {
        Log.d(TAG, "Manual refresh triggered")
        currentLocation?.let {
            Log.d(TAG, "Using cached location for refresh: lat=${it.latitude}, lon=${it.longitude}")
            viewModelScope.launch {
                fetchWeather(it.latitude, it.longitude)
            }
        } ?: run {
            Log.d(TAG, "No cached location, fetching new location")
            fetchLocationAndWeather()
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared, cancelling refresh job")
        refreshJob?.cancel()
    }
}