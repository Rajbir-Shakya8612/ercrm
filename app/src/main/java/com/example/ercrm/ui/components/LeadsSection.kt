package com.example.ercrm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ercrm.data.model.Lead
import com.example.ercrm.data.model.LeadStatus

@Composable
fun LeadsSection(
    leadStatuses: List<LeadStatus>,
    onLeadClick: (Int) -> Unit
) {
    var showAddLeadDialog by remember { mutableStateOf(false) }

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
                text = "Leads",
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = { showAddLeadDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Lead"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Kanban Board
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(leadStatuses) { status ->
                LeadColumn(
                    status = status,
                    onLeadClick = onLeadClick
                )
            }
        }
    }

    if (showAddLeadDialog) {
        AddLeadDialog(
            onDismiss = { showAddLeadDialog = false },
            onAddLead = { /* TODO: Implement add lead */ }
        )
    }
}

@Composable
fun LeadColumn(
    status: LeadStatus,
    onLeadClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
    ) {
        // Column Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(android.graphics.Color.parseColor(status.color))
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = status.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = "${status.leads.size}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }

        // Lead Cards
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            status.leads.forEach { lead ->
                LeadCard(
                    lead = lead,
                    onClick = { onLeadClick(lead.id) }
                )
            }
        }
    }
}

@Composable
fun LeadCard(
    lead: Lead,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = lead.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Created: ${formatDate(lead.created_at)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AddLeadDialog(
    onDismiss: () -> Unit,
    onAddLead: (String) -> Unit
) {
    var leadName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Lead") },
        text = {
            Column {
                OutlinedTextField(
                    value = leadName,
                    onValueChange = { leadName = it },
                    label = { Text("Lead Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (leadName.isNotBlank()) {
                        onAddLead(leadName)
                        onDismiss()
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatDate(date: String): String {
    return try {
        val formatter = java.time.format.DateTimeFormatter.ISO_DATE_TIME
        val instant = java.time.Instant.parse(date)
        val localDateTime = java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault())
        localDateTime.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    } catch (e: Exception) {
        date
    }
} 