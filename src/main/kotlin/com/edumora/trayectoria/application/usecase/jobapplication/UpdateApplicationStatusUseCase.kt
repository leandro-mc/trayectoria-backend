package com.edumora.trayectoria.application.usecase.jobapplication

import com.edumora.trayectoria.infrastructure.persistence.repository.JobApplicationRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.exception.ForbiddenException
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.request.application.UpdateApplicationStatusRequest
import com.edumora.trayectoria.web.dto.response.JobApplicationResponse
import com.edumora.trayectoria.web.mapper.JobApplicationMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Only the company owning the job offer can change the status.
 * Candidates cannot modify the status (they can only withdraw their application).
 */
@Service
class UpdateApplicationStatusUseCase(
    private val userRepository: UserRepository,
    private val jobApplicationRepository: JobApplicationRepository,
    private val mapper: JobApplicationMapper
) {
    @Transactional
    fun execute(
        email: String,
        applicationId: Long,
        request: UpdateApplicationStatusRequest
    ): JobApplicationResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        val application = jobApplicationRepository.findById(applicationId)
            .orThrow("Application not found: $applicationId")

        // Verificar que la oferta sea de la empresa autenticada
        val jobOffer = application.jobOffer
            ?: throw ForbiddenException("Application has no associated job offer")

        if (jobOffer.company?.userId != user.id) {
            throw ForbiddenException("You don't own the job offer of this application")
        }

        application.status = request.status
        return mapper.toResponse(jobApplicationRepository.save(application))
    }
}
