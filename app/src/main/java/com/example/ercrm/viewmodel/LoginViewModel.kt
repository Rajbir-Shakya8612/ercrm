package com.example.ercrm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ercrm.data.model.LoginRequest
import com.example.ercrm.data.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    init {
        // Check for existing token on initialization
        repository.getCurrentToken()?.let { token ->
            _loginState.value = LoginState.Success(token)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = repository.login(email, password)
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        _loginState.value = LoginState.Success(loginResponse.token)
                    } ?: run {
                        _loginState.value = LoginState.Error("Empty response body")
                    }
                } else {
                    _loginState.value = LoginState.Error("Login failed: ${response.code()}")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = repository.logout()
                if (response.isSuccessful) {
                    repository.clearToken()
                    _loginState.value = LoginState.LoggedOut
                } else {
                    _loginState.value = LoginState.Error("Logout failed: ${response.code()}")
                }
            } catch (e: Exception) {
                if (e is IllegalStateException) {
                    // If we're not logged in, just clear state and proceed
                    repository.clearToken()
                    _loginState.value = LoginState.LoggedOut
                } else {
                    _loginState.value = LoginState.Error(e.message ?: "An error occurred")
                }
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String) : LoginState()
    data class Error(val message: String) : LoginState()
    object LoggedOut : LoginState()
} 