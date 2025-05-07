package com.example.ercrm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.ercrm.viewmodel.FollowUpsViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun FollowUpsScreen(
    navController: NavHostController,
    viewModel: FollowUpsViewModel = hiltViewModel()
) {
    val followUps by viewModel.followUps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var showScheduleDialog by remember { mutableStateOf(false) }
    var selectedFollowUp by remember { mutableStateOf<com.example.ercrm.data.model.FollowUp?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchFollowUps()
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
            .padding(16.dp)
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
                "Follow-ups",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { viewModel.fetchFollowUps() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (followUps.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.NotificationsOff,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No follow-ups found",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(followUps) { followUp ->
                    FollowUpCard(
                        followUp = followUp,
                        onScheduleClick = {
                            selectedFollowUp = followUp
                            showScheduleDialog = true
                        },
                        onLeadClick = {
                            navController.navigate("lead_details/${followUp.id}")
                        }
                    )
                }
            }
        }
    }

    if (showScheduleDialog && selectedFollowUp != null) {
        ScheduleFollowUpDialog(
            followUp = selectedFollowUp!!,
            onDismiss = {
                showScheduleDialog = false
                selectedFollowUp = null
            },
            onSchedule = { nextFollowUp, notes ->
                viewModel.scheduleFollowUp(
                    selectedFollowUp!!.id,
                    nextFollowUp,
                    notes
                ) {
                    showScheduleDialog = false
                    selectedFollowUp = null
                    viewModel.fetchFollowUps()
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
}

@Composable
fun FollowUpCard(
    followUp: com.example.ercrm.data.model.FollowUp,
    onScheduleClick: () -> Unit,
    onLeadClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onLeadClick),
        colors = CardDefaults.cardColors(
            containerColor = if (followUp.isOverdue) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = followUp.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (followUp.isOverdue) Color(0xFFD32F2F) else Color(0xFF388E3C)
                )
                IconButton(
                    onClick = onScheduleClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = "Schedule follow-up",
                        tint = if (followUp.isOverdue) Color(0xFFD32F2F) else Color(0xFF388E3C)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Phone,
                    contentDescription = null,
                    tint = Color(0xFF388E3C),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(followUp.phone, fontSize = 16.sp)
            }
            if (!followUp.email.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(followUp.email, fontSize = 16.sp)
                }
            }
            if (!followUp.company.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = null,
                        tint = Color(0xFFF57C00),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(followUp.company, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = if (followUp.isOverdue) Color(0xFFD32F2F) else Color(0xFF388E3C),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = followUp.readableDiff,
                    fontSize = 16.sp,
                    color = if (followUp.isOverdue) Color(0xFFD32F2F) else Color(0xFF388E3C)
                )
            }
        }
    }
}

@Composable
fun ScheduleFollowUpDialog(
    followUp: com.example.ercrm.data.model.FollowUp,
    onDismiss: () -> Unit,
    onSchedule: (String, String?) -> Unit
) {
    var nextFollowUp by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    "Schedule Follow-up",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = nextFollowUp,
                    onValueChange = {},
                    label = { Text("Next Follow-up Date & Time") },
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
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (nextFollowUp.isNotBlank()) {
                                onSchedule(nextFollowUp, notes.takeIf { it.isNotBlank() })
                            }
                        },
                        enabled = nextFollowUp.isNotBlank()
                    ) {
                        Text("Schedule")
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val initial = LocalDateTime.now()
        ShowDateTimePickerDialog(
            initialDateTime = initial,
            onDateTimeSelected = { picked ->
                nextFollowUp = picked.format(dateFormatter)
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun ShowDateTimePickerDialog(
    initialDateTime: LocalDateTime,
    onDateTimeSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val dialog = android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val timeDialog = android.app.TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        onDateTimeSelected(LocalDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute))
                        onDismiss()
                    },
                    initialDateTime.hour,
                    initialDateTime.minute,
                    true
                )
                timeDialog.setOnCancelListener { onDismiss() }
                timeDialog.show()
            },
            initialDateTime.year,
            initialDateTime.monthValue - 1,
            initialDateTime.dayOfMonth
        )
        dialog.setOnCancelListener { onDismiss() }
        dialog.show()
        onDispose { dialog.dismiss() }
    }
} 