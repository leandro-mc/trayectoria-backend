package com.edumora.trayectoria.application.usecase

import com.edumora.trayectoria.infrastructure.persistence.repository.CandidateProfileRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.request.UpdateCandidateProfileRequest
import com.edumora.trayectoria.web.dto.response.CandidateProfileResponse
import com.edumora.trayectoria.web.mapper.CandidateProfileMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateCandidateProfileUseCase(
    private val userRepository: UserRepository,
    private val candidateProfileRepository: CandidateProfileRepository,
    private val mapper: CandidateProfileMapper
) {
    @Transactional
    fun execute(email: String, request: UpdateCandidateProfileRequest): CandidateProfileResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found: $email")
        val profile = candidateProfileRepository.findByUserId(user.id)
            .orThrow("Candidate profile not found")

        // Solo actualizamos los campos que llegan — los null se ignoran
        request.firstName?.let    { profile.firstName = it }
        request.lastName?.let     { profile.lastName = it }
        request.phone?.let        { profile.phone = it }
        request.location?.let     { profile.location = it }
        request.bio?.let          { profile.bio = it }
        request.linkedinUrl?.let  { profile.linkedinUrl = it }
        request.githubUrl?.let    { profile.githubUrl = it }
        request.portfolioUrl?.let { profile.portfolioUrl = it }
        request.birthdate?.let    { profile.birthdate = it }

        return mapper.toResponse(candidateProfileRepository.save(profile))
    }
}