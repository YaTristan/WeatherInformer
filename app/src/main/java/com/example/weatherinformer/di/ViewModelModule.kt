package com.example.weatherinformer.di

import com.example.domain.location.LocationClient
import com.example.domain.repository.WeatherRepository
import com.example.weatherinformer.presentation.common.ViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideMainViewModel(
        repository : WeatherRepository ,
        locationClient : LocationClient
    ) : ViewModel = ViewModel(repository ,locationClient)
}