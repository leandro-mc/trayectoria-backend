package com.edumora.trayectoria.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

/**
 * @Embeddable -> This class has no PK of its own.
 * It is stored inside a @CollectionTable in CandidateProfileEntity.
 * Use when the data only makes sense in the context of its parent.
 */
@Embeddable
class CandidateLanguageEmbeddable(

    @Column(nullable = false, length = 100)
    var language: String = "",

    // A1, A2, B1, B2, C1, C2, Native
    @Column(length = 10)
    var level: String? = null
)