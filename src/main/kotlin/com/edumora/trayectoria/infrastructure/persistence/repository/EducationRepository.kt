package com.edumora.trayectoria.infrastructure.persistence.repository

import com.edumora.trayectoria.infrastructure.persistence.entity.EducationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface EducationRepository : JpaRepository<EducationEntity, Long> {

    fun findAllByCandidateUserId(candidateUserId: Long): List<EducationEntity>

    fun existsByIdAndCandidateUserId(id: Long, candidateUserId: Long): Boolean
}