package com.edumora.trayectoria.application.usecase.candidate

import com.edumora.trayectoria.infrastructure.persistence.entity.CandidateLanguageEmbeddable
import com.edumora.trayectoria.infrastructure.persistence.repository.CandidateProfileRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.exception.ConflictException
import com.edumora.trayectoria.shared.exception.NotFoundException
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.request.candidate.LanguageRequest
import com.edumora.trayectoria.web.dto.response.LanguageResponse
import com.edumora.trayectoria.web.mapper.CandidateProfileMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CandidateLanguageUseCase(
    private val userRepository: UserRepository,
    private val candidateProfileRepository: CandidateProfileRepository,
    private val mapper: CandidateProfileMapper
) {
    @Transactional(readOnly = true)
    fun list(email: String): List<LanguageResponse> {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        val profile = candidateProfileRepository.findByUserId(user.id)
            .orThrow("Candidate profile not found")
        return profile.languages.map { mapper.toLanguageResponse(it) }
    }

    @Transactional
    fun add(email: String, request: LanguageRequest): LanguageResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        val profile = candidateProfileRepository.findByUserId(user.id)
            .orThrow("Candidate profile not found")

        val alreadyExists = profile.languages.any {
            it.language.equals(request.language, ignoreCase = true)
        }
        if (alreadyExists) throw ConflictException("Language already added: ${request.language}")

        val embeddable = CandidateLanguageEmbeddable(
            language = request.language,
            level    = request.level
        )
        profile.languages.add(embeddable)
        candidateProfileRepository.save(profile)
        return mapper.toLanguageResponse(embeddable)
    }

    @Transactional
    fun remove(email: String, language: String) {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        val profile = candidateProfileRepository.findByUserId(user.id)
            .orThrow("Candidate profile not found")

        val removed = profile.languages.removeIf {
            it.language.equals(language, ignoreCase = true)
        }
        if (!removed) throw NotFoundException("Language not found: $language")
        candidateProfileRepository.save(profile)
    }
}
