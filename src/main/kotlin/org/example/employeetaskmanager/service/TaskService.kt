package org.example.employeetaskmanager.service



import org.example.employeetaskmanager.domain.Role
import org.example.employeetaskmanager.domain.Task
import org.example.employeetaskmanager.domain.TaskHistory
import org.example.employeetaskmanager.domain.TaskStatus
import org.example.employeetaskmanager.domain.User
import org.example.employeetaskmanager.dto.CreateTaskRequest
import org.example.employeetaskmanager.dto.PagedResponse
import org.example.employeetaskmanager.dto.TaskHistoryResponse
import org.example.employeetaskmanager.dto.TaskResponse
import org.example.employeetaskmanager.dto.UpdateStatusRequest
import org.example.employeetaskmanager.dto.UpdateTaskRequest
import org.example.employeetaskmanager.dto.UserSummary
import org.example.employeetaskmanager.repository.TaskHistoryRepository
import org.example.employeetaskmanager.repository.TaskRepository
import org.example.employeetaskmanager.repository.UserRepository
import org.example.employeetaskmanager.security.AuthUtils
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
@Transactional
class TaskService(
    private val taskRepository: TaskRepository,
    private val taskHistoryRepository: TaskHistoryRepository,
    private val userRepository: UserRepository,
    private val authUtils: AuthUtils
) {

    // ADMIN — create and assign task to employee
    fun createTask(request: CreateTaskRequest): TaskResponse {
        val currentUser = authUtils.getCurrentUser()

        val assignedTo = userRepository.findById(request.assignedToId)
            .orElseThrow { NoSuchElementException("Employee not found") }

        val task = taskRepository.save(
            Task(
                title = request.title,
                description = request.description,
                priority = request.priority,
                status = TaskStatus.ASSIGNED,
                assignedTo = assignedTo,
                assignedBy = currentUser,
                dueDate = request.dueDate
            )
        )

        // record history
        taskHistoryRepository.save(
            TaskHistory(
                task = task,
                changedBy = currentUser,
                oldStatus = null,
                newStatus = TaskStatus.ASSIGNED,
                comment = "Task created and assigned to ${assignedTo.name}"
            )
        )

        return task.toResponse()
    }

    // EMPLOYEE — get my tasks
    fun getMyTasks(
        page: Int,
        size: Int,
        status: TaskStatus?,
        search: String?
    ): PagedResponse<TaskResponse> {
        val currentUser = authUtils.getCurrentUser()
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))

        val result = when {
            search != null -> taskRepository.searchByAssignedToAndTitle(currentUser, search, pageable)
            status != null -> taskRepository.findByAssignedToAndStatus(currentUser, status, pageable)
            else -> taskRepository.findByAssignedTo(currentUser, pageable)
        }

        return PagedResponse(
            content = result.content.map { it.toResponse() },
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages,
            last = result.isLast
        )
    }

    // ADMIN — get all tasks assigned by me
    fun getAssignedTasks(page: Int, size: Int): PagedResponse<TaskResponse> {
        val currentUser = authUtils.getCurrentUser()
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val result = taskRepository.findByAssignedBy(currentUser, pageable)

        return PagedResponse(
            content = result.content.map { it.toResponse() },
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages,
            last = result.isLast
        )
    }

    // get task by id
    fun getTaskById(id: Long): TaskResponse {
        val currentUser = authUtils.getCurrentUser()
        val task = findTaskWithAccess(id, currentUser)
        return task.toResponse()
    }

    // ADMIN — update task details
    fun updateTask(id: Long, request: UpdateTaskRequest): TaskResponse {
        val currentUser = authUtils.getCurrentUser()
        val task = taskRepository.findById(id)
            .orElseThrow { NoSuchElementException("Task not found") }

        if (task.assignedBy.id != currentUser.id && currentUser.role != Role.ADMIN) {
            throw AccessDeniedException("Only the assigning admin can update this task")
        }

        request.title?.let { task.title = it }
        request.description?.let { task.description = it }
        request.priority?.let { task.priority = it }
        request.dueDate?.let { task.dueDate = it }
        task.updatedAt = Instant.now()

        return taskRepository.save(task).toResponse()
    }

    // EMPLOYEE — update task status
    fun updateTaskStatus(id: Long, request: UpdateStatusRequest): TaskResponse {
        val currentUser = authUtils.getCurrentUser()
        val task = taskRepository.findById(id)
            .orElseThrow { NoSuchElementException("Task not found") }

        if (task.assignedTo.id != currentUser.id && currentUser.role != Role.ADMIN) {
            throw AccessDeniedException("You can only update status of your own tasks")
        }

        val oldStatus = task.status
        task.status = request.status
        task.updatedAt = Instant.now()

        if (request.status == TaskStatus.COMPLETED) {
            task.completedAt = Instant.now()
        }

        taskHistoryRepository.save(
            TaskHistory(
                task = task,
                changedBy = currentUser,
                oldStatus = oldStatus,
                newStatus = request.status,
                comment = request.comment
            )
        )

        return taskRepository.save(task).toResponse()
    }

    // get task history
    fun getTaskHistory(id: Long): List<TaskHistoryResponse> {
        val task = taskRepository.findById(id)
            .orElseThrow { NoSuchElementException("Task not found") }
        return taskHistoryRepository.findByTaskOrderByChangedAtDesc(task)
            .map { it.toResponse() }
    }

    // ADMIN — delete task
    fun deleteTask(id: Long) {
        val task = taskRepository.findById(id)
            .orElseThrow { NoSuchElementException("Task not found") }
        taskHistoryRepository.deleteAll(taskHistoryRepository.findByTaskId(id))
        taskRepository.delete(task)
    }

    // helpers
    private fun findTaskWithAccess(id: Long, user: User): Task {
        val task = taskRepository.findById(id)
            .orElseThrow { NoSuchElementException("Task not found") }
        if (task.assignedTo.id != user.id &&
            task.assignedBy.id != user.id &&
            user.role != Role.ADMIN) {
            throw AccessDeniedException("Access denied")
        }
        return task
    }

    private fun Task.toResponse() = TaskResponse(
        id = id,
        title = title,
        description = description,
        priority = priority,
        status = status,
        assignedTo = assignedTo.toSummary(),
        assignedBy = assignedBy.toSummary(),
        dueDate = dueDate,
        completedAt = completedAt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun User.toSummary() = UserSummary(
        id = id,
        name = name,
        email = email,
        department = department
    )

    private fun TaskHistory.toResponse() = TaskHistoryResponse(
        id = id,
        oldStatus = oldStatus,
        newStatus = newStatus,
        comment = comment,
        changedBy = changedBy.toSummary(),
        changedAt = changedAt
    )
}