package com.example.ercrm.data.model

data class LeadResponse(
    val success: Boolean,
    val lead: Lead,
    val leadStatuses: List<Status>,
    val message: String? = null,
    val error: String? = null
) 