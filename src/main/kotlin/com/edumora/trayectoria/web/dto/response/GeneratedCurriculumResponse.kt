package com.edumora.trayectoria.web.dto.response

import com.edumora.trayectoria.infrastructure.persistence.entity.CurriculumContent
import java.time.LocalDateTime

data class GeneratedCurriculumResponse(
    val id: Long,
    val jobOfferId: Long?,
    val jobOfferTitle: String?,
    val content: CurriculumContent,
    val isAiGenerated: Boolean,
    val createdAt: LocalDateTime
)