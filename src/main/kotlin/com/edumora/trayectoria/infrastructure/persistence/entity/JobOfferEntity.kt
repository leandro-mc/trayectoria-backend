package com.edumora.trayectoria.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "job_offer")
class JobOfferEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    var company: CompanyProfileEntity? = null,

    @Column(nullable = false, length = 255)
    var title: String = "",

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(columnDefinition = "TEXT")
    var responsibilities: String? = null,

    @Column(columnDefinition = "TEXT")
    var requirements: String? = null,

    @Column(columnDefinition = "TEXT")
    var benefits: String? = null,

    // REMOTE, HYBRID, ON_SITE
    @Column(name = "work_mode", length = 20)
    var workMode: String? = null,

    // FULL_TIME, PART_TIME, INTERNSHIP
    @Column(name = "job_type", length = 20)
    var jobType: String? = null,

    // ACTIVE, CLOSED, DRAFT
    @Column(nullable = false, length = 20)
    var status: String = "ACTIVE",

    @Column(length = 255)
    var location: String? = null,

    //  Instructions for the AI-generated interview
    @Column(name = "interview_instructions", columnDefinition = "TEXT")
    var interviewInstructions: String? = null,

    @Column(name = "requires_interview", nullable = false)
    var requiresInterview: Boolean = false,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "expires_at")
    var expiresAt: LocalDateTime? = null,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "job_offer_skill",
        joinColumns = [JoinColumn(name = "job_offer_id")],
        inverseJoinColumns = [JoinColumn(name = "skill_id")]
    )
    var skills: MutableSet<SkillEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "jobOffer", cascade = [CascadeType.ALL], orphanRemoval = true)
    var applications: MutableList<JobApplicationEntity> = mutableListOf()
)