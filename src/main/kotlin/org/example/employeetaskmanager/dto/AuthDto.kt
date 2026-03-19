package org.example.employeetaskmanager.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.example.employeetaskmanager.domain.Role

data class RegisterRequest(
    @field:NotBlank val name: String,
    @field:Email val email: String,
    @field:Size(min = 6) val password: String,
    val role: Role = Role.EMPLOYEE,
    val department: String? = null
)

data class LoginRequest(
    @field:Email val email: String,
    @field:NotBlank val password: String
)

data class AuthResponse(
    val token: String,
    val id: Long,
    val name: String,
    val email: String,
    val role: Role,
    val department: String?
)