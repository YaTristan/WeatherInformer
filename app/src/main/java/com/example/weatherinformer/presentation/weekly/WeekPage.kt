package com.example.weatherinformer.presentation.weekly

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.domain.model.ThreeHoursWeather
import com.example.weatherinformer.presentation.common.BoxForItem
import com.example.weatherinformer.presentation.common.ErrorPage
import com.example.weatherinformer.presentation.common.LoadingPage
import com.example.weatherinformer.presentation.common.UiState
import com.example.weatherinformer.presentation.common.ViewModel
import java.util.Locale
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun WeekPage(
    navController : NavController,
    viewModel: ViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsState()
    val weather = viewModel.allThreeHoursGroupedByDate.collectAsState()
    Log.d("WeekPage", "groupedWeather: ${weather.value}")
    when (val state = state.value) {
        is UiState.Loading -> LoadingPage()
        is UiState.Error -> ErrorPage()
        is UiState.Success -> {
            if (weather.value.isNotEmpty()) {
                Log.d("WeekPage", "groupedWeather: ${weather.value}")
                SuccessWeekPage(weather.value, navController)
            } else {
                LoadingPage()
            }
        }
    }
}

@Composable
fun SuccessWeekPage(weather : Map<String, List<ThreeHoursWeather>>, navController : NavController) {
    LazyColumn(
        modifier = Modifier
            .padding(
                WindowInsets.statusBars.asPaddingValues()
            )
            .padding(horizontal = 8.dp)
    ) {
       item {
           Box(
               modifier = Modifier.fillMaxWidth()
           ) {
               Text(
                   "Назад" ,
                   modifier = Modifier
                       .align(Alignment.TopEnd)
                       .clickable {
                           navController.popBackStack()
                   }
               )
           }
       }
        weather.forEach { (date ,forecasts) ->

            item { Row {
                Text(
                    text = date ,
                    modifier = Modifier.padding(top = 8.dp) ,
                )

            } }

            item {
                LazyRow {
                    items(forecasts) { forecast ->
                        BoxForItem(
                            time = SimpleDateFormat(
                                "HH:mm" ,
                                Locale.getDefault()
                            ).format(forecast.timestamp) ,
                            temp = forecast.temperature ,
                            hum = forecast.humidity ,
                            wind = forecast.windSpeed ,
                            image = forecast.iconCode ,
                            feelsLike = forecast.feelsLike ,
                            pressure = forecast.pressure ,
                            windDirection = forecast.windDirection
                        )
                    }

                }
            }
        }
        item { Spacer(modifier = Modifier.height(50.dp)) }
    }
}
