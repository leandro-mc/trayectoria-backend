package com.edumora.trayectoria.application.usecase.candidate

import com.edumora.trayectoria.infrastructure.persistence.entity.EducationEntity
import com.edumora.trayectoria.infrastructure.persistence.repository.CandidateProfileRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.EducationRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.exception.ForbiddenException
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.request.candidate.EducationRequest
import com.edumora.trayectoria.web.dto.response.EducationResponse
import com.edumora.trayectoria.web.mapper.CandidateProfileMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EducationUseCase(
    private val userRepository: UserRepository,
    private val candidateProfileRepository: CandidateProfileRepository,
    private val educationRepository: EducationRepository,
    private val mapper: CandidateProfileMapper
) {
    @Transactional
    fun add(email: String, request: EducationRequest): EducationResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        val profile = candidateProfileRepository.findByUserId(user.id)
            .orThrow("Candidate profile not found")

        val entity = EducationEntity(
            candidate    = profile,
            institution  = request.institution,
            degree       = request.degree,
            fieldOfStudy = request.fieldOfStudy,
            startDate    = request.startDate,
            endDate      = request.endDate
        )
        return mapper.toEducationResponse(educationRepository.save(entity))
    }

    @Transactional
    fun update(email: String, id: Long, request: EducationRequest): EducationResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        verifyOwnership(id, user.id)

        val entity = educationRepository.findById(id).orThrow("Education not found: $id")
        entity.institution  = request.institution
        entity.degree       = request.degree
        entity.fieldOfStudy = request.fieldOfStudy
        entity.startDate    = request.startDate
        entity.endDate      = request.endDate

        return mapper.toEducationResponse(educationRepository.save(entity))
    }

    @Transactional
    fun delete(email: String, id: Long) {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        verifyOwnership(id, user.id)
        educationRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun list(email: String): List<EducationResponse> {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        return educationRepository.findAllByCandidateUserId(user.id)
            .map { mapper.toEducationResponse(it) }
    }

    private fun verifyOwnership(educationId: Long, userId: Long) {
        if (!educationRepository.existsByIdAndCandidateUserId(educationId, userId)) {
            throw ForbiddenException("You don't own this education entry")
        }
    }
}