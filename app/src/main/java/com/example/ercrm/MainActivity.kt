package com.example.ercrm

import android.content.Intent
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
import com.example.ercrm.ui.screens.AttendanceDashboardScreen
import com.example.ercrm.ui.screens.LeadsDashboardScreen
import com.example.ercrm.ui.screens.FollowUpsScreen
import com.example.ercrm.ui.theme.ERCRMTheme
import com.example.ercrm.viewmodel.LoginState
import com.example.ercrm.viewmodel.LoginViewModel
import com.example.ercrm.worker.FollowUpCheckWorker
import dagger.hilt.android.AndroidEntryPoint
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.NetworkType
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val workManager by lazy { WorkManager.getInstance(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Start follow-up check worker
        startFollowUpCheckWorker()

        // Handle notification intent
        handleNotificationIntent(intent)

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
                                NavHost(navController = navController, startDestination = "new_dashboard") {
                                    composable("new_dashboard") {
                                        NewDashboardScreen(navController)
                                    }
                                    composable("attendance_dashboard") {
                                        AttendanceDashboardScreen(navController)
                                    }
                                    composable("leads_dashboard") {
                                        LeadsDashboardScreen(navController)
                                    }
                                    composable("follow_ups") {
                                        FollowUpsScreen(navController)
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent) {
        if (intent.getStringExtra("screen") == "follow_ups") {
            val leadId = intent.getIntExtra("lead_id", -1)
            if (leadId != -1) {
                // Navigate to follow-ups screen
                // Note: You'll need to implement navigation handling here
            }
        }
    }

    private fun startFollowUpCheckWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val followUpCheckRequest = PeriodicWorkRequestBuilder<FollowUpCheckWorker>(
            1, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "follow_up_check",
            ExistingPeriodicWorkPolicy.KEEP,
            followUpCheckRequest
        )
    }
}