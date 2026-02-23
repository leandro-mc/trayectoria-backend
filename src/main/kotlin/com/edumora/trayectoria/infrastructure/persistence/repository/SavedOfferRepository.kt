package com.edumora.trayectoria.infrastructure.persistence.repository

import com.edumora.trayectoria.infrastructure.persistence.entity.SavedOfferEntity
import com.edumora.trayectoria.infrastructure.persistence.entity.SavedOfferIdEmbeddable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface SavedOfferRepository : JpaRepository<SavedOfferEntity, SavedOfferIdEmbeddable> {

    fun findByCandidateUserId(candidateUserId: Long, pageable: Pageable): Page<SavedOfferEntity>

    fun existsByCandidateUserIdAndJobOfferId(candidateUserId: Long, jobOfferId: Long): Boolean

    fun deleteByCandidateUserIdAndJobOfferId(candidateUserId: Long, jobOfferId: Long)
}