package com.example.ercrm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ercrm.data.repository.RegisterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val repository = RegisterRepository()
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        role: String,
        brandPasskey: String
    ) {
        if (password != confirmPassword) {
            _registerState.value = RegisterState.Error("Passwords do not match")
            return
        }

        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val response = repository.register(
                    name = name,
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword,
                    role = role,
                    brandPasskey = brandPasskey
                )
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        _registerState.value = RegisterState.Success(loginResponse.token)
                    } ?: run {
                        _registerState.value = RegisterState.Error("Empty response body")
                    }
                } else {
                    _registerState.value = RegisterState.Error("Registration failed: ${response.code()}")
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(e.message ?: "An error occurred")
            }
        }
    }
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val token: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
} 