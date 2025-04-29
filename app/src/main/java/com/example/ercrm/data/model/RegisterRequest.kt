package com.example.ercrm.data.model

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role_id: Int,
    val is_active: Boolean = true,
    val phone: String? = null,
    val whatsapp_number: String? = null,
    val pincode: String? = null,
    val address: String? = null,
    val location: String? = null,
    val designation: String? = null,
    val date_of_joining: String? = null,
    val target_amount: Double? = null,
    val target_leads: Int? = null
) 