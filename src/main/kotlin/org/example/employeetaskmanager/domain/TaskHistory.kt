package org.example.employeetaskmanager.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "task_history")
data class TaskHistory(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    val task: Task = Task(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by", nullable = false)
    val changedBy: User = User(),

    @Enumerated(EnumType.STRING)
    @Column
    val oldStatus: TaskStatus? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val newStatus: TaskStatus = TaskStatus.ASSIGNED,

    @Column(columnDefinition = "TEXT")
    val comment: String? = null,

    @Column(nullable = false)
    val changedAt: Instant = Instant.now()
)