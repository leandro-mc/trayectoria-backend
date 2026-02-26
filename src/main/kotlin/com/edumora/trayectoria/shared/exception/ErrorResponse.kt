package com.edumora.trayectoria.shared.exception

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

/**
 * Standard structure for ALL API errors.
 * The client always receives the same format, regardless of what failed.
 *
 * @JsonInclude(NON_NULL) -> null fields are excluded from the JSON.
 * 'details' only appears in validation errors (list of invalid fields).
 *
 * Example 404 response:
 * {
 * "timestamp": "2025-02-22T10:30:00",
 * "status": 404,
 * "error": "Not Found",
 * "message": "Job offer not found: 99"
 * }
 *
 * Example 400 response with validation:
 * {
 * "timestamp": "2025-02-22T10:30:00",
 * "status": 400,
 * "error": "Bad Request",
 * "message": "Validation failed",
 * "details": [
 * "email: Invalid email format",
 * "password: Password must be at least 8 characters"
 * ]
 * }
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String,
    val details: List<String>? = null   // Solo para errores de validación
)