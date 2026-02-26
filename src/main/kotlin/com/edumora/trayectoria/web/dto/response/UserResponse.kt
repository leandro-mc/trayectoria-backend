package com.edumora.trayectoria.web.dto.response

data class UserResponse(
    val id: Long,
    val email: String,
    val role: String,
    val enabled: Boolean
)