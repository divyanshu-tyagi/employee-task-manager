package org.example.employeetaskmanager.service


import org.example.employeetaskmanager.domain.Role
import org.example.employeetaskmanager.domain.TaskStatus
import org.example.employeetaskmanager.dto.EmployeeEfficiency
import org.example.employeetaskmanager.dto.TeamReport
import org.example.employeetaskmanager.repository.TaskRepository
import org.example.employeetaskmanager.repository.UserRepository
import org.example.employeetaskmanager.security.AuthUtils
import org.springframework.stereotype.Service

@Service
class AnalyticsService(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val authUtils: AuthUtils
) {

    fun getEmployeeEfficiency(employeeId: Long): EmployeeEfficiency {
        val employee = userRepository.findById(employeeId)
            .orElseThrow { NoSuchElementException("Employee not found") }

        val totalAssigned = taskRepository.countByAssignedTo(employee)
        val completed = taskRepository.countByAssignedToAndStatus(employee, TaskStatus.COMPLETED)
        val inProgress = taskRepository.countByAssignedToAndStatus(employee, TaskStatus.IN_PROGRESS)
        val overdue = taskRepository.countByAssignedToAndStatus(employee, TaskStatus.OVERDUE)

        val completionRate = if (totalAssigned > 0)
            (completed.toDouble() / totalAssigned.toDouble()) * 100 else 0.0

        val overdueRate = if (totalAssigned > 0)
            (overdue.toDouble() / totalAssigned.toDouble()) * 100 else 0.0

        val efficiencyScore = (completionRate * 0.7) + ((100 - overdueRate) * 0.3)

        return EmployeeEfficiency(
            employeeId = employee.id,
            employeeName = employee.name,
            department = employee.department,
            totalAssigned = totalAssigned,
            completed = completed,
            inProgress = inProgress,
            overdue = overdue,
            completionRate = Math.round(completionRate * 100.0) / 100.0,
            efficiencyScore = Math.round(efficiencyScore * 100.0) / 100.0
        )
    }

    fun getTeamReport(): TeamReport {
        val currentUser = authUtils.getCurrentUser()

        val employees = if (currentUser.department != null)
            userRepository.findByDepartment(currentUser.department!!)
                .filter { it.role == Role.EMPLOYEE }
        else
            userRepository.findAll()
                .filter { it.role == Role.EMPLOYEE }

        val efficiencies = employees.map { getEmployeeEfficiency(it.id) }

        val totalTasks = efficiencies.sumOf { it.totalAssigned }
        val completedTasks = efficiencies.sumOf { it.completed }
        val overdueTasks = efficiencies.sumOf { it.overdue }

        val averageEfficiency = if (efficiencies.isNotEmpty())
            efficiencies.map { it.efficiencyScore }.average() else 0.0

        val topPerformer = efficiencies.maxByOrNull { it.efficiencyScore }?.employeeName

        return TeamReport(
            totalEmployees = employees.size,
            totalTasks = totalTasks,
            completedTasks = completedTasks,
            overdueTasks = overdueTasks,
            averageEfficiency = Math.round(averageEfficiency * 100.0) / 100.0,
            topPerformer = topPerformer,
            employees = efficiencies.sortedByDescending { it.efficiencyScore }
        )
    }

    fun getMyEfficiency(): EmployeeEfficiency {
        val currentUser = authUtils.getCurrentUser()
        return getEmployeeEfficiency(currentUser.id)
    }
}