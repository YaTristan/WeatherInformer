package com.example.weatherinformer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.weatherinformer.presentation.main.MainPage
import com.example.weatherinformer.presentation.navigation.WeatherNavGraph
import com.example.weatherinformer.ui.theme.WeatherInformerTheme
import com.example.weatherinformer.presentation.weekly.WeekPage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestLocationPermission(this, this)

        setContent {
            WeatherInformerTheme {
                val navController = rememberNavController()
                WeatherNavGraph(navController)
            }
        }
    }
}

private fun requestLocationPermission(context: Context, activity: Activity) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1001
        )
    }
}
