package com.edumora.trayectoria.web.mapper

import com.edumora.trayectoria.infrastructure.persistence.entity.GeneratedCurriculumEntity
import com.edumora.trayectoria.web.dto.response.GeneratedCurriculumResponse
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface CurriculumMapper {

    @Mapping(
        target = "jobOfferId",
        expression = "java(entity.getJobOffer() != null ? entity.getJobOffer().getId() : null)"
    )
    @Mapping(
        target = "jobOfferTitle",
        expression = "java(entity.getJobOffer() != null ? entity.getJobOffer().getTitle() : null)"
    )
    fun toResponse(entity: GeneratedCurriculumEntity): GeneratedCurriculumResponse
}