package com.edumora.trayectoria.application.usecase.auth

import com.edumora.trayectoria.infrastructure.security.jwt.JwtService
import com.edumora.trayectoria.shared.exception.UnauthorizedException
import com.edumora.trayectoria.web.dto.request.auth.LoginRequest
import com.edumora.trayectoria.web.dto.response.AuthResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class LoginUseCase(
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val jwtService: JwtService
) {

    fun execute(request: LoginRequest): AuthResponse {
        try {
            // AuthenticationManager verifica email + password contra la BD
            // Internamente llama a UserDetailsService + PasswordEncoder
            // Lanza BadCredentialsException si las credenciales son inválidas
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password)
            )
        } catch (e: BadCredentialsException) {
            // Mensaje genérico — no revelamos si el email existe o no
            throw UnauthorizedException("Invalid credentials")
        }

        val userDetails = userDetailsService.loadUserByUsername(request.email)

        val role = userDetails.authorities
            .map { it.authority }
            .firstOrNull { it.startsWith("ROLE_") }
            ?.removePrefix("ROLE_")
            ?: "UNKNOWN"

        return AuthResponse(
            accessToken  = jwtService.generateAccessToken(userDetails),
            refreshToken = jwtService.generateRefreshToken(userDetails),
            email        = userDetails.username,
            role         = role
        )
    }
}