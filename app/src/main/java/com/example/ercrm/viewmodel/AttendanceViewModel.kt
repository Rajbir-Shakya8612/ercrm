package com.example.ercrm.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ercrm.data.api.ApiService
import com.example.ercrm.data.model.AttendanceResponse
import com.example.ercrm.data.model.AttendanceState
import com.example.ercrm.data.model.LocationData
import com.example.ercrm.data.model.AttendanceStatusResponse
import com.example.ercrm.service.LocationTrackingService
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.*

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _attendanceState = MutableStateFlow(AttendanceState())
    val attendanceState: StateFlow<AttendanceState> = _attendanceState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _canCheckIn = MutableStateFlow(true)
    val canCheckIn: StateFlow<Boolean> = _canCheckIn.asStateFlow()
    private val _canCheckOut = MutableStateFlow(false)
    val canCheckOut: StateFlow<Boolean> = _canCheckOut.asStateFlow()

    init {
        checkTodayAttendance()
    }

    private fun checkTodayAttendance() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = apiService.getAttendanceStatus()
                if (response.isSuccessful) {
                    response.body()?.let { statusResp ->
                        val att = statusResp.attendance
                        _attendanceState.value = AttendanceState(
                            isCheckedIn = statusResp.canCheckOut, // checked in if can check out
                            check_in_time = att?.check_in_time,
                            check_out_time = att?.check_out_time,
                            working_hours = att?.working_hours,
                            status = att?.status
                        )
                        _canCheckIn.value = statusResp.canCheckIn
                        _canCheckOut.value = statusResp.canCheckOut
                    }
                } else {
                    _error.value = "Failed to fetch attendance status"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun checkIn(locationData: LocationData, context: Context) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val locationJson = Gson().toJson(locationData.copy(
                    type = "check_in",
                    tracked_at = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                ))
                val response = apiService.checkIn(mapOf(
                    "check_in_location" to locationJson
                ))
                if (response.isSuccessful) {
                    response.body()?.let { attendanceResponse ->
                        _attendanceState.value = AttendanceState(
                            isCheckedIn = true,
                            check_in_time = attendanceResponse.check_in_time,
                            check_out_time = attendanceResponse.check_out_time,
                            working_hours = attendanceResponse.working_hours,
                            status = attendanceResponse.status
                        )
                        _canCheckIn.value = false
                        _canCheckOut.value = true

                        // Start location tracking service
                        val serviceIntent = Intent(context, LocationTrackingService::class.java).apply {
                            action = "START_TRACKING"
                        }
                        context.startService(serviceIntent)
                    }
                } else {
                    _error.value = response.body()?.message ?: "Failed to check in"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun checkOut(locationData: LocationData, context: Context) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val locationJson = Gson().toJson(locationData.copy(
                    type = "check_out",
                    tracked_at = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                ))
                val response = apiService.checkOut(mapOf(
                    "check_out_location" to locationJson
                ))
                if (response.isSuccessful) {
                    response.body()?.let { attendanceResponse ->
                        _attendanceState.value = AttendanceState(
                            isCheckedIn = false,
                            check_in_time = attendanceResponse.check_in_time,
                            check_out_time = attendanceResponse.check_out_time,
                            working_hours = attendanceResponse.working_hours,
                            status = attendanceResponse.status
                        )
                        _canCheckIn.value = false
                        _canCheckOut.value = false

                        // Stop location tracking service
                        val serviceIntent = Intent(context, LocationTrackingService::class.java).apply {
                            action = "STOP_TRACKING"
                        }
                        context.startService(serviceIntent)
                    }
                } else {
                    _error.value = response.body()?.message ?: "Failed to check out"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}