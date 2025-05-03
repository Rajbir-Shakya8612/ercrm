package com.example.ercrm.data.model

data class AttendanceResponse(
    val success: Boolean,
    val message: String,
    val check_in_time: String? = null,
    val check_out_time: String? = null,
    val status: String? = null,
    val working_hours: String? = null
)

data class AttendanceState(
    val isCheckedIn: Boolean = false,
    val check_in_time: String? = null,
    val check_out_time: String? = null,
    val working_hours: String? = null,
    val status: String? = null
)

data class AttendanceData(
    val check_in_time: String? = null,
    val check_out_time: String? = null,
    val working_hours: String? = null,
    val status: String? = null
)

data class AttendanceStatusResponse(
    val attendance: AttendanceData?,
    val canCheckIn: Boolean,
    val canCheckOut: Boolean
) 