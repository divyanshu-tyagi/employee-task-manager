package org.example.employeetaskmanager.service

import org.example.employeetaskmanager.domain.Role
import org.example.employeetaskmanager.domain.TaskStatus
import org.example.employeetaskmanager.domain.User
import org.example.employeetaskmanager.repository.TaskRepository
import org.example.employeetaskmanager.repository.UserRepository
import org.example.employeetaskmanager.security.AuthUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.util.Optional

class AnalyticsServiceTest {

    private val taskRepository: TaskRepository = mock()
    private val userRepository: UserRepository = mock()
    private val authUtils: AuthUtils = mock()

    private val analyticsService = AnalyticsService(
        taskRepository, userRepository, authUtils
    )

    private val employee = User(
        id = 1L,
        name = "John Employee",
        email = "john@taskflow.com",
        password = "encoded",
        role = Role.EMPLOYEE,
        department = "Engineering"
    )

    @BeforeEach
    fun setup() {
        whenever(authUtils.getCurrentUser()).thenReturn(employee)
    }

    @Test
    fun `getEmployeeEfficiency calculates correct score`() {
        whenever(userRepository.findById(1L)).thenReturn(Optional.of(employee))
        whenever(taskRepository.countByAssignedTo(employee)).thenReturn(10L)
        whenever(taskRepository.countByAssignedToAndStatus(employee, TaskStatus.COMPLETED)).thenReturn(8L)
        whenever(taskRepository.countByAssignedToAndStatus(employee, TaskStatus.IN_PROGRESS)).thenReturn(1L)
        whenever(taskRepository.countByAssignedToAndStatus(employee, TaskStatus.OVERDUE)).thenReturn(1L)

        val result = analyticsService.getEmployeeEfficiency(1L)

        assert(result.totalAssigned == 10L)
        assert(result.completed == 8L)
        assert(result.completionRate == 80.0)
        assert(result.efficiencyScore > 0)
    }

    @Test
    fun `getEmployeeEfficiency returns zero score when no tasks`() {
        whenever(userRepository.findById(1L)).thenReturn(Optional.of(employee))
        whenever(taskRepository.countByAssignedTo(employee)).thenReturn(0L)
        whenever(taskRepository.countByAssignedToAndStatus(any(), any())).thenReturn(0L)

        val result = analyticsService.getEmployeeEfficiency(1L)

        assert(result.totalAssigned == 0L)
        assert(result.completionRate == 0.0)
        assert(result.efficiencyScore == 30.0)
    }

    @Test
    fun `getMyEfficiency returns current user efficiency`() {
        whenever(userRepository.findById(1L)).thenReturn(Optional.of(employee))
        whenever(taskRepository.countByAssignedTo(employee)).thenReturn(5L)
        whenever(taskRepository.countByAssignedToAndStatus(employee, TaskStatus.COMPLETED)).thenReturn(5L)
        whenever(taskRepository.countByAssignedToAndStatus(employee, TaskStatus.IN_PROGRESS)).thenReturn(0L)
        whenever(taskRepository.countByAssignedToAndStatus(employee, TaskStatus.OVERDUE)).thenReturn(0L)

        val result = analyticsService.getMyEfficiency()

        assert(result.employeeId == 1L)
        assert(result.completionRate == 100.0)
    }

    @Test
    fun `getEmployeeEfficiency throws when employee not found`() {
        whenever(userRepository.findById(99L)).thenReturn(Optional.empty())

        org.junit.jupiter.api.assertThrows<NoSuchElementException> {
            analyticsService.getEmployeeEfficiency(99L)
        }
    }
}