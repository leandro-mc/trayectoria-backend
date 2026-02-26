package com.edumora.trayectoria.infrastructure.security.jwt

import com.edumora.trayectoria.infrastructure.security.service.UserDetailsServiceImpl
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * OncePerRequestFilter ensures that this filter is executed
 * EXACTLY ONCE per HTTP request — regardless of the filter chain.
 *
 * Flow:
 * 1. Extracts the token from the Authorization: Bearer <token> header.
 * 2. Validates the token using JwtService.
 * 3. If valid, loads the user and sets the SecurityContext.
 * 4. Passes the request to the next filter in the chain.
 *
 * If no token is present or it is invalid, it simply proceeds to the next filter.
 * Spring Security will then handle rejecting the request if the endpoint requires authentication.
 */
@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsServiceImpl
) : OncePerRequestFilter() {

    companion object {
        private const val BEARER_PREFIX = "Bearer "
        private const val AUTH_HEADER = "Authorization"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader(AUTH_HEADER)

        // Si no hay header o no empieza con Bearer, seguimos sin autenticar
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.removePrefix(BEARER_PREFIX)

        // Si ya hay autenticación en el contexto, no procesamos de nuevo
        if (SecurityContextHolder.getContext().authentication != null) {
            filterChain.doFilter(request, response)
            return
        }

        runCatching { jwtService.extractEmail(token) }
            .onSuccess { email ->
                val userDetails = userDetailsService.loadUserByUsername(email)

                if (jwtService.isTokenValid(token, userDetails)) {
                    // Crea el token de autenticación con roles
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,                        // credentials = null post-autenticación
                        userDetails.authorities
                    ).apply {
                        details = WebAuthenticationDetailsSource().buildDetails(request)
                    }
                    // Seta el SecurityContext — Spring Security sabe que el usuario está autenticado
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }

        filterChain.doFilter(request, response)
    }
}
