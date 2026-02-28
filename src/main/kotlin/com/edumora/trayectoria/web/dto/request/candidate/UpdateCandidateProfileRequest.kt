package com.edumora.trayectoria.web.dto.request.candidate

import jakarta.validation.constraints.Size
import java.time.LocalDate

data class UpdateCandidateProfileRequest(
    @field:Size(max = 100)
    val firstName: String? = null,

    @field:Size(max = 100)
    val lastName: String? = null,

    @field:Size(max = 20)
    val phone: String? = null,

    @field:Size(max = 255)
    val location: String? = null,

    val bio: String? = null,

    @field:Size(max = 255)
    val linkedinUrl: String? = null,

    @field:Size(max = 255)
    val githubUrl: String? = null,

    @field:Size(max = 255)
    val portfolioUrl: String? = null,

    val birthdate: LocalDate? = null
)