package org.example.employeetaskmanager.repository

import org.example.employeetaskmanager.domain.Task
import org.example.employeetaskmanager.domain.TaskHistory
import org.springframework.data.jpa.repository.JpaRepository

interface TaskHistoryRepository: JpaRepository<TaskHistory, Long> {
    fun findByTaskOrderByChangedAtDesc(task: Task): List<TaskHistory>
    fun findByTaskId(taskId: Long): List<TaskHistory>
}