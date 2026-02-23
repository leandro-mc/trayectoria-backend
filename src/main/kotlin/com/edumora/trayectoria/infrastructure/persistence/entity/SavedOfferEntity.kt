package com.edumora.trayectoria.infrastructure.persistence.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime

/**
 * Composite PK via @EmbeddedId.
 * Alternative to @IdClass - both are valid, @EmbeddedId is more explicit.
 */
@Entity
@Table(name = "saved_offer")
class SavedOfferEntity(

    @EmbeddedId
    var id: SavedOfferIdEmbeddable = SavedOfferIdEmbeddable(),

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("candidateId")
    @JoinColumn(name = "candidate_id")
    var candidate: CandidateProfileEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("jobOfferId")
    @JoinColumn(name = "job_offer_id")
    var jobOffer: JobOfferEntity? = null,

    @Column(name = "saved_at", nullable = false, updatable = false)
    var savedAt: LocalDateTime = LocalDateTime.now()
)

@Embeddable
class SavedOfferIdEmbeddable(
    @Column(name = "candidate_id")
    var candidateId: Long = 0,
    @Column(name = "job_offer_id")
    var jobOfferId: Long = 0
) : Serializable