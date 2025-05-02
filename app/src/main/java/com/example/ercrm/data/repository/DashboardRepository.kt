package com.example.ercrm.data.repository

import android.util.Log
import com.example.ercrm.data.api.ApiService
import com.example.ercrm.data.model.DashboardResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton
import com.google.gson.JsonSyntaxException

@Singleton
class DashboardRepository @Inject constructor(
    private val apiService: ApiService
) {
    companion object {
        private const val TAG = "DashboardRepository"
    }

    suspend fun getDashboard(): Response<DashboardResponse> {
        return try {
            val response = apiService.getSalespersonDashboard()

            if (!response.isSuccessful) {
                Log.e(TAG, "Dashboard fetch failed: ${response.code()} - ${response.message()}")
                // Try to read error message from response body
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Error body: $errorBody")
            } else {
                try {
                    response.body()?.let {
                        Log.d(TAG, "Dashboard data fetched successfully")
                    }
                } catch (e: JsonSyntaxException) {
                    Log.e(TAG, "JSON parsing error: ${e.message}")
                    throw Exception("Server returned invalid response format: ${e.message}")
                }
            }
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching dashboard data", e)
            throw e
        }
    }
} 