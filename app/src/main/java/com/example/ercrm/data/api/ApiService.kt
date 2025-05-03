package com.example.ercrm.data.api

import com.example.ercrm.data.model.AttendanceResponse
import com.example.ercrm.data.model.LoginRequest
import com.example.ercrm.data.model.LoginResponse
import com.example.ercrm.data.model.RegisterRequest
import com.example.ercrm.data.model.Role
import com.example.ercrm.data.model.AttendanceStatusResponse
//import com.example.ercrm.data.model.NewDashboardScreen
import retrofit2.Response
import retrofit2.http.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface ApiService {
    @Headers("Accept: application/json")
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @Headers("Accept: application/json")
    @POST("api/logout")
    suspend fun logout(): Response<Unit>

    @Headers("Accept: application/json")
    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>

    @Headers("Accept: application/json")
    @GET("api/roles")
    suspend fun getRoles(): Response<List<Role>>

    @Headers("Accept: application/json")
    @GET("api/salesperson/attendance/status")
    suspend fun getAttendanceStatus(): Response<AttendanceStatusResponse>

    @Headers("Accept: application/json")
    @POST("api/attendance/checkin")
    suspend fun checkIn(@Body requestBody: Map<String, String>): Response<AttendanceResponse>

    @Headers("Accept: application/json")
    @POST("api/attendance/checkout")
    suspend fun checkOut(@Body requestBody: Map<String, String>): Response<AttendanceResponse>

//    @Headers("Accept: application/json")
//    @GET("api/salesperson/dashboard")
//    suspend fun getSalespersonDashboard(): Response<SalespersonDashboardResponse>
//
//    @Headers("Accept: application/json")
//    @GET("api/admin/dashboard")
//    suspend fun getAdminDashboard(): Response<SalespersonDashboardResponse>
} 