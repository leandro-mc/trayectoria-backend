package com.edumora.trayectoria.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "interview_message")
class InterviewMessageEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    var interview: SimulatedInterviewEntity? = null,

    // USER or ASSISTANT
    @Column(nullable = false, length = 20)
    var role: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String = "",

    @Column(name = "sent_at", nullable = false, updatable = false)
    var sentAt: LocalDateTime = LocalDateTime.now()
)