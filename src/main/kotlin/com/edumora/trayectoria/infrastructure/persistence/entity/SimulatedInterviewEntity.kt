package com.edumora.trayectoria.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "simulated_interview")
class SimulatedInterviewEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    var candidate: CandidateProfileEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_offer_id", nullable = false)
    var jobOffer: JobOfferEntity? = null,

    // IN_PROGRESS, COMPLETED
    @Column(nullable = false, length = 20)
    var status: String = "IN_PROGRESS",

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "completed_at")
    var completedAt: LocalDateTime? = null,

    @OneToMany(mappedBy = "interview", cascade = [CascadeType.ALL], orphanRemoval = true)
    var messages: MutableList<InterviewMessageEntity> = mutableListOf()
)