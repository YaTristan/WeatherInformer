package com.example.weatherinformer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.weatherinformer.presentation.main.MainPage
import com.example.weatherinformer.presentation.weekly.WeekPage

@Composable
fun WeatherNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.MAIN
    ) {
        composable(Routes.MAIN) {
            MainPage(navController)
        }
        composable(Routes.WEEK) {
            WeekPage(navController)
        }
    }
}