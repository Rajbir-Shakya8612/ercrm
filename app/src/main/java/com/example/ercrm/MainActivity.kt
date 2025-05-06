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
import androidx.navigation.NavHostController
import com.example.ercrm.ui.screens.NewDashboardScreen
import com.example.ercrm.ui.screens.LoginScreen
import com.example.ercrm.ui.screens.RegisterScreen
import com.example.ercrm.ui.screens.AttendanceDashboardScreen
import com.example.ercrm.ui.screens.LeadsDashboardScreen
import com.example.ercrm.ui.screens.FollowUpsScreen
import com.example.ercrm.ui.screens.LeadDetailsScreen
import com.example.ercrm.ui.theme.ERCRMTheme
import com.example.ercrm.viewmodel.LoginState
import com.example.ercrm.viewmodel.LoginViewModel
import com.example.ercrm.worker.FollowUpCheckWorker
import com.example.ercrm.worker.NotificationWorker
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
    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Start follow-up check worker
        startFollowUpCheckWorker()
        
        // Start notification worker
        startNotificationWorker()

        setContent {
            ERCRMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: LoginViewModel = hiltViewModel()
                    val loginState by viewModel.loginState.collectAsState()
                    var showRegister by remember { mutableStateOf(false) }
                    val nav = rememberNavController()
                    navController = nav
                    when {
                        showRegister -> {
                            RegisterScreen(
                                onNavigateToLogin = { showRegister = false },
                                onRegisterSuccess = { showRegister = false }
                            )
                        }
                        loginState is LoginState.Success -> {
                            key(loginState) {
                                NavHost(navController = nav, startDestination = "new_dashboard") {
                                    composable("new_dashboard") {
                                        NewDashboardScreen(nav)
                                    }
                                    composable("attendance_dashboard") {
                                        AttendanceDashboardScreen(nav)
                                    }
                                    composable("leads_dashboard") {
                                        LeadsDashboardScreen(nav)
                                    }
                                    composable("follow_ups") {
                                        FollowUpsScreen(nav)
                                    }
                                    composable("lead_details/{leadId}") { backStackEntry ->
                                        val leadId = backStackEntry.arguments?.getString("leadId")?.toIntOrNull()
                                        if (leadId != null) {
                                            LeadDetailsScreen(nav, leadId)
                                        }
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
        // Handle notification intent after navController is set
        handleNotificationIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent) {
        val screen = intent.getStringExtra("screen")
        val leadId = intent.getIntExtra("lead_id", -1)
        if (screen == "lead_details" && leadId != -1) {
            navController?.navigate("lead_details/$leadId")
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

    private fun startNotificationWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val notificationRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "notification_work",
            ExistingPeriodicWorkPolicy.KEEP,
            notificationRequest
        )
    }
}