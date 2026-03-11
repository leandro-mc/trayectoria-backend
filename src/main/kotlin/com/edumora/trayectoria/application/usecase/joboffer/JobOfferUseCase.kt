package com.edumora.trayectoria.application.usecase.joboffer

import com.edumora.trayectoria.infrastructure.persistence.entity.JobOfferEntity
import com.edumora.trayectoria.infrastructure.persistence.repository.CompanyProfileRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.JobOfferRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.JobOfferSpecification
import com.edumora.trayectoria.infrastructure.persistence.repository.SkillRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.exception.ForbiddenException
import com.edumora.trayectoria.shared.util.PageResponse
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.request.joboffer.CreateJobOfferRequest
import com.edumora.trayectoria.web.dto.request.joboffer.UpdateJobOfferStatusRequest
import com.edumora.trayectoria.web.dto.response.JobOfferResponse
import com.edumora.trayectoria.web.dto.response.JobOfferSummaryResponse
import com.edumora.trayectoria.web.mapper.JobOfferMapper
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JobOfferUseCase(
    private val userRepository: UserRepository,
    private val companyProfileRepository: CompanyProfileRepository,
    private val jobOfferRepository: JobOfferRepository,
    private val skillRepository: SkillRepository,
    private val mapper: JobOfferMapper
) {
    //  Público
    @Transactional (readOnly = true)
    fun listActive(
        workMode: String?,
        jobType: String?,
        skillId: Long?,
        keyword: String?,
        pageable: Pageable
    ): PageResponse<JobOfferSummaryResponse> {
        // Encadenamos Specifications — solo se agrega el filtro si el param no es null
        var spec: Specification<JobOfferEntity> = JobOfferSpecification.hasStatus("ACTIVE")
        workMode?.let { spec = spec.and(JobOfferSpecification.hasWorkMode(it)) }
        jobType?.let  { spec = spec.and(JobOfferSpecification.hasJobType(it)) }
        skillId?.let  { spec = spec.and(JobOfferSpecification.hasSkill(it)) }
        keyword?.let  { spec = spec.and(JobOfferSpecification.titleContains(it)) }

        return PageResponse.Companion.from(
            jobOfferRepository.findAll(spec, pageable).map { mapper.toSummaryResponse(it) }
        )
    }

    @Transactional (readOnly = true)
    fun getById(id: Long): JobOfferResponse {
        val offer = jobOfferRepository.findByIdWithSkills(id)
            .orThrow("Job offer not found: $id")
        return mapper.toResponse(offer)
    }

    //  Empresa
    @Transactional (readOnly = true)
    fun listByCompany(email: String, pageable: Pageable): PageResponse<JobOfferSummaryResponse> {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        return PageResponse.Companion.from(
            jobOfferRepository.findByCompanyUserId(user.id, pageable)
                .map { mapper.toSummaryResponse(it) }
        )
    }

    @Transactional
    fun create(email: String, request: CreateJobOfferRequest): JobOfferResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        val company = companyProfileRepository.findByUserId(user.id)
            .orThrow("Company profile not found")

        val skills = if (request.skillIds.isNotEmpty()) {
            skillRepository.findAllByIdIn(request.skillIds).toMutableSet()
        } else mutableSetOf()

        val offer = JobOfferEntity(
            company = company,
            title = request.title,
            description = request.description,
            responsibilities = request.responsibilities,
            requirements = request.requirements,
            benefits = request.benefits,
            workMode = request.workMode,
            jobType = request.jobType,
            location = request.location,
            interviewInstructions = request.interviewInstructions,
            requiresInterview = request.requiresInterview,
            expiresAt = request.expiresAt,
            skills = skills
        )
        return mapper.toResponse(jobOfferRepository.save(offer))
    }

    @Transactional
    fun update(email: String, id: Long, request: CreateJobOfferRequest): JobOfferResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        verifyOwnership(id, user.id)

        val offer = jobOfferRepository.findByIdWithSkills(id).orThrow("Job offer not found: $id")

        offer.title                = request.title
        offer.description          = request.description
        offer.responsibilities     = request.responsibilities
        offer.requirements         = request.requirements
        offer.benefits             = request.benefits
        offer.workMode             = request.workMode
        offer.jobType              = request.jobType
        offer.location             = request.location
        offer.interviewInstructions = request.interviewInstructions
        offer.requiresInterview    = request.requiresInterview
        offer.expiresAt            = request.expiresAt

        if (request.skillIds.isNotEmpty()) {
            offer.skills = skillRepository.findAllByIdIn(request.skillIds).toMutableSet()
        }

        return mapper.toResponse(jobOfferRepository.save(offer))
    }

    @Transactional
    fun changeStatus(email: String, id: Long, request: UpdateJobOfferStatusRequest): JobOfferResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        verifyOwnership(id, user.id)

        val offer = jobOfferRepository.findById(id).orThrow("Job offer not found: $id")
        offer.status = request.status
        return mapper.toResponse(jobOfferRepository.save(offer))
    }

    @Transactional
    fun delete(email: String, id: Long) {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        verifyOwnership(id, user.id)
        jobOfferRepository.deleteById(id)
    }

    private fun verifyOwnership(offerId: Long, userId: Long) {
        if (!jobOfferRepository.existsByIdAndCompanyUserId(offerId, userId)) {
            throw ForbiddenException("You don't own this job offer")
        }
    }
}