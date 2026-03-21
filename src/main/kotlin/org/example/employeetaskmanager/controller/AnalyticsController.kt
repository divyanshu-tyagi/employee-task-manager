package org.example.employeetaskmanager.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.example.employeetaskmanager.dto.EmployeeEfficiency
import org.example.employeetaskmanager.dto.TeamReport
import org.example.employeetaskmanager.service.AnalyticsService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics")
@SecurityRequirement(name = "Bearer Authentication")
class AnalyticsController(private val analyticsService: AnalyticsService) {

    @GetMapping("/me")
    @Operation(summary = "Get my efficiency score")
    fun getMyEfficiency(): ResponseEntity<EmployeeEfficiency> =
        ResponseEntity.ok(analyticsService.getMyEfficiency())

    @GetMapping("/employee/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get efficiency score for specific employee (ADMIN only)")
    fun getEmployeeEfficiency(@PathVariable id: Long): ResponseEntity<EmployeeEfficiency> =
        ResponseEntity.ok(analyticsService.getEmployeeEfficiency(id))

    @GetMapping("/team")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get full team report (ADMIN only)")
    fun getTeamReport(): ResponseEntity<TeamReport> =
        ResponseEntity.ok(analyticsService.getTeamReport())
}