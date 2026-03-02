package com.edumora.trayectoria.web.dto.response

import java.time.LocalDateTime

/**
 * Unified response for both candidate and company.
 * Certain fields are null depending on the requester:
 * - Candidate sees: jobOfferTitle, companyName
 * - Company sees: candidateFirstName, candidateLastName, candidateEmail
 */
data class JobApplicationResponse(
    val id: Long,
    val status: String,
    val appliedAt: LocalDateTime,
    val updatedAt: LocalDateTime?,

    // Info de la oferta — siempre presente
    val jobOfferId: Long,
    val jobOfferTitle: String?,
    val companyName: String?,

    // Info del candidato — presente en vista de empresa
    val candidateId: Long,
    val candidateFirstName: String?,
    val candidateLastName: String?,
    val candidateEmail: String?,

    // Currículum adjunto — presente si se postulo con uno
    val curriculumId: Long?
)