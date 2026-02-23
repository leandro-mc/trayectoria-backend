package com.edumora.trayectoria.infrastructure.persistence.repository

import com.edumora.trayectoria.infrastructure.persistence.entity.CompanyProfileEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface CompanyProfileRepository : JpaRepository<CompanyProfileEntity, Long> {

    fun findByUserId(userId: Long): Optional<CompanyProfileEntity>
}