package com.example.ercrm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ercrm.ui.screens.NewDashboardScreen
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
                    val navController = rememberNavController()
                    
                    when {
                        showRegister -> {
                            RegisterScreen(
                                onNavigateToLogin = { showRegister = false },
                                onRegisterSuccess = { showRegister = false }
                            )
                        }
                        loginState is LoginState.Success -> {
                            key(loginState) {
                                NavHost(navController = navController, startDestination = "dashboard") {
                                    composable("dashboard") {
                                        NewDashboardScreen(navController = navController)
                                    }
                                    composable("attendance_dashboard") {
                                        NewDashboardScreen(navController = navController)
                                    }
                                }
                            }
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
                            Box(modifier = Modifier.fillMaxSize()) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}