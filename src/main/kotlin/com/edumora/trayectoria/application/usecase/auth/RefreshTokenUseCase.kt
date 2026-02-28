package com.edumora.trayectoria.application.usecase.auth

import com.edumora.trayectoria.infrastructure.security.jwt.JwtService
import com.edumora.trayectoria.shared.exception.UnauthorizedException
import com.edumora.trayectoria.web.dto.request.auth.RefreshTokenRequest
import com.edumora.trayectoria.web.dto.response.AuthResponse
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class RefreshTokenUseCase(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService
) {

    fun execute(request: RefreshTokenRequest): AuthResponse {
        val email = runCatching { jwtService.extractEmail(request.refreshToken) }
            .getOrElse { throw UnauthorizedException("Invalid refresh token") }

        if (jwtService.isTokenExpired(request.refreshToken)) {
            throw UnauthorizedException("Refresh token has expired")
        }

        val userDetails = userDetailsService.loadUserByUsername(email)

        val role = userDetails.authorities
            .map { it.authority }
            .firstOrNull { it.startsWith("ROLE_") }
            ?.removePrefix("ROLE_")
            ?: "UNKNOWN"

        return AuthResponse(
            accessToken  = jwtService.generateAccessToken(userDetails),
            refreshToken = jwtService.generateRefreshToken(userDetails),
            email        = email,
            role         = role
        )
    }
}