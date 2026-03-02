package com.edumora.trayectoria.web.mapper

import com.edumora.trayectoria.infrastructure.persistence.entity.JobApplicationEntity
import com.edumora.trayectoria.web.dto.response.JobApplicationResponse
import org.mapstruct.Mapper
import org.mapstruct.Mapping

/**
 * Java expressions access the entity associations.
 * JobApplicationEntity contains:
 * - candidate: CandidateProfileEntity (@ManyToOne)
 * - jobOffer:  JobOfferEntity (@ManyToOne)
 *
 * All candidate/company fields can be null if the profile
 * is incomplete — the mapper treats them as nullable.
 */
@Mapper(componentModel = "spring")
interface JobApplicationMapper {

    @Mapping(target = "jobOfferId",
        expression = "java(entity.getJobOffer() != null ? entity.getJobOffer().getId() : 0L)")
    @Mapping(target = "jobOfferTitle",
        expression = "java(entity.getJobOffer() != null ? entity.getJobOffer().getTitle() : null)")
    @Mapping(target = "companyName",
        expression = "java(entity.getJobOffer() != null && entity.getJobOffer().getCompany() != null ? entity.getJobOffer().getCompany().getCompanyName() : null)")
    @Mapping(target = "candidateId",
        expression = "java(entity.getCandidate() != null ? entity.getCandidate().getUserId() : 0L)")
    @Mapping(target = "candidateFirstName",
        expression = "java(entity.getCandidate() != null ? entity.getCandidate().getFirstName() : null)")
    @Mapping(target = "candidateLastName",
        expression = "java(entity.getCandidate() != null ? entity.getCandidate().getLastName() : null)")
    @Mapping(target = "candidateEmail",
        expression = "java(entity.getCandidate() != null && entity.getCandidate().getUser() != null ? entity.getCandidate().getUser().getEmail() : null)")
    @Mapping(target = "curriculumId",
        expression = "java(entity.getCurriculum() != null ? entity.getCurriculum().getId() : null)")
    fun toResponse(entity: JobApplicationEntity): JobApplicationResponse
}