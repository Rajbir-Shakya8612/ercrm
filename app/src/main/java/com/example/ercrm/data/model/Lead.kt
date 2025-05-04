package com.example.ercrm.data.model

data class Lead(
    val id: Int,
    val name: String,
    val phone: String,
    val email: String?,
    val company: String?,
    val address: String?,
    val status_id: Int,
    val status: Status?,
    val follow_up_date: String?,
    val latitude: Double?,
    val longitude: Double?,
    val additional_info: String?,
    val notes: String?,
    val description: String?,
    val created_at: String?,
    val updated_at: String?,
    val lost_reason: String?,
    val closed_won_reason: String?
)

data class Status(
    val id: Int,
    val name: String,
    val slug: String,
    val color: String?,
    val description: String?
)

data class LeadsResponse(
    val lead_statuses: List<Status>,
    val leads: List<Lead>
)

data class LeadRequest(
    val name: String,
    val phone: String,
    val email: String?,
    val company: String?,
    val address: String?,
    val status_id: Int,
    val follow_up_date: String?,
    val latitude: Double?,
    val longitude: Double?,
    val notes: String?,
    val description: String?,
    val lost_reason: String? = null,
    val closed_won_reason: String? = null
) 