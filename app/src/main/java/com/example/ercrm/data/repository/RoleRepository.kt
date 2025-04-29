package com.example.ercrm.data.repository

import android.util.Log
import com.example.ercrm.data.api.RetrofitClient
import com.example.ercrm.data.model.Role
import retrofit2.Response

class RoleRepository {
    suspend fun getRoles(): Response<List<Role>> {
        Log.d("RoleRepository", "Fetching roles from API...")
        try {
            val response = RetrofitClient.apiService.getRoles()
            Log.d("RoleRepository", "Roles API response: ${response.isSuccessful}, code: ${response.code()}, roles count: ${response.body()?.size}")
            
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                Log.e("RoleRepository", "Error response: $errorBody")
                Log.e("RoleRepository", "Error code: ${response.code()}")
            } else {
                val roles = response.body()
                Log.d("RoleRepository", "Received roles: ${roles?.map { it.name }}")
            }
            
            return response
        } catch (e: Exception) {
            Log.e("RoleRepository", "Exception while fetching roles", e)
            throw e
        }
    }
} 