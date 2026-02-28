package com.edumora.trayectoria.web.mapper

import com.edumora.trayectoria.infrastructure.persistence.entity.CompanyProfileEntity
import com.edumora.trayectoria.web.dto.response.CompanyProfileResponse
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface CompanyProfileMapper {

    @Mapping(
        target = "email",
        expression = "java(entity.getUser() != null ? entity.getUser().getEmail() : \"\")"
    )
    fun toResponse(entity: CompanyProfileEntity): CompanyProfileResponse
}