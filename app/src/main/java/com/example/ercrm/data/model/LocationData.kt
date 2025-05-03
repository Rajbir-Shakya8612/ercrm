package com.example.ercrm.data.model

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val address: String? = null,
    val speed: Double? = null,
    val type: String = "tracking",
    val exit_timestamp: String? = null,
    val stay_duration: Int? = null,
    val tracked_at: String? = null
) 