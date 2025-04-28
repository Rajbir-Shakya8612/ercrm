package com.example.ercrm.api

import com.example.ercrm.data.LoginCredentials
import com.example.ercrm.data.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body credentials: LoginCredentials): LoginResponse
}

object ApiClient {
    private const val BASE_URL = "http://127.0.0.1:8000/"
    
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
} 