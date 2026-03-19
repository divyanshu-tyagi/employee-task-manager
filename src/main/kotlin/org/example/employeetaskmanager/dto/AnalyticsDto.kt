package org.example.employeetaskmanager.dto

data class EmployeeEfficiency(
    val employeeId: Long,
    val employeeName: String,
    val department: String?,
    val totalAssigned: Long,
    val completed: Long,
    val inProgress: Long,
    val overdue: Long,
    val completionRate: Double,
    val efficiencyScore: Double
)

data class TeamReport(
    val totalEmployees: Int,
    val totalTasks: Long,
    val completedTasks: Long,
    val overdueTasks: Long,
    val averageEfficiency: Double,
    val topPerformer: String?,
    val employees: List<EmployeeEfficiency>
)