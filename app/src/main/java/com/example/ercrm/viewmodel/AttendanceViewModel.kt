package com.example.ercrm.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ercrm.data.api.ApiService
import com.example.ercrm.data.model.AttendanceState
import com.example.ercrm.data.model.LocationData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _attendanceState = MutableStateFlow(AttendanceState())
    val attendanceState: StateFlow<AttendanceState> = _attendanceState.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun checkIn(location: Location) {
        viewModelScope.launch {
            try {
                // Create location JSON string as required by the API
                val locationJson = buildLocationJson(location)

                // Create request body with check_in_location
                val requestBody = mapOf(
                    "check_in_location" to locationJson
                )

                val response = apiService.checkIn(requestBody)
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        if (body.success) {
                            _attendanceState.value = AttendanceState(
                                isCheckedIn = true,
                                check_in_time = body.check_in_time,
                                status = body.status
                            )
                            _error.value = null
                        } else {
                            _error.value = body.message
                        }
                    }
                } else {
                    _error.value = "Check-in failed: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error during check-in: ${e.message}"
            }
        }
    }

    fun checkOut(location: Location) {
        viewModelScope.launch {
            try {
                // Create location JSON string as required by the API
                val locationJson = buildLocationJson(location)

                // Create request body with check_out_location
                val requestBody = mapOf(
                    "check_out_location" to locationJson
                )

                val response = apiService.checkOut(requestBody)
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        if (body.success) {
                            _attendanceState.value = _attendanceState.value.copy(
                                isCheckedIn = false,
                                check_out_time = body.check_out_time,
                                working_hours = body.working_hours
                            )
                            _error.value = null
                        } else {
                            _error.value = body.message
                        }
                    }
                } else {
                    _error.value = "Check-out failed: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error during check-out: ${e.message}"
            }
        }
    }

    private fun buildLocationJson(location: Location): String {
        val locationData = LocationData(
            latitude = location.latitude,
            longitude = location.longitude,
            accuracy = location.accuracy
        )
        return """{"latitude":${locationData.latitude},"longitude":${locationData.longitude},"accuracy":${locationData.accuracy}}"""
    }
}