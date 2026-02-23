package com.edumora.trayectoria.infrastructure.persistence.repository

import com.edumora.trayectoria.infrastructure.persistence.entity.JobOfferEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import java.util.Optional

/**
 * JpaSpecificationExecutor allows dynamic filtering via Specification<T>.
 * This is the clean way to handle optional query params like
 * ?workMode=REMOTE&jobType=INTERNSHIP&skill=Java
 * without writing a different method for every filter combination.
 * The Specification implementation goes in the use case layer.
 */
interface JobOfferRepository : JpaRepository<JobOfferEntity, Long>,
    JpaSpecificationExecutor<JobOfferEntity> {

    // Pageable -> Spring handles LIMIT/OFFSET automatically
    // Returns Page<T> which includes total count, total pages, current page
    fun findByStatus(status: String, pageable: Pageable): Page<JobOfferEntity>

    fun findByCompanyUserId(companyUserId: Long, pageable: Pageable): Page<JobOfferEntity>

    fun existsByIdAndCompanyUserId(id: Long, companyUserId: Long): Boolean

    @Query("""
        SELECT jo FROM JobOfferEntity jo 
        LEFT JOIN FETCH jo.skills 
        WHERE jo.id = :id
    """)
    fun findByIdWithSkills(id: Long): Optional<JobOfferEntity>
}