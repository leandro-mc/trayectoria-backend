package com.edumora.trayectoria.application.usecase.ai

import com.edumora.trayectoria.infrastructure.persistence.repository.GeneratedCurriculumRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.exception.ForbiddenException
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.response.GeneratedCurriculumResponse
import com.edumora.trayectoria.web.mapper.CurriculumMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetCurriculaUseCase(
    private val userRepository: UserRepository,
    private val curriculumRepository: GeneratedCurriculumRepository,
    private val mapper: CurriculumMapper
) {
    @Transactional(readOnly = true)
    fun listAll(email: String): List<GeneratedCurriculumResponse> {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        return curriculumRepository.findByCandidateUserId(user.id)
            .map { mapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getById(email: String, id: Long): GeneratedCurriculumResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        val curriculum = curriculumRepository.findById(id).orThrow("Curriculum not found: $id")
        if (curriculum.candidate?.userId != user.id) {
            throw ForbiddenException("You don't own this curriculum")
        }
        return mapper.toResponse(curriculum)
    }

    @Transactional
    fun delete(email: String, id: Long) {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        val curriculum = curriculumRepository.findById(id).orThrow("Curriculum not found: $id")
        if (curriculum.candidate?.userId != user.id) {
            throw ForbiddenException("You don't own this curriculum")
        }
        curriculumRepository.deleteById(id)
    }
}
