package com.edumora.trayectoria.application.usecase.company

import com.edumora.trayectoria.infrastructure.persistence.repository.JobApplicationRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.JobOfferRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.response.CompanyStatsResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetStatsUseCase(
    private val userRepository: UserRepository,
    private val jobOfferRepository: JobOfferRepository,
    private val jobApplicationRepository: JobApplicationRepository,
) {
    @Transactional(readOnly = true)
    fun execute(email: String): CompanyStatsResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        return CompanyStatsResponse(
            activeOffers        = jobOfferRepository.countByCompanyUserIdAndStatus(user.id, "ACTIVE"),
            totalApplications   = jobApplicationRepository.countByJobOfferCompanyUserId(user.id),
            pendingApplications = jobApplicationRepository.countByJobOfferCompanyUserIdAndStatus(user.id, "PENDING")
        )
    }
}