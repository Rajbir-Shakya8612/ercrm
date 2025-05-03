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
    val description: String?,
    val created_at: String?,
    val updated_at: String?
)

data class Status(
    val id: Int,
    val name: String,
    val color: String?
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
    val additional_info: String?,
    val description: String?
) 