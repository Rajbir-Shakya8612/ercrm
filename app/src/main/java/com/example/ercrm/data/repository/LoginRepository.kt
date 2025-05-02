package com.example.ercrm.data.repository

import android.util.Log
import com.example.ercrm.data.api.ApiService
import com.example.ercrm.data.model.LoginRequest
import com.example.ercrm.data.model.LoginResponse
import com.example.ercrm.di.NetworkModule
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(
    private val apiService: ApiService
) {
    companion object {
        private const val TAG = "LoginRepository"
    }

    suspend fun login(email: String, password: String): Response<LoginResponse> {
        val loginRequest = LoginRequest(email = email, password = password)
        val response = apiService.login(loginRequest)
        if (response.isSuccessful) {
            // Save the token when login is successful
            val token = response.body()?.token
            Log.d(TAG, "Login successful, token received: ${token?.take(10)}...")
            
            // Set the token in NetworkModule
            NetworkModule.setAuthToken(token)
            Log.d(TAG, "Token set in NetworkModule")
        } else {
            Log.e(TAG, "Login failed: ${response.code()} - ${response.message()}")
        }
        return response
    }
    
    suspend fun logout(): Response<Unit> {
        try {
            val response = apiService.logout()
            if (response.isSuccessful) {
                clearToken()
                Log.d(TAG, "Logout successful")
            } else {
                Log.e(TAG, "Logout failed: ${response.code()} - ${response.message()}")
            }
            return response
        } catch (e: Exception) {
            Log.e(TAG, "Error during logout", e)
            throw e
        }
    }

    // Clear the token when needed (e.g., on logout success or app exit)
    fun clearToken() {
        Log.d(TAG, "Clearing auth token")
        NetworkModule.setAuthToken(null)
    }

    // Get current token for debugging
    fun getCurrentToken(): String? = NetworkModule.getAuthToken()
} 