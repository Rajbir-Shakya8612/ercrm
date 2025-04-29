package com.example.ercrm.data.api

import com.example.ercrm.data.model.LoginRequest
import com.example.ercrm.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface ApiService {
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Unit>
} 