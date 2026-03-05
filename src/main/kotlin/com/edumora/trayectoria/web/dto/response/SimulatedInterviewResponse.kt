package com.edumora.trayectoria.web.dto.response

import java.time.LocalDateTime

data class SimulatedInterviewResponse(
    val id: Long,
    val jobOfferId: Long?,
    val jobOfferTitle: String?,
    val status: String,
    val createdAt: LocalDateTime,
    val completedAt: LocalDateTime?,
    val messages: List<InterviewMessageResponse>   // Vacío en listados, completo en detalle
)

data class InterviewMessageResponse(
    val id: Long,
    val role: String,       // "USER" | "ASSISTANT"
    val content: String,
    val sentAt: LocalDateTime
)

// Respuesta al completar — incluye feedback generado por IA
data class CompletedInterviewResponse(
    val interview: SimulatedInterviewResponse,
    val feedback: InterviewFeedback
)

data class InterviewFeedback(
    val overallScore: Int,
    val summary: String,
    val strengths: List<String>,
    val areasForImprovement: List<String>,
    val recommendation: String,   // STRONG_YES | YES | MAYBE | NO
    val details: String
)
