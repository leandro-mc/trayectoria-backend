package com.edumora.trayectoria.shared.util

import com.edumora.trayectoria.shared.exception.UnauthorizedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

/**
 * Utilidad para obtener el usuario autenticado desde cualquier use case.
 *
 * El controller extrae el email del SecurityContext y lo pasa al use case.
 * Los use cases NO acceden al SecurityContext directamente —
 * eso es responsabilidad de la capa web. Esta función es el puente.
 *
 * Uso en controller:
 *   val email = SecurityUtils.currentUserEmail()
 *   useCase.execute(email, request)
 */
object SecurityUtils {

    fun currentUserEmail(): String {
        val principal = SecurityContextHolder.getContext().authentication?.principal
            ?: throw UnauthorizedException("No authenticated user found")

        return when (principal) {
            is UserDetails -> principal.username
            is String      -> principal
            else           -> throw UnauthorizedException("Cannot resolve authenticated user")
        }
    }
}