package com.edumora.trayectoria.web.dto.request.candidate

import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

data class WorkExperienceRequest(
    @field:NotBlank
    val company: String,

    @field:NotBlank
    val position: String,

    val description: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val isCurrent: Boolean = false
)