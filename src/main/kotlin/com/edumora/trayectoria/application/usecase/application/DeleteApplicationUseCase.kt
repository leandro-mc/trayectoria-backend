package com.edumora.trayectoria.application.usecase.application

import com.edumora.trayectoria.infrastructure.persistence.repository.JobApplicationRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.exception.ForbiddenException
import com.edumora.trayectoria.shared.exception.UnprocessableEntityException
import com.edumora.trayectoria.shared.util.orThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteApplicationUseCase(
    private val userRepository: UserRepository,
    private val jobApplicationRepository: JobApplicationRepository
) {
    @Transactional
    fun execute(email: String, applicationId: Long) {
        val user = userRepository.findByEmail(email).orThrow("User not found")

        if (!jobApplicationRepository.existsByIdAndCandidateUserId(applicationId, user.id)) {
            throw ForbiddenException("Application not found or you don't own it")
        }

        val application = jobApplicationRepository.findById(applicationId)
            .orThrow("Application not found: $applicationId")

        // No se puede retirar si ya fue aceptada o rechazada
        if (application.status in listOf("ACCEPTED", "REJECTED")) {
            throw UnprocessableEntityException(
                "Cannot withdraw a ${application.status.lowercase()} application"
            )
        }

        jobApplicationRepository.deleteById(applicationId)
    }
}