package com.example.ercrm.data

data class LoginCredentials(
    val username: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null
) 