package com.example.domain.location

interface LocationClient {
    suspend fun getLocation(): Location?
}