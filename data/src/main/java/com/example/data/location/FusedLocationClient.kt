package com.example.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.location.Location as AndroidLocation
import androidx.core.content.ContextCompat
import com.example.domain.location.Location
import com.example.domain.location.LocationClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FusedLocationClient @Inject constructor(
    private val context: Context
) : LocationClient {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    override suspend fun getLocation(): Location? {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocation && !hasCoarseLocation) {
            Log.w("FusedLocationClient", "Permissions not granted")
            return null
        }

        return try {
            Log.d("FusedLocationClient", "Requesting lastLocation...")
            val androidLocation: AndroidLocation? = fusedLocationClient.lastLocation.await()

            if (androidLocation == null) {
                Log.w("FusedLocationClient", "lastLocation returned null, maybe location not yet available")
            } else {
                Log.d("FusedLocationClient", "Location received: ${androidLocation.latitude}, ${androidLocation.longitude}")
            }

            androidLocation?.let {
                Location(
                    latitude = it.latitude,
                    longitude = it.longitude
                )
            }
        } catch (e: Exception) {
            Log.e("FusedLocationClient", "Error getting location: ${e.message}", e)
            null
        }
    }
}
