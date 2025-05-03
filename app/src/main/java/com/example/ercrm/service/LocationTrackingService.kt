package com.example.ercrm.service

import android.app.*
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.ercrm.R
import com.example.ercrm.data.api.ApiService
import com.example.ercrm.data.model.LocationData
import com.example.ercrm.utils.LocationHelper
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
import java.util.concurrent.TimeUnit
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class LocationTrackingService : Service() {
    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var locationHelper: LocationHelper

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var isTracking = false
    private var lastLocation: Location? = null
    private var lastLocationTime: Long = 0

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "location_tracking_channel"
        private const val CHANNEL_NAME = "Location Tracking"
        private const val LOCATION_INTERVAL = 5L // 5 minutes
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START_TRACKING" -> startLocationTracking()
            "STOP_TRACKING" -> stopLocationTracking()
        }
        return START_STICKY
    }

    private fun startLocationTracking() {
        if (isTracking) return
        isTracking = true

        val notification = createNotification("Location tracking active")
        startForeground(NOTIFICATION_ID, notification)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    serviceScope.launch {
                        try {
                            val currentTime = System.currentTimeMillis()
                            val speed = if (lastLocation != null) {
                                val distance = lastLocation!!.distanceTo(location)
                                val timeDiff = (currentTime - lastLocationTime) / 1000.0 // in seconds
                                if (timeDiff > 0) distance / timeDiff else 0.0
                            } else 0.0

                            val locationData = LocationData(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                accuracy = location.accuracy,
                                speed = speed,
                                type = "tracking",
                                tracked_at = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                            )
                            
                            apiService.trackLocation(locationData)
                            
                            lastLocation = location
                            lastLocationTime = currentTime
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = TimeUnit.MINUTES.toMillis(LOCATION_INTERVAL)
            fastestInterval = TimeUnit.MINUTES.toMillis(1)
        }

        try {
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun stopLocationTracking() {
        isTracking = false
        locationCallback?.let {
            fusedLocationClient?.removeLocationUpdates(it)
        }
        stopForeground(true)
        stopSelf()
    }

    private fun createNotification(content: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location Tracking")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        stopLocationTracking()
    }
} 