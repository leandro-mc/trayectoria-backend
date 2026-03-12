package com.edumora.trayectoria.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "work_experience")
class WorkExperienceEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    /**
     * @ManyToOne  -> Many experiences belong to one candidate
     * FetchType.LAZY -> candidate is NOT loaded unless explicitly accessed (performance)
     * @JoinColumn -> specifies the FK column name in this table
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    var candidate: CandidateProfileEntity? = null,

    @Column(length = 150)
    var company: String? = null,

    @Column(length = 150)
    var position: String? = null,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "start_date")
    var startDate: LocalDate? = null,

    @Column(name = "end_date")
    var endDate: LocalDate? = null,

    @Column(name = "is_current", nullable = false)
    @get:JvmName("getIsCurrent")
    var isCurrent: Boolean = false
)