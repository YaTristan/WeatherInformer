package com.example.weatherinformer.di

import android.content.Context
import com.example.data.api.WeatherApi
import com.example.data.location.FusedLocationClient
import com.example.data.repository.WeatherRepositoryImpl
import com.example.domain.location.LocationClient
import com.example.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWeatherRepository(api: WeatherApi): WeatherRepositoryImpl {
        return WeatherRepositoryImpl(api)
    }



}