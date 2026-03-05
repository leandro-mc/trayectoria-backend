package com.edumora.trayectoria.application.usecase.jobapplication

import com.edumora.trayectoria.infrastructure.persistence.repository.JobApplicationRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.util.PageResponse
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.response.JobApplicationResponse
import com.edumora.trayectoria.web.mapper.JobApplicationMapper
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class GetMyApplicationsUseCase(
    private val userRepository: UserRepository,
    private val jobApplicationRepository: JobApplicationRepository,
    private val mapper: JobApplicationMapper
) {
    fun execute(email: String, pageable: Pageable): PageResponse<JobApplicationResponse> {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        return PageResponse.from(
            jobApplicationRepository
                .findByCandidateUserIdOrderByAppliedAtDesc(user.id, pageable)
                .map { mapper.toResponse(it) }
        )
    }
}