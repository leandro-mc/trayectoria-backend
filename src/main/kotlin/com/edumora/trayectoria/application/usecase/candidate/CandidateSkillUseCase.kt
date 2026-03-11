package com.edumora.trayectoria.application.usecase.candidate

import com.edumora.trayectoria.infrastructure.persistence.repository.CandidateProfileRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.SkillRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.exception.NotFoundException
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.request.candidate.SkillsRequest
import com.edumora.trayectoria.web.dto.response.SkillResponse
import com.edumora.trayectoria.web.mapper.CandidateProfileMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CandidateSkillUseCase(
    private val userRepository: UserRepository,
    private val candidateProfileRepository: CandidateProfileRepository,
    private val skillRepository: SkillRepository,
    private val mapper: CandidateProfileMapper
) {
    @Transactional(readOnly = true)
    fun list(email: String): List<SkillResponse> {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        val profile = candidateProfileRepository.findByUserIdWithSkills(user.id)
            .orThrow("Candidate profile not found")
        return profile.skills.map { mapper.toSkillResponse(it) }
    }

    @Transactional
    fun addSkills(email: String, request: SkillsRequest): List<SkillResponse> {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        val profile = candidateProfileRepository.findByUserIdWithSkills(user.id)
            .orThrow("Candidate profile not found")

        val skills = skillRepository.findAllByIdIn(request.skillIds)
        if (skills.size != request.skillIds.size) {
            throw NotFoundException("One or more skills not found")
        }

        profile.skills.addAll(skills)
        candidateProfileRepository.save(profile)
        return profile.skills.map { mapper.toSkillResponse(it) }
    }

    @Transactional
    fun removeSkill(email: String, skillId: Long) {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        val profile = candidateProfileRepository.findByUserIdWithSkills(user.id)
            .orThrow("Candidate profile not found")

        profile.skills.removeIf { it.id == skillId }
        candidateProfileRepository.save(profile)
    }
}