package com.example.ercrm.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ercrm.data.api.ApiService
import com.example.ercrm.data.model.SalespersonDashboardData
import com.example.ercrm.data.model.SalespersonDashboardResponse
import com.example.ercrm.data.repository.SalespersonDashboardRepository
import com.example.ercrm.di.NetworkModule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SalespersonDashboardViewModel @Inject constructor(
    private val apiService: ApiService,
    private val dashboardRepository: SalespersonDashboardRepository
) : ViewModel() {

    private val _dashboardState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            try {
                _dashboardState.value = DashboardState.Loading
                _error.value = null
                
                if (NetworkModule.getAuthToken() == null) {
                    _dashboardState.value = DashboardState.Error("Not authenticated. Please login again.")
                    return@launch
                }

                val response = dashboardRepository.getDashboard()
                if (response.isSuccessful) {
                    val dashboardResponse = response.body()
                    android.util.Log.d("SalespersonDashboardViewModel", "Raw response: $dashboardResponse")

                    if (dashboardResponse != null) {
                        _dashboardState.value = DashboardState.Success(dashboardResponse.data ?: SalespersonDashboardData())
                    } else {
                        _dashboardState.value = DashboardState.Error("Empty response from server")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("SalespersonDashboardViewModel", "Error response: $errorBody")
                    val errorMessage = when (response.code()) {
                        401 -> {
                            NetworkModule.setAuthToken(null)
                            "Session expired. Please login again."
                        }
                        403 -> "You don't have permission to access this resource"
                        404 -> "Dashboard data not found"
                        500 -> "Server error. Please try again later."
                        else -> errorBody ?: "Failed to load dashboard: ${response.code()}"
                    }
                    _dashboardState.value = DashboardState.Error(errorMessage)
                }
            } catch (e: Exception) {
                android.util.Log.e("SalespersonDashboardViewModel", "Network error", e)
                val errorMessage = when (e) {
                    is java.net.UnknownHostException -> "No internet connection"
                    is java.net.SocketTimeoutException -> "Connection timed out"
                    is retrofit2.HttpException -> "Server error: ${e.code()}"
                    else -> e.message ?: "An unexpected error occurred"
                }
                _dashboardState.value = DashboardState.Error(errorMessage)
            }
        }
    }

    fun checkIn(location: Location) {
        viewModelScope.launch {
            try {
                _error.value = null
                val locationJson = buildLocationJson(location)
                val requestBody = mapOf("check_in_location" to locationJson)

                val response = dashboardRepository.checkIn(requestBody)
                if (response.isSuccessful) {
                    response.body()?.let { dashboardResponse ->
                        if (dashboardResponse.success) {
                            loadDashboard()
                        } else {
                            _error.value = dashboardResponse.message
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
                _error.value = null
                val locationJson = buildLocationJson(location)
                val requestBody = mapOf("check_out_location" to locationJson)

                val response = dashboardRepository.checkOut(requestBody)
                if (response.isSuccessful) {
                    response.body()?.let { dashboardResponse ->
                        if (dashboardResponse.success) {
                            loadDashboard()
                        } else {
                            _error.value = dashboardResponse.message
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
        return """
            {
                "latitude": ${location.latitude},
                "longitude": ${location.longitude},
                "accuracy": ${location.accuracy}
            }
        """.trimIndent()
    }
}

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(val data: SalespersonDashboardData) : DashboardState()
    data class Error(val message: String) : DashboardState()
} 