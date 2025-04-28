package com.example.ercrm.data.repository

import com.example.ercrm.data.api.RetrofitClient
import com.example.ercrm.data.model.LoginRequest
import com.example.ercrm.data.model.LoginResponse
import retrofit2.Response

class LoginRepository {
    suspend fun login(email: String, password: String): Response<LoginResponse> {
        val loginRequest = LoginRequest(email = email, password = password)
        return RetrofitClient.apiService.login(loginRequest)
    }
} 