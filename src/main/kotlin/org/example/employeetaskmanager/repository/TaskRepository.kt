package org.example.employeetaskmanager.repository

import org.example.employeetaskmanager.domain.Task
import org.example.employeetaskmanager.domain.TaskStatus
import org.example.employeetaskmanager.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TaskRepository : JpaRepository<Task, Long> {
    fun findByAssignedTo(user: User, pageable: Pageable): Page<Task>
    fun findByAssignedToAndStatus(user: User, status: TaskStatus, pageable: Pageable): Page<Task>
    fun findByAssignedBy(user: User, pageable: Pageable): Page<Task>
    fun countByAssignedToAndStatus(user: User, status: TaskStatus): Long
    fun countByAssignedTo(user: User): Long

    @Query("SELECT t FROM Task t WHERE t.assignedTo = :user AND LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%'))")
    fun searchByAssignedToAndTitle(user: User, search: String, pageable: Pageable): Page<Task>

}