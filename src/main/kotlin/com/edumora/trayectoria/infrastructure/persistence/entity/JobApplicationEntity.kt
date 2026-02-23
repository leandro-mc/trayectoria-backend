package com.edumora.trayectoria.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "job_application",
    uniqueConstraints = [UniqueConstraint(columnNames = ["candidate_id", "job_offer_id"])]
)
class JobApplicationEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    var candidate: CandidateProfileEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_offer_id", nullable = false)
    var jobOffer: JobOfferEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id")
    var curriculum: GeneratedCurriculumEntity? = null,

    // PENDING, VIEWED, IN_REVIEW, ACCEPTED, REJECTED
    @Column(nullable = false, length = 20)
    var status: String = "PENDING",

    @Column(name = "applied_at", nullable = false, updatable = false)
    var appliedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
)