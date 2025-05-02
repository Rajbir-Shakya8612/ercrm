package com.example.ercrm.data.model

data class SalespersonDashboardResponse(
    val success: Boolean = false,
    val message: String? = null,
    val data: SalespersonDashboardData? = null
)

data class SalespersonDashboardData(
    val totalLeads: Int = 0,
    val monthlySales: Int = 0,
    val todayMeetings: Int = 0,
    val targetAchievement: Int = 0,
    val leadStatuses: List<LeadStatus> = emptyList(),
    val meetings: List<Meeting> = emptyList(),
    val attendance: Attendance? = null,
    val tasks: List<Task> = emptyList(),
    val performanceData: PerformanceData = PerformanceData(),
    val recentActivities: List<Activity> = emptyList(),
    val events: List<Event> = emptyList(),
    val plansData: PlansData = PlansData()
)

data class LeadStatus(
    val id: Int,
    val name: String,
    val slug: String,
    val color: String,
    val description: String,
    val created_at: String,
    val updated_at: String,
    val brand_id: Int?,
    val leads: List<Lead> = emptyList()
)

data class Lead(
    val id: Int,
    val name: String,
    val status: String,
    val created_at: String
)

data class Meeting(
    val id: Int,
    val title: String,
    val date: String,
    val time: String,
    val status: String
)

data class Attendance(
    val checkInTime: String?,
    val checkOutTime: String?,
    val workingHours: String?,
    val status: String
)

data class Task(
    val id: Int,
    val title: String,
    val dueDate: String,
    val status: String
)

data class PerformanceData(
    val labels: List<String> = emptyList(),
    val leads: List<Int> = emptyList(),
    val attendance: AttendanceData = AttendanceData(),
    val achievements: Achievements = Achievements()
)

data class AttendanceData(
    val present: List<Int> = emptyList(),
    val late: List<Int> = emptyList(),
    val absent: List<Int> = emptyList()
)

data class Achievements(
    val leads: Int = 0,
    val sales: Int = 0
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
    val description: String,
    val status: String,
    val check_in_time: String?,
    val check_out_time: String?,
    val working_hours: String?,
    val backgroundColor: String,
    val borderColor: String
)

data class PlansData(
    val plans: List<Plan> = emptyList(),
    val monthlyTarget: Int = 0,
    val monthlyAchieved: Int = 0,
    val quarterlyTarget: Int = 0,
    val quarterlyAchieved: Int = 0,
    val yearlyTarget: Int = 0,
    val yearlyAchieved: Int = 0,
    val monthlyLeadsTarget: Int = 0,
    val monthlyLeadsAchieved: Int = 0,
    val quarterlyLeadsTarget: Int = 0,
    val quarterlyLeadsAchieved: Int = 0,
    val yearlyLeadsTarget: Int = 0,
    val yearlyLeadsAchieved: Int = 0,
    val chartData: ChartData = ChartData()
)

data class Plan(
    val id: Int,
    val name: String,
    val target: Int,
    val achieved: Int
)

data class ChartData(
    val monthly: TimeFrameData = TimeFrameData(),
    val quarterly: TimeFrameData = TimeFrameData(),
    val yearly: TimeFrameData = TimeFrameData()
)

data class TimeFrameData(
    val labels: List<String> = emptyList(),
    val leadData: List<Int> = emptyList(),
    val salesData: List<Int> = emptyList(),
    val leadTargets: List<Int> = emptyList(),
    val salesTargets: List<Int> = emptyList(),
    val leadPercentages: List<Int> = emptyList(),
    val salesPercentages: List<Int> = emptyList()
) 