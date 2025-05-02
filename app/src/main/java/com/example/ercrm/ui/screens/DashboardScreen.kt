package com.example.ercrm.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ercrm.viewmodel.DashboardViewModel
import com.example.ercrm.viewmodel.DashboardState
import com.example.ercrm.data.model.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import androidx.activity.compose.rememberLauncherForActivityResult
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import com.example.ercrm.data.model.Activity

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(
    onNavigateToLeads: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToMeetings: () -> Unit,
    userRole: String = "salesperson"
) {
    val viewModel: DashboardViewModel = hiltViewModel()
    val dashboardState by viewModel.dashboardState.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Request location permission
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Location permission is required for attendance", Toast.LENGTH_LONG).show()
        }
    }

    // Load dashboard data when screen is first displayed
    LaunchedEffect(userRole) {
        viewModel.loadDashboard()
    }

    // Show error if any
    error?.let { errorMessage ->
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Calendar Section
        CalendarSection()
        
        Spacer(modifier = Modifier.height(16.dp))

        // Dashboard Content
        when (dashboardState) {
            is DashboardState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            is DashboardState.Success -> {
                val data = (dashboardState as DashboardState.Success).data
                
                // Overview Section
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Metrics Cards in pairs
                val metrics = getMetricsData(data)
                for (i in metrics.indices step 2) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // First card
                        MetricCard(
                            title = metrics[i].title,
                            value = metrics[i].value,
                            icon = metrics[i].icon,
                            color = metrics[i].color,
                            onClick = when (metrics[i].title) {
                                "Leads" -> onNavigateToLeads
                                "Tasks" -> onNavigateToTasks
                                "Meetings" -> onNavigateToMeetings
                                else -> ({})
                            },
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Second card (if exists)
                        if (i + 1 < metrics.size) {
                            MetricCard(
                                title = metrics[i + 1].title,
                                value = metrics[i + 1].value,
                                icon = metrics[i + 1].icon,
                                color = metrics[i + 1].color,
                                onClick = when (metrics[i + 1].title) {
                                    "Leads" -> onNavigateToLeads
                                    "Tasks" -> onNavigateToTasks
                                    "Meetings" -> onNavigateToMeetings
                                    else -> ({})
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Performance Section
                data.performanceData?.let { performanceData ->
                    PerformanceSection(performanceData)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Attendance Section
                data.attendance?.let { attendance ->
                    AttendanceSection(
                        attendance = attendance,
                        onCheckIn = { location ->
                            viewModel.checkIn(location)
                        },
                        onCheckOut = { location ->
                            viewModel.checkOut(location)
                        },
                        locationPermissionLauncher = locationPermissionLauncher,
                        fusedLocationClient = fusedLocationClient
                    )
                }

                // Recent Activities
                if (data.recentActivities.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    RecentActivitiesSection(activities = data.recentActivities)
                }
            }
            is DashboardState.Error -> {
                Text(
                    text = (dashboardState as DashboardState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun CalendarSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "Calendar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            // Simple calendar grid (you can replace this with a more sophisticated calendar library)
            val currentMonth = YearMonth.now()
            val daysInMonth = currentMonth.lengthOfMonth()
            val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value

            // Calendar header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar days
            for (i in 0 until 6) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (j in 1..7) {
                        val day = i * 7 + j - firstDayOfMonth + 1
                        if (day in 1..daysInMonth) {
                            Text(
                                text = day.toString(),
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(4.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            Spacer(modifier = Modifier.size(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: @Composable () -> Unit,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            icon()
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun PerformanceSection(performanceData: PerformanceData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Performance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            performanceData.achievements?.let { achievements ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Lead Achievement",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${achievements.leads}%",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column {
                        Text(
                            text = "Sales Achievement",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${achievements.sales}%",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceSection(
    attendance: AttendanceData,
    onCheckIn: (Location) -> Unit,
    onCheckOut: (Location) -> Unit,
    locationPermissionLauncher: ActivityResultLauncher<String>,
    fusedLocationClient: FusedLocationProviderClient
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Attendance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Check In Time",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = attendance.check_in_time?.let { formatDateTime(it) } ?: "--:--",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column {
                    Text(
                        text = "Check Out Time",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = attendance.check_out_time?.let { formatDateTime(it) } ?: "--:--",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = attendance.status?.capitalize() ?: "Not Available",
                        style = MaterialTheme.typography.titleMedium,
                        color = when(attendance.status) {
                            "present" -> Color(0xFF4CAF50)
                            "late" -> Color(0xFFFFA000)
                            "absent" -> Color(0xFFE91E63)
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
                if (attendance.working_hours != null) {
                    Column {
                        Text(
                            text = "Working Hours",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = attendance.working_hours,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val context = LocalContext.current
            val isCheckedIn = attendance.check_in_time != null && attendance.check_out_time == null

            Button(
                onClick = {
                    if (isCheckedIn) {
                        requestLocationAndPerformAction(
                            context = context,
                            locationPermissionLauncher = locationPermissionLauncher,
                            fusedLocationClient = fusedLocationClient,
                            action = onCheckOut
                        )
                    } else if (attendance.check_out_time == null) {
                        requestLocationAndPerformAction(
                            context = context,
                            locationPermissionLauncher = locationPermissionLauncher,
                            fusedLocationClient = fusedLocationClient,
                            action = onCheckIn
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = attendance.check_out_time == null
            ) {
                Text(
                    when {
                        attendance.check_out_time != null -> "Day Completed"
                        isCheckedIn -> "Check Out"
                        else -> "Check In"
                    }
                )
            }
        }
    }
}

@Composable
fun RecentActivitiesSection(activities: List<Activity>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Recent Activities",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            activities.take(5).forEach { activity ->
                ActivityItem(activity)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ActivityItem(activity: Activity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = activity.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = activity.details,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = formatDateTime(activity.created_at),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatDateTime(dateTime: String): String {
    return try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val localDateTime = java.time.LocalDateTime.parse(dateTime, formatter)
        localDateTime.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"))
    } catch (e: Exception) {
        dateTime
    }
}

private fun requestLocationAndPerformAction(
    context: Context,
    locationPermissionLauncher: ActivityResultLauncher<String>,
    fusedLocationClient: FusedLocationProviderClient,
    action: (Location) -> Unit
) {
    when {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED -> {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let { action(it) }
            }
        }
        else -> {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}


private fun getMetricsData(data: DashboardData): List<MetricCardData> {
    return listOf(
        MetricCardData(
            title = "Leads",
            value = data.totalLeads.toString(),
            icon = { 
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Leads",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(24.dp)
                )
            },
            color = Color(0xFF2196F3)
        ),
        MetricCardData(
            title = "Sales",
            value = "₹${String.format("%,.2f", data.monthlySales)}",
            icon = { 
                Icon(
                    Icons.Filled.AttachMoney,
                    contentDescription = "Sales",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(24.dp)
                )
            },
            color = Color(0xFF4CAF50)
        ),
        MetricCardData(
            title = "Meetings",
            value = data.todayMeetings.toString(),
            icon = { 
                Icon(
                    Icons.Filled.Event,
                    contentDescription = "Meetings",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(24.dp)
                )
            },
            color = Color(0xFFFFA000)
        ),
        MetricCardData(
            title = "Target",
            value = "${data.targetAchievement}%",
            icon = { 
                Icon(
                    Icons.Filled.Timeline,
                    contentDescription = "Target",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(24.dp)
                )
            },
            color = Color(0xFFE91E63)
        )
    )
}

data class MetricCardData(
    val title: String,
    val value: String,
    val icon: @Composable () -> Unit,
    val color: Color
) 