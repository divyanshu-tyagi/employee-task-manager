package org.example.employeetaskmanager.controller


import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.example.employeetaskmanager.domain.User
import org.example.employeetaskmanager.dto.UserSummary
import org.example.employeetaskmanager.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users")
@SecurityRequirement(name = "Bearer Authentication")
class UserController(private val userRepository: UserRepository) {

    @GetMapping("/employees")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all employees (ADMIN only)")
    fun getAllEmployees(): ResponseEntity<List<UserSummary>> {
        val employees = userRepository.findAll()
            .filter { it.role.name == "EMPLOYEE" }
            .map { it.toSummary() }
        return ResponseEntity.ok(employees)
    }

    @GetMapping("/employees/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get employee by ID (ADMIN only)")
    fun getEmployee(@PathVariable id: Long): ResponseEntity<UserSummary> {
        val user = userRepository.findById(id)
            .orElseThrow { NoSuchElementException("Employee not found") }
        return ResponseEntity.ok(user.toSummary())
    }

    private fun User.toSummary() = UserSummary(
        id = id,
        name = name,
        email = email,
        department = department
    )
}