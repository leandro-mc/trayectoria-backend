package com.edumora.trayectoria.web.dto.request.candidate

import com.fasterxml.jackson.annotation.JsonProperty
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

    @get:JvmName("getIsCurrent")
    @JsonProperty("isCurrent")
    val isCurrent: Boolean = false
)