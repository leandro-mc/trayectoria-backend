package com.edumora.trayectoria.web.mapper

import com.edumora.trayectoria.infrastructure.persistence.entity.JobOfferEntity
import com.edumora.trayectoria.web.dto.response.JobOfferResponse
import com.edumora.trayectoria.web.dto.response.JobOfferSummaryResponse
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring", uses = [CandidateProfileMapper::class])
interface JobOfferMapper {

    @Mapping(target = "companyId",   expression = "java(entity.getCompany() != null ? entity.getCompany().getUserId() : 0L)")
    @Mapping(target = "companyName", expression = "java(entity.getCompany() != null ? entity.getCompany().getCompanyName() : null)")
    @Mapping(target = "skills",      expression = "java(mapSkills(entity.getSkills()))")
    fun toResponse(entity: JobOfferEntity): JobOfferResponse

    @Mapping(target = "companyName", expression = "java(entity.getCompany() != null ? entity.getCompany().getCompanyName() : null)")
    @Mapping(target = "skills",      expression = "java(mapSkills(entity.getSkills()))")
    fun toSummaryResponse(entity: JobOfferEntity): JobOfferSummaryResponse

    fun mapSkills(skills: Set<com.edumora.trayectoria.infrastructure.persistence.entity.SkillEntity>): List<com.edumora.trayectoria.web.dto.response.SkillResponse>
}