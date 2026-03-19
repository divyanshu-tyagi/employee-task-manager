package org.example.employeetaskmanager.dto

import jakarta.validation.constraints.NotBlank
import org.example.employeetaskmanager.domain.Priority
import org.example.employeetaskmanager.domain.TaskStatus
import java.time.Instant

data class CreateTaskRequest(
    @field:NotBlank val title: String,
    val description: String? = null,
    val priority: Priority = Priority.MEDIUM,
    val assignedToId: Long,
    val dueDate: Instant? = null
)

data class UpdateTaskRequest(
    val title: String? = null,
    val description: String? = null,
    val priority: Priority? = null,
    val dueDate: Instant? = null
)

data class UpdateStatusRequest(
    val status: TaskStatus,
    val comment: String? = null
)

data class TaskResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val priority: Priority,
    val status: TaskStatus,
    val assignedTo: UserSummary,
    val assignedBy: UserSummary,
    val dueDate: Instant?,
    val completedAt: Instant?,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class UserSummary(
    val id: Long,
    val name: String,
    val email: String,
    val department: String?
)

data class TaskHistoryResponse(
    val id: Long,
    val oldStatus: TaskStatus?,
    val newStatus: TaskStatus,
    val comment: String?,
    val changedBy: UserSummary,
    val changedAt: Instant
)

data class PagedResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)