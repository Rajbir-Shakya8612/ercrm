package com.example.ercrm.data.repository

import android.util.Log
import com.example.ercrm.data.api.ApiService
import com.example.ercrm.data.model.SalespersonDashboardResponse
import com.example.ercrm.data.model.AttendanceResponse
import retrofit2.Response
import javax.inject.Inject

class SalespersonDashboardRepository @Inject constructor(
    private val apiService: ApiService
) {

    companion object {
        private const val TAG = "SalespersonDashboardRepository"
    }

    suspend fun getDashboard(): Response<SalespersonDashboardResponse> {
        return try {
            val response = apiService.getSalespersonDashboard()
            if (!response.isSuccessful) {
                Log.e(TAG, "Dashboard fetch failed: ${response.code()} - ${response.message()}")
            } else {
                Log.d(TAG, "Dashboard data fetched successfully")
            }
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching dashboard data", e)
            throw e
        }
    }

    suspend fun checkIn(location: Map<String, String>): Response<AttendanceResponse> {
        return try {
            apiService.checkIn(location)
        } catch (e: Exception) {
            Log.e(TAG, "Error during check-in", e)
            throw e
        }
    }

    suspend fun checkOut(location: Map<String, String>): Response<AttendanceResponse> {
        return try {
            apiService.checkOut(location)
        } catch (e: Exception) {
            Log.e(TAG, "Error during check-out", e)
            throw e
        }
    }
}
