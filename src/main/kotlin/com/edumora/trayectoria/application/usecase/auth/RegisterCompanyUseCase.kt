package com.edumora.trayectoria.application.usecase.auth

import com.edumora.trayectoria.infrastructure.persistence.entity.CompanyProfileEntity
import com.edumora.trayectoria.infrastructure.persistence.entity.UserEntity
import com.edumora.trayectoria.infrastructure.persistence.repository.CompanyProfileRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.RoleRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.infrastructure.security.jwt.JwtService
import com.edumora.trayectoria.shared.exception.ConflictException
import com.edumora.trayectoria.shared.exception.NotFoundException
import com.edumora.trayectoria.web.dto.request.auth.RegisterCompanyRequest
import com.edumora.trayectoria.web.dto.response.AuthResponse
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterCompanyUseCase(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val companyProfileRepository: CompanyProfileRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {

    @Transactional
    fun execute(request: RegisterCompanyRequest): AuthResponse {

        if (userRepository.existsByEmail(request.email)) {
            throw ConflictException("Email already in use: ${request.email}")
        }

        val role = roleRepository.findByName("ROLE_COMPANY")
            .orElseThrow { NotFoundException("Role ROLE_COMPANY not found") }

        val user = userRepository.save(
            UserEntity(
                email = request.email,
                password = passwordEncoder.encode(request.password),
                roles = mutableSetOf(role)
            )
        )

        companyProfileRepository.save(
            CompanyProfileEntity(
                user = user,
                companyName = request.companyName
            )
        )

        val userDetails = User.builder()
            .username(user.email)
            .password("")
            .authorities(SimpleGrantedAuthority("ROLE_COMPANY"))
            .build()

        return AuthResponse(
            accessToken  = jwtService.generateAccessToken(userDetails),
            refreshToken = jwtService.generateRefreshToken(userDetails),
            email        = user.email,
            role         = "COMPANY"
        )
    }
}