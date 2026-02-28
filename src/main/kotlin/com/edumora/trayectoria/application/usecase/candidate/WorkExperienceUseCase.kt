package com.edumora.trayectoria.application.usecase.candidate

import com.edumora.trayectoria.infrastructure.persistence.entity.WorkExperienceEntity
import com.edumora.trayectoria.infrastructure.persistence.repository.CandidateProfileRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.WorkExperienceRepository
import com.edumora.trayectoria.shared.exception.ForbiddenException
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.request.candidate.WorkExperienceRequest
import com.edumora.trayectoria.web.dto.response.WorkExperienceResponse
import com.edumora.trayectoria.web.mapper.CandidateProfileMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WorkExperienceUseCase(
    private val userRepository: UserRepository,
    private val candidateProfileRepository: CandidateProfileRepository,
    private val workExperienceRepository: WorkExperienceRepository,
    private val mapper: CandidateProfileMapper
) {
    @Transactional
    fun add(email: String, request: WorkExperienceRequest): WorkExperienceResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        val profile = candidateProfileRepository.findByUserId(user.id)
            .orThrow("Candidate profile not found")

        val entity = WorkExperienceEntity(
            candidate   = profile,
            company     = request.company,
            position    = request.position,
            description = request.description,
            startDate   = request.startDate,
            endDate     = request.endDate,
            isCurrent   = request.isCurrent
        )
        return mapper.toWorkExperienceResponse(workExperienceRepository.save(entity))
    }

    @Transactional
    fun update(email: String, id: Long, request: WorkExperienceRequest): WorkExperienceResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        verifyOwnership(id, user.id)

        val entity = workExperienceRepository.findById(id).orThrow("Work experience not found: $id")
        entity.company     = request.company
        entity.position    = request.position
        entity.description = request.description
        entity.startDate   = request.startDate
        entity.endDate     = request.endDate
        entity.isCurrent   = request.isCurrent

        return mapper.toWorkExperienceResponse(workExperienceRepository.save(entity))
    }

    @Transactional
    fun delete(email: String, id: Long) {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        verifyOwnership(id, user.id)
        workExperienceRepository.deleteById(id)
    }

    fun list(email: String): List<WorkExperienceResponse> {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        return workExperienceRepository.findAllByCandidateUserId(user.id)
            .map { mapper.toWorkExperienceResponse(it) }
    }

    private fun verifyOwnership(experienceId: Long, userId: Long) {
        if (!workExperienceRepository.existsByIdAndCandidateUserId(experienceId, userId)) {
            throw ForbiddenException("You don't own this work experience")
        }
    }
}