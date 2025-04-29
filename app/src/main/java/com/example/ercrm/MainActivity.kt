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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ercrm.ui.screens.LoginScreen
import com.example.ercrm.ui.screens.WelcomeScreen
import com.example.ercrm.ui.theme.ERCRMTheme
import com.example.ercrm.viewmodel.LoginState
import com.example.ercrm.viewmodel.LoginViewModel

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
                    val viewModel: LoginViewModel = viewModel()
                    val loginState by viewModel.loginState.collectAsState()
                    
                    when (loginState) {
                        is LoginState.Success -> {
                            WelcomeScreen(
                                onLogout = { viewModel.logout() }
                            )
                        }
                        is LoginState.LoggedOut, 
                        is LoginState.Idle,
                        is LoginState.Error -> {
                            LoginScreen(
                                onLoginSuccess = { email, password ->
                                    viewModel.login(email, password)
                                }
                            )
                        }
                        is LoginState.Loading -> {
                            // You could show a loading indicator here if needed
                        }
                    }
                }
            }
        }
    }
}