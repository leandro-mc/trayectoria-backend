package com.edumora.trayectoria.infrastructure.persistence.repository

import com.edumora.trayectoria.infrastructure.persistence.entity.InterviewMessageEntity
import org.springframework.data.jpa.repository.JpaRepository

interface InterviewMessageRepository : JpaRepository<InterviewMessageEntity, Long> {

    fun findAllByInterviewIdOrderBySentAtAsc(interviewId: Long): List<InterviewMessageEntity>
}