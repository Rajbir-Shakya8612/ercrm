package com.example.ercrm.data.model

import com.google.gson.annotations.SerializedName

data class FollowUp(
    val id: Int,
    val name: String,
    @SerializedName("follow_up_date")
    val followUpDate: String,
    val phone: String,
    val email: String?,
    val company: String?,
    @SerializedName("is_overdue")
    val isOverdue: Boolean,
    @SerializedName("days_left")
    val daysLeft: Int,
    @SerializedName("readable_diff")
    val readableDiff: String
)

data class FollowUpsResponse(
    val success: Boolean,
    val followUps: List<FollowUp>,
    val message: String? = null,
    val error: String? = null
)

data class FollowUpRequest(
    @SerializedName("next_follow_up")
    val nextFollowUp: String,
    val notes: String? = null
)

data class FollowUpResponse(
    val success: Boolean,
    val message: String,
    val error: String? = null
) 