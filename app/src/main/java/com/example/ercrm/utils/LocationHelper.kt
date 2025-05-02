package com.example.ercrm.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationHelper @Inject constructor(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) {
    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
    }

    fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            LOCATION_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getLocationUpdates(intervalMs: Long = 10000): Flow<Location> = callbackFlow {
        if (!hasLocationPermission()) {
            throw SecurityException("Location permission not granted")
        }

        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = intervalMs
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    trySend(location)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    suspend fun getLastLocation(): Location = suspendCancellableCoroutine { continuation ->
        if (!hasLocationPermission()) {
            continuation.resumeWithException(SecurityException("Location permission not granted"))
            return@suspendCancellableCoroutine
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(location)
                } else {
                    continuation.resumeWithException(Exception("Location not available"))
                }
            }
            .addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
    }
} 