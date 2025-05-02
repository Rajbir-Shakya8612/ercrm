package com.example.ercrm.ui.components

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ercrm.data.model.Attendance
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun AttendanceSection(
    attendance: Attendance?,
    onCheckIn: (Location) -> Unit,
    onCheckOut: (Location) -> Unit
) {
    var showLocationPermissionDialog by remember { mutableStateOf(false) }
    var showLocationSettingsDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Attendance",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Current Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = attendance?.status?.capitalize() ?: "Not Checked In",
                        style = MaterialTheme.typography.titleMedium,
                        color = when (attendance?.status) {
                            "present" -> MaterialTheme.colorScheme.primary
                            "late" -> MaterialTheme.colorScheme.tertiary
                            "absent" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }

                // Check In/Out Button
                Button(
                    onClick = {
                        if (attendance == null) {
                            showLocationPermissionDialog = true
                        } else if (attendance.checkOutTime == null) {
                            showLocationPermissionDialog = true
                        }
                    },
                    enabled = attendance == null || attendance.checkOutTime == null
                ) {
                    Text(
                        text = if (attendance == null) "Check In" else "Check Out"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Check In Time
                Column {
                    Text(
                        text = "Check In",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = attendance?.checkInTime?.let { formatTime(it) } ?: "--:--",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // Check Out Time
                Column {
                    Text(
                        text = "Check Out",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = attendance?.checkOutTime?.let { formatTime(it) } ?: "--:--",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // Working Hours
                Column {
                    Text(
                        text = "Working Hours",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = attendance?.workingHours ?: "--:--",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }

    // Location Permission Dialog
    if (showLocationPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showLocationPermissionDialog = false },
            title = { Text("Location Permission Required") },
            text = { Text("Please enable location services to check in/out.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLocationPermissionDialog = false
                        showLocationSettingsDialog = true
                    }
                ) {
                    Text("Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLocationPermissionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Location Settings Dialog
    if (showLocationSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showLocationSettingsDialog = false },
            title = { Text("Enable Location") },
            text = { Text("Please enable location services in your device settings.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLocationSettingsDialog = false
                        // TODO: Open location settings
                    }
                ) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLocationSettingsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun formatTime(time: String): String {
    return try {
        val localTime = LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME)
        localTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
    } catch (e: Exception) {
        time
    }
}

private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
} 