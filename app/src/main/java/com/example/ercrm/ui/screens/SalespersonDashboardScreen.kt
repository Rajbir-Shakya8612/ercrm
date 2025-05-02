package com.example.ercrm.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ercrm.ui.components.*
import com.example.ercrm.viewmodel.DashboardState
import com.example.ercrm.viewmodel.SalespersonDashboardViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalespersonDashboardScreen(
    onNavigateToLeadDetails: (Int) -> Unit,
    onNavigateToMeetingDetails: (Int) -> Unit,
    onNavigateToTaskDetails: (Int) -> Unit,
    viewModel: SalespersonDashboardViewModel = hiltViewModel()
) {
    val dashboardState by viewModel.dashboardState.collectAsState()
    val error by viewModel.error.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Salesperson Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (dashboardState) {
                is DashboardState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                    )
                }
                is DashboardState.Success -> {
                    val data = (dashboardState as DashboardState.Success).data
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp)
                    ) {
                        // Calendar Section
                        CalendarSection(
                            selectedDate = LocalDate.now(),
                            events = data.events,
                            onDateSelected = { /* Handle date selection */ }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Overview Section
                        OverviewSection(
                            totalLeads = data.totalLeads,
                            monthlySales = data.monthlySales,
                            todayMeetings = data.todayMeetings,
                            targetAchievement = data.targetAchievement
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Performance Section
                        PerformanceSection(
                            performanceData = data.performanceData,
                            plansData = data.plansData
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Attendance Section
                        AttendanceSection(
                            attendance = data.attendance,
                            onCheckIn = { location -> viewModel.checkIn(location) },
                            onCheckOut = { location -> viewModel.checkOut(location) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Leads Section
                        LeadsSection(
                            leadStatuses = data.leadStatuses,
                            onLeadClick = onNavigateToLeadDetails
                        )
                    }
                }
                is DashboardState.Error -> {
                    Text(
                        text = (dashboardState as DashboardState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(androidx.compose.ui.Alignment.Center)
                            .padding(16.dp)
                    )
                }
            }

            // Show error message if any
            error?.let { errorMessage ->
                Snackbar(
                    modifier = Modifier
                        .align(androidx.compose.ui.Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(errorMessage)
                }
            }
        }
    }
} 