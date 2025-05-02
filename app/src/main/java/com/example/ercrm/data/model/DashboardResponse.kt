package com.example.ercrm.data.model

data class DashboardResponse(
    val success: Boolean = false,
    val message: String? = null,
    val data: DashboardData? = null
)

data class DashboardData(
    val totalLeads: Int = 0,
    val monthlySales: Double = 0.0,
    val todayMeetings: Int = 0,
    val targetAchievement: Int = 0,
    val leadStatuses: List<LeadStatus> = emptyList(),
    val meetings: List<Meeting> = emptyList(),
    val attendance: AttendanceData? = null,
    val tasks: List<Task> = emptyList(),
    val performanceData: PerformanceData? = null,
    val recentActivities: List<Activity> = emptyList(),
    val events: List<Event> = emptyList(),
    val plansData: PlansData? = null
)

data class LeadStatus(
    val id: Int,
    val name: String,
    val leads: List<Lead> = emptyList()
)

data class Lead(
    val id: Int,
    val name: String,
    val company: String?,
    val phone: String?,
    val email: String?,
    val status: String?,
    val notes: String?,
    val created_at: String
)

data class Meeting(
    val id: String,
    val title: String,
    val start: String,
    val end: String,
    val description: String?,
    val location: String?,
    val status: String?,
    val attendees: List<String>?,
    val notes: String?,
    val backgroundColor: String,
    val borderColor: String
)

data class AttendanceData(
    val id: Int? = null,
    val date: String? = null,
    val is_checked_in: Boolean = false,
    val check_in_time: String? = null,
    val check_out_time: String? = null,
    val working_hours: String? = null,
    val status: String? = null,
    val check_in_location: String? = null,
    val check_out_location: String? = null
)

data class Task(
    val id: String,
    val title: String,
    val start: String,
    val end: String,
    val description: String?,
    val priority: String,
    val status: String,
    val backgroundColor: String,
    val borderColor: String
)

data class PerformanceData(
    val labels: List<String>,
    val leads: List<Int>,
    val attendance: AttendanceStats,
    val achievements: Achievements?
)

data class AttendanceStats(
    val present: List<Int>,
    val late: List<Int>,
    val absent: List<Int>
)

data class Achievements(
    val leads: Double,
    val sales: Double
)

data class Activity(
    val type: String,
    val description: String,
    val details: String,
    val created_at: String
)

data class Event(
    val id: String,
    val title: String,
    val start: String,
    val end: String,
    val type: String? = null,
    val status: String? = null,
    val description: String? = null,
    val backgroundColor: String,
    val borderColor: String
)

data class PlansData(
    val currentPlan: Plan?,
    val previousPlans: List<Plan>
)

data class Plan(
    val id: Int,
    val month: String,
    val leadTarget: Int,
    val salesTarget: Double,
    val leadAchieved: Int,
    val salesAchieved: Double
) 