package com.example.ercrm.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.example.ercrm.R
import com.google.accompanist.pager.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ercrm.data.model.LocationData
import com.example.ercrm.viewmodel.AttendanceViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import com.example.ercrm.utils.LocationHelper
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import dagger.hilt.android.EntryPointAccessors
import com.example.ercrm.ui.components.CalendarSection
import java.time.LocalDate
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ercrm.viewmodel.LeadsViewModel
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.DisposableEffectScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.filled.ArrowDropDown

@Composable
fun NewDashboardScreen(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedBottom by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(initialPage = 0)

    Scaffold(
        bottomBar = {
            BottomNavigationBar(selected = selectedBottom) { selectedBottom = it }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // 🔍 Search Bar
            SearchBar()

            Spacer(modifier = Modifier.height(20.dp))

            // 📱 Card Carousel
            HorizontalPager(
                count = 2,
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 32.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) { index ->
                DashboardCard(index)
            }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalPagerIndicator(
                pagerState = pagerState,
                activeColor = OrangePrimary,
                inactiveColor = Color(0xFFE9EEF6),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 🧭 Tabs + Grid Content
            DashboardTabs(selectedTab) { selectedTab = it }
            Spacer(modifier = Modifier.height(18.dp))
            DashboardGrid(
                onAttendanceClick = { navController.navigate("attendance_dashboard") },
                onLeadsClick = { navController.navigate("leads_dashboard") }
            )
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
fun SearchBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFE9EEF6))
            .height(48.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFB0B7C3), modifier = Modifier.padding(start = 18.dp))
        Text(
            text = "What would you like to do today?",
            color = Color(0xFFB0B7C3),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 52.dp)
        )
    }
}

@Composable
fun DashboardCard(index: Int) {
    val gradients = listOf(
        listOf(Color(0xFFFF6F00), Color(0xFFFFA726)),
        listOf(Color(0xFFFF6F00), Color(0xFFFFC371))
    )

    val gradient = gradients[index % gradients.size]

    Box(
        modifier = Modifier
            .fillMaxWidth(0.88f)
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(gradient))
            .padding(18.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text("ICICI Bank", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("2300 1130 5224", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Account No.", color = Color.White, fontSize = 13.sp)
                Button(
                    onClick = { /* TODO */ },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text("Show Balance", color = Color.White, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun DashboardTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("Home", "Shop", "Discover")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        tabs.forEachIndexed { index, label ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (selectedTab == index) Color(0xFFFFF3E0) else Color.Transparent)
                    .clickable { onTabSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 16.sp,
                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                    color = if (selectedTab == index) OrangePrimary else Color(0xFFB0B7C3),
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
fun DashboardGrid(onAttendanceClick: () -> Unit, onLeadsClick: () -> Unit) {
    val gridItems = listOf(
        Triple(Icons.Default.Fingerprint, "Attendance", onAttendanceClick),
        Triple(Icons.Default.Receipt, "Leads", onLeadsClick),
        Triple(Icons.Default.BarChart, "Sales", {}),
        Triple(Icons.Default.AccountBalance, "Plans", {}),
        Triple(Icons.Default.Event, "Meetings", {}),
        Triple(Icons.Default.CheckCircle, "Tasks", {}),
        Triple(Icons.Default.AccountBalanceWallet, "Point Wallet", {}),
        Triple(Icons.Default.QrCode, "Primary Sales", {}),
        Triple(Icons.Default.ShoppingCart, "Secondary Sales", {})
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .height(320.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false
    ) {
        items(gridItems) { (icon, label, onClick) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .padding(18.dp)
                        .clickable { onClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = label, tint = OrangePrimary, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(label, fontSize = 13.sp, color = Color(0xFF2E5AAC), fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(selected: Int, onSelect: (Int) -> Unit) {
    val items = listOf(
        Pair(Icons.Default.Home, "Home"),
        Pair(Icons.Default.Star, "Favorites"),
        Pair(Icons.Default.Dashboard, "Dashboard"),
        Pair(Icons.Default.LocalOffer, "Offers"),
        Pair(Icons.Default.Person, "Profile")
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, (icon, label) ->
            NavigationBarItem(
                selected = selected == index,
                onClick = { onSelect(index) },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected == index) OrangePrimary else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = label, tint = if (selected == index) Color.White else Color(0xFFB0B7C3))
                    }
                },
                label = {
                    Text(
                        label,
                        fontSize = 11.sp,
                        fontWeight = if (selected == index) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected == index) OrangePrimary else Color(0xFFB0B7C3)
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}

private val OrangePrimary = Color(0xFFFF6F00)

@Composable
fun AttendanceDashboardScreen(
    navController: NavHostController,
    viewModel: AttendanceViewModel = hiltViewModel()
) {
    val attendanceState by viewModel.attendanceState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var showLocationPermissionDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    val canCheckIn by viewModel.canCheckIn.collectAsState()
    val canCheckOut by viewModel.canCheckOut.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val locationHelper = EntryPointAccessors.fromApplication(
        context.applicationContext,
        com.example.ercrm.utils.LocationHelperEntryPoint::class.java
    ).locationHelper()

    LaunchedEffect(Unit) {
        // Request location permission when screen opens
        showLocationPermissionDialog = true
    }

    if (showLocationPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showLocationPermissionDialog = false },
            title = { Text("Location Permission Required") },
            text = { Text("This app needs location permission to record your attendance. Please grant location permission to continue.") },
            confirmButton = {
                TextButton(onClick = { showLocationPermissionDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    if (showErrorDialog && error != null) {
        AlertDialog(
            onDismissRequest = {
                showErrorDialog = false
                viewModel.clearError()
            },
            title = { Text("Error") },
            text = { Text(error ?: "An error occurred") },
            confirmButton = {
                TextButton(onClick = {
                    showErrorDialog = false
                    viewModel.clearError()
                }) {
                    Text("OK")
                }
            }
        )
    }

    LaunchedEffect(error) {
        if (error != null) {
            showErrorDialog = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Attendance",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Date Display
        Text(
            text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (attendanceState.isCheckedIn) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = attendanceState.status ?: if (attendanceState.isCheckedIn) "You are Checked In" else "You are not Checked In",
                    fontSize = 18.sp,
                    color = if (attendanceState.isCheckedIn) Color(0xFF388E3C) else Color(0xFFD32F2F),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Check In/Out Button
        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        val location = locationHelper.getLastLocation()
                        val locData = LocationData(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            accuracy = location.accuracy,
                            type = if (canCheckIn) "check_in" else "check_out",
                            tracked_at = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                        )
                        if (canCheckIn) {
                            viewModel.checkIn(locData, context)
                        } else if (canCheckOut) {
                            viewModel.checkOut(locData, context)
                        }
                    } catch (e: SecurityException) {
                        showLocationPermissionDialog = true
                    } catch (e: Exception) {
                        showErrorDialog = true
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = when {
                    canCheckIn -> OrangePrimary
                    canCheckOut -> Color(0xFFD32F2F)
                    else -> Color.LightGray
                }
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = (!isLoading && (canCheckIn || canCheckOut))
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text(
                    when {
                        canCheckIn -> "Check In"
                        canCheckOut -> "Check Out"
                        else -> "Attendance Complete"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Time Records
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                attendanceState.check_in_time?.let {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Check In Time:", fontSize = 16.sp, color = Color.Gray)
                        Text(formatToKolkataTime(it), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                attendanceState.check_out_time?.let {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Check Out Time:", fontSize = 16.sp, color = Color.Gray)
                        Text(formatToKolkataTime(it), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
                attendanceState.working_hours?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Working Hours:", fontSize = 16.sp, color = Color.Gray)
                        Text(it, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Calendar Section
        Spacer(modifier = Modifier.height(12.dp))
        var selectedDate by remember { mutableStateOf(java.time.LocalDate.now()) }
        com.example.ercrm.ui.components.CalendarSection(
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it }
        )
    }
}

fun formatToKolkataTime(isoString: String?): String {
    return try {
        if (isoString.isNullOrBlank()) return ""
        val zonedDateTime = ZonedDateTime.parse(isoString)
        val kolkataTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
        kolkataTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
    } catch (e: Exception) {
        isoString ?: ""
    }
}

@Composable
fun LeadsDashboardScreen(navController: NavHostController, leadsViewModel: LeadsViewModel = hiltViewModel()) {
    val leadsState by leadsViewModel.leadsState.collectAsState()
    val isLoading by leadsViewModel.isLoading.collectAsState()
    val error by leadsViewModel.error.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showLeadDialog by remember { mutableStateOf(false) }
    var dialogStatusId by remember { mutableStateOf<Int?>(null) }
    var dialogLead by remember { mutableStateOf<com.example.ercrm.data.model.Lead?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }

    // Fetch leads on first load
    LaunchedEffect(Unit) { leadsViewModel.fetchLeads() }
    LaunchedEffect(error) { if (error != null) showErrorDialog = true }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(8.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Leads Dashboard", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (leadsState == null) {
                Text("No data found", color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                // Kanban Board
                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    leadsState?.lead_statuses?.forEach { status ->
                        val leadsForStatus = leadsState?.leads?.filter { it.status_id == status.id } ?: emptyList()
                        Column(
                            Modifier
                                .width(340.dp)
                                .padding(12.dp)
                                .background(
                                    color = status.color?.let { Color(android.graphics.Color.parseColor(it)) } ?: Color(0xFFF5F5F5),
                                    shape = RoundedCornerShape(20.dp)
                                )
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(status.name, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.Black, modifier = Modifier.padding(12.dp))
                                }
                                Spacer(Modifier.weight(1f))
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(Color.White, shape = CircleShape)
                                        .border(2.dp, OrangePrimary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(leadsForStatus.size.toString(), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = OrangePrimary)
                                }
                                IconButton(onClick = {
                                    dialogStatusId = status.id
                                    dialogLead = null
                                    showLeadDialog = true
                                }, modifier = Modifier.size(38.dp)) {
                                    Icon(Icons.Default.Add, contentDescription = "Add Lead", tint = OrangePrimary, modifier = Modifier.size(28.dp))
                                }
                            }
                            if (leadsForStatus.isNotEmpty()) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState())
                                ) {
                                    leadsForStatus.forEach { lead ->
                                        Card(
                                            modifier = Modifier
                                                .width(300.dp)
                                                .height(280.dp)
                                                .padding(bottom = 16.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color.White),
                                            elevation = CardDefaults.cardElevation(8.dp)
                                        ) {
                                            Column(Modifier.padding(20.dp)) {
                                                Text(lead.name, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                                                Spacer(Modifier.height(10.dp))
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF388E3C), modifier = Modifier.size(26.dp))
                                                    Spacer(Modifier.width(8.dp))
                                                    Text(lead.phone, fontSize = 18.sp)
                                                }
                                                if (!lead.email.isNullOrBlank()) {
                                                    Spacer(Modifier.height(8.dp))
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(26.dp))
                                                        Spacer(Modifier.width(8.dp))
                                                        Text(lead.email, fontSize = 18.sp)
                                                    }
                                                }
                                                if (!lead.address.isNullOrBlank()) {
                                                    Spacer(Modifier.height(8.dp))
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.Home, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(26.dp))
                                                        Spacer(Modifier.width(8.dp))
                                                        Text(lead.address, fontSize = 18.sp)
                                                    }
                                                }
                                                if (!lead.follow_up_date.isNullOrBlank()) {
                                                    Spacer(Modifier.height(8.dp))
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(26.dp))
                                                        Spacer(Modifier.width(8.dp))
                                                        val formattedDate = try {
                                                            java.time.LocalDate.parse(lead.follow_up_date.substring(0,10)).format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                                        } catch (e: Exception) { lead.follow_up_date }
                                                        Text("Follow-up: $formattedDate", fontSize = 18.sp, color = Color(0xFFD32F2F))
                                                    }
                                                }
                                                Spacer(Modifier.height(20.dp))
                                                Row(
                                                    Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    IconButton(
                                                        onClick = {
                                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${lead.phone}"))
                                                            context.startActivity(intent)
                                                        },
                                                        modifier = Modifier.size(54.dp)
                                                    ) {
                                                        Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color(0xFF388E3C), modifier = Modifier.size(32.dp))
                                                    }
                                                    IconButton(
                                                        onClick = {
                                                            val url = "https://wa.me/${lead.phone}"
                                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                                            context.startActivity(intent)
                                                        },
                                                        modifier = Modifier.size(54.dp)
                                                    ) {
                                                        Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = Color(0xFF25D366), modifier = Modifier.size(32.dp))
                                                    }
                                                    IconButton(
                                                        onClick = {
                                                            dialogLead = lead
                                                            dialogStatusId = lead.status_id
                                                            showLeadDialog = true
                                                        },
                                                        modifier = Modifier.size(54.dp)
                                                    ) {
                                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF1976D2), modifier = Modifier.size(32.dp))
                                                    }
                                                    IconButton(
                                                        onClick = { leadsViewModel.deleteLead(lead.id) },
                                                        modifier = Modifier.size(54.dp)
                                                    ) {
                                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD32F2F), modifier = Modifier.size(32.dp))
                                                    }
                                                    IconButton(
                                                        onClick = {
                                                            if (lead.latitude != null && lead.longitude != null) {
                                                                val gmmIntentUri = Uri.parse("geo:${lead.latitude},${lead.longitude}?q=${lead.latitude},${lead.longitude}")
                                                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                                                mapIntent.setPackage("com.google.android.apps.maps")
                                                                context.startActivity(mapIntent)
                                                            }
                                                        },
                                                        modifier = Modifier.size(54.dp)
                                                    ) {
                                                        Icon(Icons.Default.LocationOn, contentDescription = "View Location", tint = Color(0xFFF57C00), modifier = Modifier.size(38.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // Add new dialog usage
        if (showLeadDialog) {
            AddLeadDialog(
                statuses = leadsState?.lead_statuses ?: emptyList(),
                onAdd = { leadReq ->
                    coroutineScope.launch {
                        val locationHelper = EntryPointAccessors.fromApplication(
                            context.applicationContext,
                            com.example.ercrm.utils.LocationHelperEntryPoint::class.java
                        ).locationHelper()
                        val location = locationHelper.getLastLocation()
                        if (dialogLead == null) {
                            leadsViewModel.createLead(
                                leadReq.copy(
                                    latitude = location.latitude,
                                    longitude = location.longitude
                                )
                            ) { showLeadDialog = false; dialogLead = null; dialogStatusId = null }
                        } else {
                            leadsViewModel.updateLead(
                                dialogLead!!.id,
                                leadReq.copy(
                                    latitude = location.latitude,
                                    longitude = location.longitude
                                )
                            ) { showLeadDialog = false; dialogLead = null; dialogStatusId = null }
                        }
                    }
                },
                onDismiss = { showLeadDialog = false; dialogLead = null; dialogStatusId = null },
                initialStatusId = dialogStatusId,
                initialLead = dialogLead
            )
        }
        // Error Dialog
        if (showErrorDialog && error != null) {
            AlertDialog(
                onDismissRequest = {
                    showErrorDialog = false
                    leadsViewModel.clearError()
                },
                title = { Text("Error") },
                text = { Text(error ?: "An error occurred") },
                confirmButton = {
                    TextButton(onClick = {
                        showErrorDialog = false
                        leadsViewModel.clearError()
                    }) { Text("OK") }
                }
            )
        }
        // Floating Action Button for adding new lead
        FloatingActionButton(
            onClick = {
                dialogStatusId = null
                dialogLead = null
                showLeadDialog = true
            },
            containerColor = OrangePrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Lead", tint = Color.White, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun AddLeadDialog(
    statuses: List<com.example.ercrm.data.model.Status>,
    onAdd: (com.example.ercrm.data.model.LeadRequest) -> Unit,
    onDismiss: () -> Unit,
    initialStatusId: Int?,
    initialLead: com.example.ercrm.data.model.Lead? = null
) {
    var name by remember { mutableStateOf(initialLead?.name ?: "") }
    var phone by remember { mutableStateOf(initialLead?.phone ?: "") }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var email by remember { mutableStateOf(initialLead?.email ?: "") }
    var address by remember { mutableStateOf(initialLead?.address ?: "") }
    var statusId by remember { mutableStateOf(initialLead?.status_id ?: initialStatusId ?: statuses.firstOrNull()?.id ?: 0) }
    var followUpDate by remember { mutableStateOf(initialLead?.follow_up_date?.let {
        try { java.time.LocalDate.parse(it.substring(0,10)).format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")) } catch (e: Exception) { it }
    } ?: "") }
    var description by remember { mutableStateOf(initialLead?.description ?: "") }
    var notes by remember { mutableStateOf(initialLead?.notes ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val today = java.time.LocalDate.now()
    val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")
    var statusDropdownExpanded by remember { mutableStateOf(false) }
    val statusInteractionSource = remember { MutableInteractionSource() }
    // Reason dialog state
    var showReasonDialog by remember { mutableStateOf(false) }
    var reasonText by remember { mutableStateOf("") }
    var pendingStatusId by remember { mutableStateOf<Int?>(null) }
    // Helper to get status name by id
    fun getStatusNameById(id: Int): String? = statuses.find { it.id == id }?.name
    // Helper to get status slug by id
    fun getStatusSlugById(id: Int): String? = statuses.find { it.id == id }?.slug?.lowercase()?.trim()
    // Helper to check if status needs reason
    fun isClosedLost(id: Int): Boolean {
        val slug = getStatusSlugById(id)
        return slug == "lost"
    }
    fun isClosedWon(id: Int): Boolean {
        val slug = getStatusSlugById(id)
        return slug == "won"
    }
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(min = 600.dp)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 600.dp)
            ) {
                Column(
                    Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(if (initialLead == null) "Add Lead" else "Edit Lead", fontWeight = FontWeight.Bold, fontSize = 22.sp, modifier = Modifier.padding(bottom = 16.dp))
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name*") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            if (it.length <= 10 && it.all { ch -> ch.isDigit() }) {
                                phone = it
                            }
                            phoneError = null
                        },
                        label = { Text("Phone*") },
                        isError = phoneError != null,
                        supportingText = { if (phoneError != null) Text(phoneError!!, color = Color.Red, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email*") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    // Status Dropdown (improved)
                    Box {
                        OutlinedTextField(
                            value = statuses.find { it.id == statusId }?.name ?: "",
                            onValueChange = {},
                            label = { Text("Status*") },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = "Show statuses",
                                    modifier = Modifier.clickable { statusDropdownExpanded = true }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .pointerInput(Unit) {} // disables keyboard
                                .clickable(
                                    interactionSource = statusInteractionSource,
                                    indication = null
                                ) { statusDropdownExpanded = true }
                        )
                        DropdownMenu(
                            expanded = statusDropdownExpanded,
                            onDismissRequest = { statusDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            statuses.forEach { status ->
                                DropdownMenuItem(
                                    onClick = {
                                        val name = status.name.lowercase().trim()
                                        if (name == "closed lost" || name == "closed won") {
                                            pendingStatusId = status.id
                                            statusDropdownExpanded = false
                                            showReasonDialog = true
                                        } else {
                                            statusId = status.id
                                            statusDropdownExpanded = false
                                        }
                                    },
                                    text = { Text(status.name) }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    // Follow-up Date with calendar icon clickable
                    OutlinedTextField(
                        value = followUpDate,
                        onValueChange = {},
                        label = { Text("Follow-up Date (dd-MM-yyyy)*") },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = "Pick date",
                                modifier = Modifier.clickable { showDatePicker = true }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                    )
                    if (showDatePicker) {
                        val initial = try { java.time.LocalDate.parse(followUpDate, dateFormatter) } catch (e: Exception) { today }
                        ShowDatePickerDialog(
                            initialDate = initial,
                            onDateSelected = { picked ->
                                followUpDate = picked.format(dateFormatter)
                            },
                            onDismiss = { showDatePicker = false }
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) { Text("Cancel") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
                            // Phone validation: 10 digits, starts with 6-9
                            if (phone.length != 10 || phone[0] !in '6'..'9') {
                                phoneError = "Enter valid 10-digit Indian number"
                                return@Button
                            }
                            // If closed status, show reason dialog instead of submit
                            if (isClosedLost(statusId) || isClosedWon(statusId)) {
                                pendingStatusId = statusId
                                showReasonDialog = true
                                return@Button
                            }
                            onAdd(
                                com.example.ercrm.data.model.LeadRequest(
                                    name = name,
                                    phone = phone,
                                    email = email,
                                    company = null,
                                    address = address,
                                    status_id = statusId,
                                    follow_up_date = try { java.time.LocalDate.parse(followUpDate, dateFormatter).toString() } catch (e: Exception) { followUpDate },
                                    latitude = null,
                                    longitude = null,
                                    notes = notes,
                                    description = description
                                )
                            )
                        }) { Text(if (initialLead == null) "Add Lead" else "Update Lead") }
                    }
                }
            }
        }
    }
    // Reason Dialog for Closed Lost/Won
    if (showReasonDialog) {
        Dialog(onDismissRequest = { showReasonDialog = false }) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text("Change Lead Status", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = reasonText,
                        onValueChange = { reasonText = it },
                        label = { Text("Reason *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(18.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showReasonDialog = false }) { Text("Cancel") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
                            if (reasonText.isBlank()) return@Button
                            // Phone validation: 10 digits, starts with 6-9
                            if (phone.length != 10 || phone[0] !in '6'..'9') {
                                phoneError = "Enter valid 10-digit Indian number"
                                return@Button
                            }
                            val sid = pendingStatusId ?: statusId
                            val req = com.example.ercrm.data.model.LeadRequest(
                                name = name,
                                phone = phone,
                                email = email,
                                company = null,
                                address = address,
                                status_id = sid,
                                follow_up_date = try { java.time.LocalDate.parse(followUpDate, dateFormatter).toString() } catch (e: Exception) { followUpDate },
                                latitude = null,
                                longitude = null,
                                notes = notes,
                                description = description,
                                lost_reason = if (isClosedLost(sid)) reasonText else null,
                                closed_won_reason = if (isClosedWon(sid)) reasonText else null
                            )
                            statusId = sid
                            showReasonDialog = false
                            onAdd(req)
                        }) { Text("Submit") }
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMenuBox(label: String, options: List<String>, selectedIndex: Int, onSelectedIndexChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = options.getOrNull(selectedIndex) ?: "",
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier.clickable { expanded = true }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(onClick = {
                    onSelectedIndexChange(index)
                    expanded = false
                }, text = { Text(option) })
            }
        }
    }
}

@Composable
fun ShowDatePickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val dialog = android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
                onDismiss()
            },
            initialDate.year, initialDate.monthValue - 1, initialDate.dayOfMonth
        )
        dialog.setOnCancelListener { onDismiss() }
        dialog.show()
        onDispose { dialog.dismiss() }
    }
}
