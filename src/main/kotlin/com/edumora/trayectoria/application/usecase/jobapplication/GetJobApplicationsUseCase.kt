package com.edumora.trayectoria.application.usecase.jobapplication

import com.edumora.trayectoria.infrastructure.persistence.repository.JobApplicationRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.JobOfferRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.exception.ForbiddenException
import com.edumora.trayectoria.shared.util.PageResponse
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.response.JobApplicationResponse
import com.edumora.trayectoria.web.mapper.JobApplicationMapper
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

/**
 * Company queries applications for ONE of its job offers.
 * We verify that the offer belongs to the authenticated company before displaying data.
 */
@Service
class GetJobApplicationsUseCase(
    private val userRepository: UserRepository,
    private val jobOfferRepository: JobOfferRepository,
    private val jobApplicationRepository: JobApplicationRepository,
    private val mapper: JobApplicationMapper
) {
    fun execute(
        email: String,
        jobOfferId: Long,
        pageable: Pageable
    ): PageResponse<JobApplicationResponse> {
        val user = userRepository.findByEmail(email).orThrow("User not found")

        // Verificar que la oferta sea de esta empresa
        if (!jobOfferRepository.existsByIdAndCompanyUserId(jobOfferId, user.id)) {
            throw ForbiddenException("Job offer not found or you don't own it")
        }

        return PageResponse.from(
            jobApplicationRepository
                .findByJobOfferIdOrderByAppliedAtDesc(jobOfferId, pageable)
                .map { mapper.toResponse(it) }
        )
    }
}
