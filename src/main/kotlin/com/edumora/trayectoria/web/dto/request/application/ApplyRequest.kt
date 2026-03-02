package com.edumora.trayectoria.web.dto.request.application

/**
 * curriculumId is optional — it will be linked once the AI module is ready.
 * Currently, the use case receives it but stores null if it's not provided.
 * No @NotNull required — it's an optional enrichment for the application.
 */
data class ApplyRequest(
    val curriculumId: Long? = null
)