package com.edumora.trayectoria.application.usecase

import com.edumora.trayectoria.infrastructure.persistence.entity.CandidateProfileEntity
import com.edumora.trayectoria.infrastructure.persistence.entity.UserEntity
import com.edumora.trayectoria.infrastructure.persistence.repository.CandidateProfileRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.RoleRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.infrastructure.security.jwt.JwtService
import com.edumora.trayectoria.shared.exception.ConflictException
import com.edumora.trayectoria.shared.exception.NotFoundException
import com.edumora.trayectoria.web.dto.request.RegisterCandidateRequest
import com.edumora.trayectoria.web.dto.response.AuthResponse
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @Transactional -> the entire method runs within a database transaction.
 * If something fails, an automatic rollback is performed — no partial data is saved.
 * If a UserEntity is saved but the CandidateProfile fails, both are reverted.
 *
 * @Service -> Spring registers this as a managed bean. Although it is named
 * UseCase, we use @Service because that is what Spring recognizes for business logic.
 */
@Service
class RegisterCandidateUseCase(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val candidateProfileRepository: CandidateProfileRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {

    @Transactional
    fun execute(request: RegisterCandidateRequest): AuthResponse {

        // 1. Verificar que el email no esté en uso
        if (userRepository.existsByEmail(request.email)) {
            throw ConflictException("Email already in use: ${request.email}")
        }

        // 2. Obtener el rol ROLE_CANDIDATE de la BD (fue insertado por Flyway)
        val role = roleRepository.findByName("ROLE_CANDIDATE")
            .orElseThrow { NotFoundException("Role ROLE_CANDIDATE not found") }

        // 3. Crear y guardar el UserEntity
        val user = userRepository.save(
            UserEntity(
                email = request.email,
                password = passwordEncoder.encode(request.password),
                roles = mutableSetOf(role)
            )
        )

        // 4. Crear el perfil del candidato (vacío inicialmente — se completa después)
        candidateProfileRepository.save(
            CandidateProfileEntity(
                user = user,
                firstName = request.firstName,
                lastName = request.lastName
            )
        )

        // 5. Generar tokens y retornar la respuesta
        val userDetails = buildUserDetails(user.email, "ROLE_CANDIDATE")

        return AuthResponse(
            accessToken  = jwtService.generateAccessToken(userDetails),
            refreshToken = jwtService.generateRefreshToken(userDetails),
            email        = user.email,
            role         = "CANDIDATE"
        )
    }

    private fun buildUserDetails(email: String, roleName: String) =
        User.builder()
            .username(email)
            .password("")
            .authorities(SimpleGrantedAuthority(roleName))
            .build()
}