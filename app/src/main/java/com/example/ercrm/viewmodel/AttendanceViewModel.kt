package com.example.ercrm.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ercrm.data.api.ApiService
import com.example.ercrm.data.model.AttendanceResponse
import com.example.ercrm.data.model.AttendanceState
import com.example.ercrm.data.model.LocationData
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

    init {
        checkTodayAttendance()
    }

    private fun checkTodayAttendance() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // TODO: Add API call to get today's attendance status
                // For now, we'll just set initial state
                _attendanceState.value = AttendanceState(
                    isCheckedIn = false,
                    check_in_time = null,
                    check_out_time = null,
                    working_hours = null,
                    status = "Not Checked In"
                )
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun checkIn(locationData: LocationData) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = apiService.checkIn(mapOf(
                    "latitude" to locationData.latitude.toString(),
                    "longitude" to locationData.longitude.toString(),
                    "accuracy" to locationData.accuracy.toString()
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
                    }
                } else {
                    _error.value = "Failed to check in"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun checkOut(locationData: LocationData) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = apiService.checkOut(mapOf(
                    "latitude" to locationData.latitude.toString(),
                    "longitude" to locationData.longitude.toString(),
                    "accuracy" to locationData.accuracy.toString()
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
                    }
                } else {
                    _error.value = "Failed to check out"
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