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
        role_id: Int,
        is_active: Boolean = true,
        phone: String? = null,
        whatsapp_number: String? = null,
        pincode: String? = null,
        address: String? = null,
        location: String? = null,
        designation: String? = null,
        date_of_joining: String? = null,
        target_amount: Double? = null,
        target_leads: Int? = null
    ): Response<LoginResponse> {
        val registerRequest = RegisterRequest(
            name = name,
            email = email,
            password = password,
            role_id = role_id,
            is_active = is_active,
            phone = phone,
            whatsapp_number = whatsapp_number,
            pincode = pincode,
            address = address,
            location = location,
            designation = designation,
            date_of_joining = date_of_joining,
            target_amount = target_amount,
            target_leads = target_leads
        )
        return RetrofitClient.apiService.register(registerRequest)
    }
} 