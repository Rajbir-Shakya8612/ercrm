package com.example.ercrm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ercrm.ui.screens.DashboardScreen
import com.example.ercrm.ui.screens.LoginScreen
import com.example.ercrm.ui.screens.RegisterScreen
import com.example.ercrm.ui.theme.ERCRMTheme
import com.example.ercrm.viewmodel.LoginState
import com.example.ercrm.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ERCRMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: LoginViewModel = hiltViewModel()
                    val loginState by viewModel.loginState.collectAsState()
                    var showRegister by remember { mutableStateOf(false) }
                    
                    when {
                        showRegister -> {
                            RegisterScreen(
                                onNavigateToLogin = { showRegister = false },
                                onRegisterSuccess = { showRegister = false }
                            )
                        }
                        loginState is LoginState.Success -> {
                            DashboardScreen(
                                onNavigateToLeads = { /* TODO: Navigate to Leads */ },
                                onNavigateToTasks = { /* TODO: Navigate to Tasks */ },
                                onNavigateToMeetings = { /* TODO: Navigate to Meetings */ }
                            )
                        }
                        loginState is LoginState.LoggedOut || 
                        loginState is LoginState.Idle ||
                        loginState is LoginState.Error -> {
                            LoginScreen(
                                onLoginSuccess = { email, password ->
                                    viewModel.login(email, password)
                                },
                                onNavigateToRegister = { showRegister = true }
                            )
                        }
                        loginState is LoginState.Loading -> {
                            // You could show a loading indicator here if needed
                        }
                    }
                }
            }
        }
    }
}