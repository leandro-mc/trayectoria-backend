package com.edumora.trayectoria.application.usecase

import com.edumora.trayectoria.infrastructure.persistence.repository.CandidateProfileRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.response.CandidateProfileResponse
import com.edumora.trayectoria.web.mapper.CandidateProfileMapper
import org.springframework.stereotype.Service

@Service
class GetCandidateProfileUseCase(
    private val userRepository: UserRepository,
    private val candidateProfileRepository: CandidateProfileRepository,
    private val mapper: CandidateProfileMapper
) {
    fun execute(email: String): CandidateProfileResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found: $email")
        val profile = candidateProfileRepository.findByUserIdWithSkills(user.id)
            .orThrow("Candidate profile not found")
        return mapper.toResponse(profile)
    }
}