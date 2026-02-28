package com.edumora.trayectoria.web.dto.request.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * @field:NotBlank -> validates that the field is neither null nor empty.
 * @field:Email    -> validates email format.
 * @field:Size     -> validates minimum/maximum length.
 *
 * The @field: prefix is necessary in Kotlin because
 * data classes generate properties rather than fields directly.
 * Without @field:, the validation will not be applied.
 * * Validation is triggered using @Valid in the controller.
 */
data class RegisterCandidateRequest(

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val password: String,

    @field:NotBlank(message = "First name is required")
    val firstName: String,

    @field:NotBlank(message = "Last name is required")
    val lastName: String
)