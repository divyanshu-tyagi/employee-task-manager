package org.example.employeetaskmanager.security

import org.example.employeetaskmanager.domain.User
import org.example.employeetaskmanager.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AuthUtils (private val userRepository: UserRepository) {

    fun getCurrentUser(): User {
        val email = getCurrentUserEmail()
        return userRepository.findByEmail(email)
            .orElseThrow { RuntimeException("User not found") }
    }

    fun getCurrentUserEmail(): String {
        return SecurityContextHolder.getContext().authentication?.principal as? String
            ?: throw RuntimeException("No authenticated user found")
    }
}