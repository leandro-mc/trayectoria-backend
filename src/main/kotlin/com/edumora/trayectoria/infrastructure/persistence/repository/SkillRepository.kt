package com.edumora.trayectoria.infrastructure.persistence.repository

import com.edumora.trayectoria.infrastructure.persistence.entity.SkillEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface SkillRepository : JpaRepository<SkillEntity, Long> {

    fun findByNameIgnoreCase(name: String): Optional<SkillEntity>

    fun existsByNameIgnoreCase(name: String): Boolean

    // Find all skills whose name contains the search term (for autocomplete)
    fun findByNameContainingIgnoreCase(name: String): List<SkillEntity>

    @Query("SELECT s FROM SkillEntity s WHERE s.id IN :ids")
    fun findAllByIdIn(ids: List<Long>): List<SkillEntity>
}