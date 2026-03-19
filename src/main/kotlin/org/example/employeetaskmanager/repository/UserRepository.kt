package org.example.employeetaskmanager.repository

import org.example.employeetaskmanager.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository: JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
    fun existsByEmail(email: String): Boolean
    fun findByDepartment(department: String): List<User>
}