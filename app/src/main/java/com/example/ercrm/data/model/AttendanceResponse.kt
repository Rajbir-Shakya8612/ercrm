package com.example.ercrm.data.model

data class AttendanceResponse(
    val success: Boolean,
    val message: String,
    val check_in_time: String? = null,
    val check_out_time: String? = null,
    val status: String? = null,
    val working_hours: String? = null
)

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float
)

data class AttendanceState(
    val isCheckedIn: Boolean = false,
    val check_in_time: String? = null,
    val check_out_time: String? = null,
    val working_hours: String? = null,
    val status: String? = null
) 