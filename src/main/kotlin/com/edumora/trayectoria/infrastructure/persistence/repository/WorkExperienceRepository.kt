package com.edumora.trayectoria.infrastructure.persistence.repository

import com.edumora.trayectoria.infrastructure.persistence.entity.WorkExperienceEntity
import org.springframework.data.jpa.repository.JpaRepository

interface WorkExperienceRepository : JpaRepository<WorkExperienceEntity, Long> {

    fun findAllByCandidateUserId(candidateUserId: Long): List<WorkExperienceEntity>

    // Verify ownership before update/delete (security check)
    fun existsByIdAndCandidateUserId(id: Long, candidateUserId: Long): Boolean
}