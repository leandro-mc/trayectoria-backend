package com.edumora.trayectoria.application.usecase

import com.edumora.trayectoria.infrastructure.persistence.repository.SkillRepository
import com.edumora.trayectoria.web.dto.response.SkillResponse
import com.edumora.trayectoria.web.mapper.CandidateProfileMapper
import org.springframework.stereotype.Service

@Service
class SkillCatalogUseCase(
    private val skillRepository: SkillRepository,
    private val mapper: CandidateProfileMapper
) {
    fun listAll(): List<SkillResponse> =
        skillRepository.findAll().map { mapper.toSkillResponse(it) }

    fun search(name: String): List<SkillResponse> =
        skillRepository.findByNameContainingIgnoreCase(name)
            .map { mapper.toSkillResponse(it) }
}