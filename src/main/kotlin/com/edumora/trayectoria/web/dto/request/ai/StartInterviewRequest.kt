package com.edumora.trayectoria.web.dto.request.ai

import jakarta.validation.constraints.NotNull

data class StartInterviewRequest(
    @field:NotNull(message = "jobOfferId is required")
    val jobOfferId: Long
)