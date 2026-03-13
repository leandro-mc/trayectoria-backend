package com.edumora.trayectoria.web.dto.response

data class CompanyStatsResponse(
    val activeOffers: Long,
    val totalApplications: Long,
    val pendingApplications: Long
)