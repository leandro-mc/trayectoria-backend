package com.edumora.trayectoria.web.dto.response

data class CompanyProfileResponse(
    val userId: Long,
    val email: String,
    val companyName: String?,
    val industry: String?,
    val about: String?,
    val website: String?,
    val logoUrl: String?,
    val location: String?
)