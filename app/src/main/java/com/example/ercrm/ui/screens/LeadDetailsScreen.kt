package com.example.ercrm.ui.screens

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.ercrm.viewmodel.LeadsViewModel
import com.example.ercrm.data.model.Lead
import android.util.Log
import com.example.ercrm.ui.screens.LeadDetailsDialog

@Composable
fun LeadDetailsScreen(navController: NavHostController, leadId: Int, leadsViewModel: LeadsViewModel = hiltViewModel()) {
    val leadsState by leadsViewModel.leadsState.collectAsState()
    val isLoading by leadsViewModel.isLoading.collectAsState()
    val error by leadsViewModel.error.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var foundLead by remember { mutableStateOf<Lead?>(null) }

    // Fetch lead details when screen is opened
    LaunchedEffect(leadId) {
        Log.d("LeadDetailsScreen", "Fetching details for lead: $leadId")
        leadsViewModel.fetchLeadDetails(leadId)
    }

    // Update UI when lead details are loaded
    LaunchedEffect(leadsState) {
        foundLead = leadsState?.leads?.find { it.id == leadId }
        showDialog = foundLead != null
    }

    Box(Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { leadsViewModel.fetchLeadDetails(leadId) }) { Text("Retry") }
                }
            }
            foundLead != null && showDialog -> {
                LeadDetailsDialog(lead = foundLead!!, onDismiss = {
                    showDialog = false
                    navController.popBackStack()
                })
            }
            else -> {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Text("Lead not found.")
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { navController.popBackStack() }) { Text("Back") }
                }
            }
        }
    }
} 