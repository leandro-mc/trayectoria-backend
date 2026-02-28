package com.edumora.trayectoria.web.dto.request

import jakarta.validation.constraints.NotBlank

data class LanguageRequest(
    @field:NotBlank
    val language: String,

    val level: String? = null   // A1, A2, B1, B2, C1, C2, Native
)
