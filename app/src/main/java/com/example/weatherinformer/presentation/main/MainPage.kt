package com.example.weatherinformer.presentation.main

import android.R.attr.contentDescription
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.domain.model.CurrentWeather
import com.example.domain.model.ThreeHoursWeather
import com.example.weatherinformer.presentation.common.BoxForItem
import com.example.weatherinformer.presentation.common.ViewModel
import com.example.weatherinformer.presentation.common.ErrorPage
import com.example.weatherinformer.presentation.common.LoadingPage
import com.example.weatherinformer.presentation.common.UiState
import com.example.weatherinformer.presentation.navigation.Routes
import java.util.Locale

@Composable
fun MainPage(
    navController : NavController,
    viewModel: ViewModel = hiltViewModel(),
) {
    val state = viewModel.uiState.collectAsState()
    val threeHoursWeather = viewModel.threeHoursGroupedByDate.collectAsState()


    when (state.value) {
        is UiState.Loading -> LoadingPage()

        is UiState.Error -> ErrorPage()

        is UiState.Success -> {
            threeHoursWeather.value?.let { threeHours ->
                SuccessMainPage(threeHours, navController)
            } ?: LoadingPage()
        }
    }
}

@Composable
fun SuccessMainPage(dailyWeather : Map<String, List<ThreeHoursWeather>>, navController : NavController) {

    LazyColumn(
        modifier = Modifier
            .padding(
                WindowInsets.statusBars.asPaddingValues()
            )
            .padding(horizontal = 8.dp) ,
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Прогноз на неделю" ,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clickable {
                            navController.navigate(Routes.WEEK)
                        }
                )
            }
        }
        dailyWeather.forEach { (date ,forecasts) ->

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp) ,
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = date)
                }
            }

            items(forecasts) { forecast ->
                Box(
                    modifier = Modifier.fillMaxWidth() ,
                    contentAlignment = Alignment.Center
                ) {
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
}
