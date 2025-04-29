package com.example.ercrm.data.repository

import com.example.ercrm.data.api.RetrofitClient
import com.example.ercrm.data.model.LoginRequest
import com.example.ercrm.data.model.LoginResponse
import retrofit2.Response

class LoginRepository {
    // Store the token after successful login
    private var authToken: String? = null

    suspend fun login(email: String, password: String): Response<LoginResponse> {
        val loginRequest = LoginRequest(email = email, password = password)
        val response = RetrofitClient.apiService.login(loginRequest)
        if (response.isSuccessful) {
            // Save the token when login is successful
            authToken = response.body()?.token
        }
        return response
    }
    
    suspend fun logout(): Response<Unit> {
        // Check if we have a token to send with logout request
        val token = authToken ?: throw IllegalStateException("Not logged in")
        return RetrofitClient.apiService.logout("Bearer $token")
    }

    // Clear the token when needed (e.g., on logout success or app exit)
    fun clearToken() {
        authToken = null
    }
} 