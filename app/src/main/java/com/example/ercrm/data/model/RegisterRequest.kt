package com.example.ercrm.data.model

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val role: String,
    val brandPasskey: String
) 