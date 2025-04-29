package com.example.ercrm.data.repository

import com.example.ercrm.data.api.RetrofitClient
import com.example.ercrm.data.model.LoginResponse
import com.example.ercrm.data.model.RegisterRequest
import retrofit2.Response

class RegisterRepository {
    suspend fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        role: String,
        brandPasskey: String
    ): Response<LoginResponse> {
        val registerRequest = RegisterRequest(
            name = name,
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            role = role,
            brandPasskey = brandPasskey
        )
        return RetrofitClient.apiService.register(registerRequest)
    }
} 