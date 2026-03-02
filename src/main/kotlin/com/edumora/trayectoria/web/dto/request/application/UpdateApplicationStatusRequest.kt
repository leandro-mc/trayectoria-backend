package com.edumora.trayectoria.web.dto.request.application

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class UpdateApplicationStatusRequest(
    @field:NotBlank(message = "Status is required")
    @field:Pattern(
        regexp = "VIEWED|IN_REVIEW|ACCEPTED|REJECTED",
        message = "Status must be one of: VIEWED, IN_REVIEW, ACCEPTED, REJECTED"
    )
    val status: String
)