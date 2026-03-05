package com.edumora.trayectoria.infrastructure.persistence.repository

import com.edumora.trayectoria.infrastructure.persistence.entity.GeneratedCurriculumEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface GeneratedCurriculumRepository : JpaRepository<GeneratedCurriculumEntity, Long> {

    fun findAllByCandidateUserId(candidateUserId: Long): List<GeneratedCurriculumEntity>

    fun findByIdAndCandidateUserId(id: Long, candidateUserId: Long): Optional<GeneratedCurriculumEntity>

    fun existsByIdAndCandidateUserId(id: Long, candidateUserId: Long): Boolean

    fun findByCandidateUserId(candidateUserId: Long): List<GeneratedCurriculumEntity>
}