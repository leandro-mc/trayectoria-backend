package com.edumora.trayectoria.infrastructure.persistence.repository

import com.edumora.trayectoria.infrastructure.persistence.entity.JobOfferEntity
import com.edumora.trayectoria.infrastructure.persistence.entity.SavedOfferEntity
import com.edumora.trayectoria.infrastructure.persistence.entity.SavedOfferIdEmbeddable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SavedOfferRepository : JpaRepository<SavedOfferEntity, SavedOfferIdEmbeddable> {

    fun findByCandidateUserId(candidateUserId: Long, pageable: Pageable): Page<SavedOfferEntity>

    fun existsByCandidateUserIdAndJobOfferId(candidateUserId: Long, jobOfferId: Long): Boolean

    fun deleteByCandidateUserIdAndJobOfferId(candidateUserId: Long, jobOfferId: Long)

    @Query("SELECT s.jobOffer FROM SavedOfferEntity s WHERE s.id.candidateId = :userId")
    fun findJobOffersByCandidateUserId(
        @Param("userId") userId: Long, pageable: Pageable
    ): Page<JobOfferEntity>

    fun existsByIdCandidateIdAndIdJobOfferId(
        candidateId: Long, jobOfferId: Long
    ): Boolean

//    @Transactional
//    @Modifying
//    @Query("DELETE FROM SavedOfferEntity s WHERE s.id.candidateUserId = :candidateId AND s.id.jobOfferId = :jobOfferId")
//    fun deleteByCandidateUserIdAndJobOfferId(@Param("candidateId") candidateId: Long, @Param("jobOfferId") jobOfferId: Long)
}