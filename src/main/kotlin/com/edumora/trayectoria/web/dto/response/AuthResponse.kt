package com.edumora.trayectoria.web.dto.response

/**
 * Data returned by the API after a successful login or registration.
 * The frontend stores the accessToken in memory and the refreshToken
 * in an httpOnly cookie (in a more secure implementation — for now,
 * returning both in the body is acceptable).
 */
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val email: String,
    val role: String           // "CANDIDATE" o "COMPANY" — útil para el frontend
)

