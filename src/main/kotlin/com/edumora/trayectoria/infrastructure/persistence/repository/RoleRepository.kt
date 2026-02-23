package com.edumora.trayectoria.infrastructure.persistence.repository

import com.edumora.trayectoria.infrastructure.persistence.entity.RoleEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface RoleRepository : JpaRepository<RoleEntity, Long> {

    // Used during registration to assign the correct role
    fun findByName(name: String): Optional<RoleEntity>
}