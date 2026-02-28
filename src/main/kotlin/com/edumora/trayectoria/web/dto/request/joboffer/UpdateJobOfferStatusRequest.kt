package com.edumora.trayectoria.web.dto.request.joboffer

import jakarta.validation.constraints.NotBlank

data class UpdateJobOfferStatusRequest(
    @field:NotBlank
    val status: String  // ACTIVE, CLOSED, DRAFT
)