package org.example.employeetaskmanager.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.example.employeetaskmanager.domain.User
import org.example.employeetaskmanager.dto.AuthResponse
import org.example.employeetaskmanager.dto.LoginRequest
import org.example.employeetaskmanager.dto.RegisterRequest
import org.example.employeetaskmanager.repository.UserRepository
import org.example.employeetaskmanager.security.JwtService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
class AuthController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email already registered")
        }

        val user = userRepository.save(
            User(
                name = request.name,
                email = request.email,
                password = passwordEncoder.encode(request.password),
                role = request.role,
                department = request.department
            )
        )

        val token = jwtService.generateToken(user.email, user.role.name)
        return ResponseEntity.ok(user.toAuthResponse(token))
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )

        val user = userRepository.findByEmail(request.email)
            .orElseThrow { RuntimeException("User not found") }

        val token = jwtService.generateToken(user.email, user.role.name)
        return ResponseEntity.ok(user.toAuthResponse(token))
    }

    private fun User.toAuthResponse(token: String) = AuthResponse(
        token = token,
        id = id,
        name = name,
        email = email,
        role = role,
        department = department
    )
}