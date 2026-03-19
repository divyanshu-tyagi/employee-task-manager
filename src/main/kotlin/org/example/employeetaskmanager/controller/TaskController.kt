package org.example.employeetaskmanager.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.example.employeetaskmanager.domain.TaskStatus
import org.example.employeetaskmanager.dto.CreateTaskRequest
import org.example.employeetaskmanager.dto.PagedResponse
import org.example.employeetaskmanager.dto.TaskHistoryResponse
import org.example.employeetaskmanager.dto.TaskResponse
import org.example.employeetaskmanager.dto.UpdateStatusRequest
import org.example.employeetaskmanager.dto.UpdateTaskRequest
import org.example.employeetaskmanager.service.TaskService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks")
@SecurityRequirement(name = "Bearer Authentication")
class TaskController(private val taskService: TaskService) {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create and assign task (ADMIN only)")
    fun createTask(@Valid @RequestBody request: CreateTaskRequest): ResponseEntity<TaskResponse> =
        ResponseEntity.ok(taskService.createTask(request))

    @GetMapping
    @Operation(summary = "Get my tasks (EMPLOYEE) or assigned tasks (ADMIN)")
    fun getMyTasks(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) status: TaskStatus?,
        @RequestParam(required = false) search: String?
    ): ResponseEntity<PagedResponse<TaskResponse>> =
        ResponseEntity.ok(taskService.getMyTasks(page, size, status, search))

    @GetMapping("/assigned")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all tasks assigned by me (ADMIN only)")
    fun getAssignedTasks(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PagedResponse<TaskResponse>> =
        ResponseEntity.ok(taskService.getAssignedTasks(page, size))

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    fun getTaskById(@PathVariable id: Long): ResponseEntity<TaskResponse> =
        ResponseEntity.ok(taskService.getTaskById(id))

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update task details (ADMIN only)")
    fun updateTask(
        @PathVariable id: Long,
        @RequestBody request: UpdateTaskRequest
    ): ResponseEntity<TaskResponse> =
        ResponseEntity.ok(taskService.updateTask(id, request))

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status (EMPLOYEE)")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestBody request: UpdateStatusRequest
    ): ResponseEntity<TaskResponse> =
        ResponseEntity.ok(taskService.updateTaskStatus(id, request))

    @GetMapping("/{id}/history")
    @Operation(summary = "Get task change history")
    fun getTaskHistory(@PathVariable id: Long): ResponseEntity<List<TaskHistoryResponse>> =
        ResponseEntity.ok(taskService.getTaskHistory(id))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete task (ADMIN only)")
    fun deleteTask(@PathVariable id: Long): ResponseEntity<Void> {
        taskService.deleteTask(id)
        return ResponseEntity.noContent().build()
    }
}