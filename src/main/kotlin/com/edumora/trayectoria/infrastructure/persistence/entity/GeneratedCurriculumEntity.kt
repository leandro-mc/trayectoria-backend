package com.edumora.trayectoria.infrastructure.persistence.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity
@Table(name = "generated_curriculum")
class GeneratedCurriculumEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    var candidate: CandidateProfileEntity? = null,

    // nullable: if null, this is a base curriculum not tied to any offer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_offer_id")
    var jobOffer: JobOfferEntity? = null,

    // Stored as JSONB in PostgreSQL — structured AI-generated content
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    var content: CurriculumContent = CurriculumContent(),

    @Column(name = "is_ai_generated", nullable = false)
    var isAiGenerated: Boolean = true,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)