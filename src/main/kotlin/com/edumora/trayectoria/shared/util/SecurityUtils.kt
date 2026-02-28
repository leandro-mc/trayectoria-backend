package com.edumora.trayectoria.shared.util

import com.edumora.trayectoria.shared.exception.UnauthorizedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

/**
 * Utility to retrieve the authenticated user from any use case.
 *
 * The controller extracts the email from the SecurityContext and passes it
 * to the use case. Use cases DO NOT access the SecurityContext directly —
 * that is the web layer's responsibility. This function acts as the bridge.
 *
 * Usage in controller:
 * val email = SecurityUtils.currentUserEmail()
 * useCase.execute(email, request)
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