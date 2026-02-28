package com.edumora.trayectoria.web.dto.request.company

import jakarta.validation.constraints.Size

data class UpdateCompanyProfileRequest(
    @field:Size(max = 255)
    val companyName: String? = null,

    @field:Size(max = 150)
    val industry: String? = null,

    val about: String? = null,

    @field:Size(max = 255)
    val website: String? = null,

    @field:Size(max = 255)
    val location: String? = null
)