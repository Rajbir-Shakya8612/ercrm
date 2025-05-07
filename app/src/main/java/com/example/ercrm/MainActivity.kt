package com.example.ercrm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import com.example.ercrm.ui.screens.*
import com.example.ercrm.ui.theme.ERCRMTheme
import com.example.ercrm.viewmodel.LoginState
import com.example.ercrm.viewmodel.LoginViewModel
import com.example.ercrm.worker.FollowUpCheckWorker
import com.example.ercrm.worker.NotificationWorker
import dagger.hilt.android.AndroidEntryPoint
import androidx.work.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private val workManager by lazy { WorkManager.getInstance(applicationContext) }
    private var navController: NavHostController? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        Log.d(TAG, "Permission result: $allGranted")
        if (allGranted) {
            // All permissions granted, start workers
            startFollowUpCheckWorker()
            startNotificationWorker()
        } else {
            // Show a message that notifications won't work without permissions
            Log.w(TAG, "Required permissions not granted")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request required permissions
        requestRequiredPermissions()

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

    private fun requestRequiredPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.SCHEDULE_EXACT_ALARM,
            Manifest.permission.USE_EXACT_ALARM
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notGrantedPermissions = permissions.filter { permission ->
                ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
            }

            if (notGrantedPermissions.isEmpty()) {
                Log.d(TAG, "All permissions already granted")
                // All permissions already granted, start workers
                startFollowUpCheckWorker()
                startNotificationWorker()
            } else {
                Log.d(TAG, "Requesting permissions: $notGrantedPermissions")
                // Request permissions
                requestPermissionLauncher.launch(notGrantedPermissions.toTypedArray())
            }
        } else {
            Log.d(TAG, "Android version < 13, starting workers directly")
            // For Android 12 and below, start workers directly
            startFollowUpCheckWorker()
            startNotificationWorker()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "New intent received: ${intent.action}")
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent) {
        val screen = intent.getStringExtra("screen")
        val leadId = intent.getIntExtra("lead_id", -1)
        Log.d(TAG, "Handling notification intent: screen=$screen, leadId=$leadId")

        when {
            screen == "lead_details" && leadId != -1 -> {
                Log.d(TAG, "Navigating to lead details: $leadId")
                navController?.navigate("lead_details/$leadId")
            }
            screen == "follow_ups" -> {
                Log.d(TAG, "Navigating to follow-ups screen")
                navController?.navigate("follow_ups")
            }
            else -> {
                Log.w(TAG, "No valid screen found in notification")
            }
        }
    }

    private fun startFollowUpCheckWorker() {
        Log.d(TAG, "Starting follow-up check worker")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val followUpCheckRequest = PeriodicWorkRequestBuilder<FollowUpCheckWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()
        Log.d(TAG, "Enqueueing follow-up check worker")
        workManager.enqueueUniquePeriodicWork(
            "follow_up_check",
            ExistingPeriodicWorkPolicy.UPDATE,
            followUpCheckRequest
        ).also {
            Log.d(TAG, "Follow-up check worker enqueued: $it")
        }
    }

    private fun startNotificationWorker() {
        Log.d(TAG, "Starting notification worker")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val notificationRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()
        Log.d(TAG, "Enqueueing notification worker")
        workManager.enqueueUniquePeriodicWork(
            "notification_work",
            ExistingPeriodicWorkPolicy.UPDATE,
            notificationRequest
        ).also {
            Log.d(TAG, "Notification worker enqueued: $it")
        }
    }
}
