package com.edumora.trayectoria.web.dto.request.candidate

import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

data class EducationRequest(
    @field:NotBlank
    val institution: String,

    val degree: String? = null,
    val fieldOfStudy: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null
)