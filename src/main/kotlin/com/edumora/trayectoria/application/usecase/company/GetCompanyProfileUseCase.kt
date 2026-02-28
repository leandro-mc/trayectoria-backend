package com.edumora.trayectoria.application.usecase.company

import com.edumora.trayectoria.infrastructure.persistence.repository.CompanyProfileRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.response.CompanyProfileResponse
import com.edumora.trayectoria.web.mapper.CompanyProfileMapper
import org.springframework.stereotype.Service

@Service
class GetCompanyProfileUseCase(
    private val userRepository: UserRepository,
    private val companyProfileRepository: CompanyProfileRepository,
    private val mapper: CompanyProfileMapper
) {
    fun execute(email: String): CompanyProfileResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found: $email")
        val profile = companyProfileRepository.findByUserId(user.id)
            .orThrow("Company profile not found")
        return mapper.toResponse(profile)
    }
}