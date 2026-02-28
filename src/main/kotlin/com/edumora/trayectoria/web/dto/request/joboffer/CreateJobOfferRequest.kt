package com.edumora.trayectoria.web.dto.request.joboffer

import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class CreateJobOfferRequest(
    @field:NotBlank
    val title: String,

    val description: String? = null,
    val responsibilities: String? = null,
    val requirements: String? = null,
    val benefits: String? = null,
    val workMode: String? = null,   // REMOTE, HYBRID, ON_SITE
    val jobType: String? = null,    // FULL_TIME, PART_TIME, INTERNSHIP
    val location: String? = null,
    val interviewInstructions: String? = null,
    val requiresInterview: Boolean = false,
    val expiresAt: LocalDateTime? = null,
    val skillIds: List<Long> = emptyList()
)