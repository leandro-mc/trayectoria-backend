package com.edumora.trayectoria.infrastructure.persistence.repository

import com.edumora.trayectoria.infrastructure.persistence.entity.CandidateProfileEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface CandidateProfileRepository : JpaRepository<CandidateProfileEntity, Long> {

    // userId IS the PK here, but this makes intent explicit in use cases
    fun findByUserId(userId: Long): Optional<CandidateProfileEntity>

    // Fetch with skills in one query to avoid N+1
    @Query("SELECT c FROM CandidateProfileEntity c LEFT JOIN FETCH c.skills WHERE c.userId = :userId")
    fun findByUserIdWithSkills(userId: Long): Optional<CandidateProfileEntity>
}