package com.edumora.trayectoria.application.usecase.company

import com.edumora.trayectoria.infrastructure.persistence.repository.CompanyProfileRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.request.company.UpdateCompanyProfileRequest
import com.edumora.trayectoria.web.dto.response.CompanyProfileResponse
import com.edumora.trayectoria.web.mapper.CompanyProfileMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateCompanyProfileUseCase(
    private val userRepository: UserRepository,
    private val companyProfileRepository: CompanyProfileRepository,
    private val mapper: CompanyProfileMapper
) {
    @Transactional
    fun execute(email: String, request: UpdateCompanyProfileRequest): CompanyProfileResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found: $email")
        val profile = companyProfileRepository.findByUserId(user.id)
            .orThrow("Company profile not found")

        request.companyName?.let { profile.companyName = it }
        request.industry?.let   { profile.industry = it }
        request.about?.let      { profile.about = it }
        request.website?.let    { profile.website = it }
        request.location?.let   { profile.location = it }

        return mapper.toResponse(companyProfileRepository.save(profile))
    }
}