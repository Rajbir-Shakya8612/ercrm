package com.example.ercrm.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ercrm.data.model.Role
import com.example.ercrm.data.repository.RegisterRepository
import com.example.ercrm.data.repository.RoleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerRepository: RegisterRepository,
    private val roleRepository: RoleRepository
) : ViewModel() {
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    private val _roles = MutableStateFlow<List<Role>>(emptyList())
    val roles: StateFlow<List<Role>> = _roles

    private val _selectedRole = MutableStateFlow<Role?>(null)
    val selectedRole: StateFlow<Role?> = _selectedRole

    private val _isLoadingRoles = MutableStateFlow(false)
    val isLoadingRoles: StateFlow<Boolean> = _isLoadingRoles

    private val _rolesError = MutableStateFlow<String?>(null)
    val rolesError: StateFlow<String?> = _rolesError

    init {
        fetchRoles()
    }

    fun fetchRoles() {
        if (_isLoadingRoles.value) return

        _isLoadingRoles.value = true
        _rolesError.value = null
        
        viewModelScope.launch {
            try {
                val response = roleRepository.getRoles()
                if (response.isSuccessful) {
                    response.body()?.let { rolesList ->
                        if (rolesList.isEmpty()) {
                            _rolesError.value = "No roles available"
                            _roles.value = emptyList()
                            _selectedRole.value = null
                        } else {
                            _roles.value = rolesList.sortedBy { it.name }
                            if (_selectedRole.value == null) {
                                _selectedRole.value = rolesList.firstOrNull()
                            }
                            _rolesError.value = null
                        }
                    } ?: run {
                        _rolesError.value = "Failed to load roles"
                        _roles.value = emptyList()
                        _selectedRole.value = null
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        404 -> "Roles service not found"
                        401 -> "Unauthorized access"
                        else -> "Error loading roles: ${response.code()}"
                    }
                    _rolesError.value = errorMessage
                    _roles.value = emptyList()
                    _selectedRole.value = null
                    Log.e("RegisterViewModel", "Error code: ${response.code()}")
                }
            } catch (e: Exception) {
                _rolesError.value = "Failed to load roles. Please try again."
                _roles.value = emptyList()
                _selectedRole.value = null
                Log.e("RegisterViewModel", "Exception in fetching roles", e)
            } finally {
                _isLoadingRoles.value = false
            }
        }
    }

    fun setSelectedRole(roleId: Int) {
        val role = _roles.value.find { it.id == roleId }
        if (role != null) {
            _selectedRole.value = role
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        brandPasskey: String,
        selectedRole: Int
    ) {
        // Validate password match
        if (password != confirmPassword) {
            _registerState.value = RegisterState.Error("Passwords do not match")
            return
        }

        // Validate passkey length
        if (brandPasskey.length != 6) {
            _registerState.value = RegisterState.Error("Brand passkey must be 6 characters long")
            return
        }

        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val response = registerRepository.register(
                    name = name,
                    email = email,
                    password = password,
                    password_confirmation = confirmPassword,
                    role_id = selectedRole,
                    passkey = brandPasskey
                )
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    if (token != null) {
                        _registerState.value = RegisterState.Success(token)
                    } else {
                        _registerState.value = RegisterState.Error("Empty response")
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid registration data"
                        409 -> "Email already exists"
                        422 -> {
                            // Handle validation errors
                            val errorBody = response.errorBody()?.string()
                            if (errorBody?.contains("passkey") == true) {
                                "Invalid brand passkey"
                            } else {
                                "Validation error: Please check your input"
                            }
                        }
                        else -> "Registration failed: ${response.code()}"
                    }
                    _registerState.value = RegisterState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(e.localizedMessage ?: "An error occurred")
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
