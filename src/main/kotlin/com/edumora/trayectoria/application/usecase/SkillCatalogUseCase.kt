package com.edumora.trayectoria.application.usecase

import com.edumora.trayectoria.infrastructure.persistence.repository.SkillRepository
import com.edumora.trayectoria.web.dto.response.SkillResponse
import com.edumora.trayectoria.web.mapper.CandidateProfileMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SkillCatalogUseCase(
    private val skillRepository: SkillRepository,
    private val mapper: CandidateProfileMapper
) {
    @Transactional(readOnly = true)
    fun listAll(): List<SkillResponse> =
        skillRepository.findAll().map { mapper.toSkillResponse(it) }

    @Transactional(readOnly = true)
    fun search(name: String): List<SkillResponse> =
        skillRepository.findByNameContainingIgnoreCase(name)
            .map { mapper.toSkillResponse(it) }
}