package com.edumora.trayectoria.shared.util

import org.springframework.data.domain.Page

/**
 * Wrapper estándar para respuestas paginadas.
 * Evita exponer el objeto Page<T> de Spring directamente al cliente.
 *
 * El cliente recibe:
 * {
 *   "content": [...],
 *   "page": 0,
 *   "size": 10,
 *   "totalElements": 42,
 *   "totalPages": 5,
 *   "last": false
 * }
 */
data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
) {
    companion object {
        fun <T> from(page: Page<T>): PageResponse<T> = PageResponse(
            content       = page.content,
            page          = page.number,
            size          = page.size,
            totalElements = page.totalElements,
            totalPages    = page.totalPages,
            last          = page.isLast
        )
    }
}