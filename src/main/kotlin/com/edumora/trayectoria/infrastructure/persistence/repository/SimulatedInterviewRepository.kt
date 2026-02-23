package com.edumora.trayectoria.infrastructure.persistence.repository

import com.edumora.trayectoria.infrastructure.persistence.entity.SimulatedInterviewEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface SimulatedInterviewRepository : JpaRepository<SimulatedInterviewEntity, Long> {

    fun findAllByCandidateUserId(candidateUserId: Long): List<SimulatedInterviewEntity>

    fun findByIdAndCandidateUserId(id: Long, candidateUserId: Long): Optional<SimulatedInterviewEntity>

    // Fetch with messages to render full interview history
    @Query("""
        SELECT i FROM SimulatedInterviewEntity i 
        LEFT JOIN FETCH i.messages 
        WHERE i.id = :id AND i.candidate.userId = :candidateUserId
        ORDER BY i.id
    """)
    fun findByIdWithMessages(id: Long, candidateUserId: Long): Optional<SimulatedInterviewEntity>
}