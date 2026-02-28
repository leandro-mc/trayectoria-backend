package com.edumora.trayectoria.web.mapper

import com.edumora.trayectoria.infrastructure.persistence.entity.CandidateLanguageEmbeddable
import com.edumora.trayectoria.infrastructure.persistence.entity.CandidateProfileEntity
import com.edumora.trayectoria.infrastructure.persistence.entity.EducationEntity
import com.edumora.trayectoria.infrastructure.persistence.entity.SkillEntity
import com.edumora.trayectoria.infrastructure.persistence.entity.WorkExperienceEntity
import com.edumora.trayectoria.web.dto.response.CandidateProfileResponse
import com.edumora.trayectoria.web.dto.response.EducationResponse
import com.edumora.trayectoria.web.dto.response.LanguageResponse
import com.edumora.trayectoria.web.dto.response.SkillResponse
import com.edumora.trayectoria.web.dto.response.WorkExperienceResponse
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface CandidateProfileMapper {

    @Mapping(target = "email",   expression = "java(entity.getUser() != null ? entity.getUser().getEmail() : \"\")")
    @Mapping(target = "skills",  expression = "java(mapSkills(entity.getSkills()))")
    @Mapping(target = "languages", expression = "java(mapLanguages(entity.getLanguages()))")
    fun toResponse(entity: CandidateProfileEntity): CandidateProfileResponse

    fun toWorkExperienceResponse(entity: WorkExperienceEntity): WorkExperienceResponse

    fun toEducationResponse(entity: EducationEntity): EducationResponse

    @Mapping(target = "language", source = "language")
    @Mapping(target = "level",    source = "level")
    fun toLanguageResponse(embeddable: CandidateLanguageEmbeddable): LanguageResponse

    fun toSkillResponse(entity: SkillEntity): SkillResponse

    // MapStruct no puede inferir Set<SkillEntity> -> List<SkillResponse> solo
    // declaramos el metodo y MapStruct lo implementa usando toSkillResponse()
    fun mapSkills(skills: Set<SkillEntity>): List<SkillResponse>

    fun mapLanguages(languages: List<CandidateLanguageEmbeddable>): List<LanguageResponse>
}