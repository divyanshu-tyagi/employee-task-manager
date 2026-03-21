package org.example.employeetaskmanager.service



import org.example.employeetaskmanager.domain.Priority
import org.example.employeetaskmanager.domain.Role
import org.example.employeetaskmanager.domain.Task
import org.example.employeetaskmanager.domain.TaskStatus
import org.example.employeetaskmanager.domain.User
import org.example.employeetaskmanager.dto.CreateTaskRequest
import org.example.employeetaskmanager.dto.UpdateStatusRequest
import org.example.employeetaskmanager.repository.TaskHistoryRepository
import org.example.employeetaskmanager.repository.TaskRepository
import org.example.employeetaskmanager.repository.UserRepository
import org.example.employeetaskmanager.security.AuthUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.security.access.AccessDeniedException
import java.util.Optional

class TaskServiceTest {

    private val taskRepository: TaskRepository = mock()
    private val taskHistoryRepository: TaskHistoryRepository = mock()
    private val userRepository: UserRepository = mock()
    private val authUtils: AuthUtils = mock()

    private val taskService = TaskService(
        taskRepository, taskHistoryRepository, userRepository, authUtils
    )

    private val adminUser = User(
        id = 1L,
        name = "Admin User",
        email = "admin@taskflow.com",
        password = "encoded",
        role = Role.ADMIN,
        department = "Engineering"
    )

    private val employeeUser = User(
        id = 2L,
        name = "John Employee",
        email = "john@taskflow.com",
        password = "encoded",
        role = Role.EMPLOYEE,
        department = "Engineering"
    )

    private val sampleTask = Task(
        id = 1L,
        title = "Fix login bug",
        description = "Users getting 401",
        priority = Priority.HIGH,
        status = TaskStatus.ASSIGNED,
        assignedTo = employeeUser,
        assignedBy = adminUser
    )

    @BeforeEach
    fun setup() {
        whenever(authUtils.getCurrentUser()).thenReturn(adminUser)
    }

    @Test
    fun `createTask saves task and records history`() {
        val request = CreateTaskRequest(
            title = "Fix login bug",
            description = "Users getting 401",
            priority = Priority.HIGH,
            assignedToId = 2L
        )

        whenever(userRepository.findById(2L)).thenReturn(Optional.of(employeeUser))
        whenever(taskRepository.save(any())).thenReturn(sampleTask)
        whenever(taskHistoryRepository.save(any())).thenReturn(mock())

        val result = taskService.createTask(request)

        verify(taskRepository).save(any())
        verify(taskHistoryRepository).save(any())
        assert(result.title == "Fix login bug")
        assert(result.status == TaskStatus.ASSIGNED)
    }

    @Test
    fun `createTask throws when employee not found`() {
        val request = CreateTaskRequest(
            title = "Test task",
            assignedToId = 99L
        )

        whenever(userRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            taskService.createTask(request)
        }
    }

    @Test
    fun `updateTaskStatus records history and updates status`() {
        whenever(authUtils.getCurrentUser()).thenReturn(employeeUser)
        whenever(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask))
        whenever(taskRepository.save(any())).thenReturn(sampleTask)
        whenever(taskHistoryRepository.save(any())).thenReturn(mock())

        val request = UpdateStatusRequest(
            status = TaskStatus.IN_PROGRESS,
            comment = "Started working"
        )

        val result = taskService.updateTaskStatus(1L, request)

        verify(taskHistoryRepository).save(any())
        verify(taskRepository).save(any())
    }

    @Test
    fun `updateTaskStatus throws when employee updates another employee task`() {
        val otherEmployee = User(
            id = 3L,
            name = "Other Employee",
            email = "other@taskflow.com",
            password = "encoded",
            role = Role.EMPLOYEE
        )

        whenever(authUtils.getCurrentUser()).thenReturn(otherEmployee)
        whenever(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask))

        assertThrows<AccessDeniedException> {
            taskService.updateTaskStatus(1L, UpdateStatusRequest(TaskStatus.IN_PROGRESS))
        }
    }

    @Test
    fun `getTaskById throws when task not found`() {
        whenever(taskRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            taskService.getTaskById(99L)
        }
    }

    @Test
    fun `deleteTask removes history and task`() {
        whenever(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask))
        whenever(taskHistoryRepository.findByTaskId(1L)).thenReturn(emptyList())

        taskService.deleteTask(1L)

        verify(taskHistoryRepository).deleteAll(any())
        verify(taskRepository).delete(sampleTask)
    }
}