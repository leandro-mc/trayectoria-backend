package com.edumora.trayectoria.infrastructure.persistence.repository

import com.edumora.trayectoria.infrastructure.persistence.entity.JobApplicationEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface JobApplicationRepository : JpaRepository<JobApplicationEntity, Long> {

    fun findByCandidateUserId(candidateUserId: Long, pageable: Pageable): Page<JobApplicationEntity>

    fun findByJobOfferIdAndJobOfferCompanyUserId(
        jobOfferId: Long,
        companyUserId: Long,
        pageable: Pageable
    ): Page<JobApplicationEntity>

    // Prevents duplicate applications
    fun existsByCandidateUserIdAndJobOfferId(candidateUserId: Long, jobOfferId: Long): Boolean

    // Ownership check for candidate
    fun findByIdAndCandidateUserId(id: Long, candidateUserId: Long): Optional<JobApplicationEntity>

    // Ownership check for company
    @Query("""
        SELECT a FROM JobApplicationEntity a 
        WHERE a.id = :id AND a.jobOffer.company.userId = :companyUserId
    """)
    fun findByIdAndCompanyUserId(id: Long, companyUserId: Long): Optional<JobApplicationEntity>
}