package com.edumora.trayectoria.application.usecase.jobapplication

import com.edumora.trayectoria.infrastructure.persistence.entity.JobApplicationEntity
import com.edumora.trayectoria.infrastructure.persistence.repository.CandidateProfileRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.JobApplicationRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.JobOfferRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.exception.ConflictException
import com.edumora.trayectoria.shared.exception.UnprocessableEntityException
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.request.application.ApplyRequest
import com.edumora.trayectoria.web.dto.response.JobApplicationResponse
import com.edumora.trayectoria.web.mapper.JobApplicationMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApplyToJobOfferUseCase(
    private val userRepository: UserRepository,
    private val candidateProfileRepository: CandidateProfileRepository,
    private val jobOfferRepository: JobOfferRepository,
    private val jobApplicationRepository: JobApplicationRepository,
    private val mapper: JobApplicationMapper
) {

    @Transactional
    fun execute(email: String, jobOfferId: Long, request: ApplyRequest): JobApplicationResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        val candidate = candidateProfileRepository.findByUserId(user.id)
            .orThrow("Candidate profile not found")
        val jobOffer = jobOfferRepository.findByIdWithSkills(jobOfferId)
            .orThrow("Job offer not found: $jobOfferId")

        // Verificar que la oferta esté activa
        if (jobOffer.status != "ACTIVE") {
            throw UnprocessableEntityException(
                "Cannot apply to a ${jobOffer.status.lowercase()} job offer"
            )
        }

        // Verificar que no haya postulado antes
        if (jobApplicationRepository.existsByCandidateUserIdAndJobOfferId(user.id, jobOfferId)) {
            throw ConflictException("You have already applied to this job offer")
        }

        val application = JobApplicationEntity(
            candidate  = candidate,
            jobOffer   = jobOffer,
            status     = "PENDING",
            // curriculum se vinculará cuando el módulo de IA esté listo
            // por ahora se ignora el curriculumId del request
            curriculum = null
        )

        return mapper.toResponse(jobApplicationRepository.save(application))
    }
}