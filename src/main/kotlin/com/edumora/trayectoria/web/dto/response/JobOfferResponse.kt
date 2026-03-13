package com.edumora.trayectoria.web.dto.response

import java.time.LocalDateTime

// Respuesta completa — para el detalle de una oferta
data class JobOfferResponse(
    val id: Long,
    val companyId: Long,
    val companyName: String?,
    val title: String,
    val description: String?,
    val responsibilities: String?,
    val requirements: String?,
    val benefits: String?,
    val workMode: String?,
    val jobType: String?,
    val status: String,
    val location: String?,
    val requiresInterview: Boolean,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime?,
    val skills: List<SkillResponse>
)

// Respuesta resumida — para listas y búsqueda
data class JobOfferSummaryResponse(
    val id: Long,
    val companyName: String?,
    val title: String,
    val workMode: String?,
    val jobType: String?,
    val status: String,
    val location: String?,
    val createdAt: LocalDateTime,
    val skills: List<SkillResponse>
)

data class JobOfferInterviewInstructionsResponse(
    val jobOfferId: Long,
    val jobOfferTitle: String,
    val interviewInstructions: String?,
    val requiresInterview: Boolean
)