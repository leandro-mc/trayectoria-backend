package com.edumora.trayectoria.application.usecase.ai

import com.edumora.trayectoria.infrastructure.persistence.entity.CandidateProfileEntity
import com.edumora.trayectoria.infrastructure.persistence.entity.CurriculumContent
import com.edumora.trayectoria.infrastructure.persistence.entity.CurriculumEducation
import com.edumora.trayectoria.infrastructure.persistence.entity.CurriculumExperience
import com.edumora.trayectoria.infrastructure.persistence.entity.CurriculumLanguageItem
import com.edumora.trayectoria.infrastructure.persistence.repository.CandidateProfileRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.exception.ForbiddenException
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.response.GeneratedCurriculumResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class GetBaseCurriculumUseCase(
    private val userRepository: UserRepository,
    private val candidateProfileRepository: CandidateProfileRepository
) {
    private val periodFormatter = DateTimeFormatter.ofPattern("MMM yyyy")

    @Transactional(readOnly = true)
    fun execute(email: String, candidateId: Long): GeneratedCurriculumResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found")

        // Ownership: candidato solo puede ver el suyo, empresa puede ver cualquiera
        val isCandidate = user.roles.any { it.name == "ROLE_CANDIDATE" }
        if (isCandidate && user.id != candidateId) {
            throw ForbiddenException("You can only access your own base curriculum")
        }

        val candidate = candidateProfileRepository.findById(candidateId)
            .orThrow("Candidate profile not found: $candidateId")

        return GeneratedCurriculumResponse(
            id           = 0L,                  // virtual — no existe en DB
            jobOfferId   = null,
            jobOfferTitle = null,
            content      = buildContent(candidate),
            isAiGenerated = false,
            createdAt    = LocalDateTime.now()
        )
    }

    private fun buildContent(candidate: CandidateProfileEntity): CurriculumContent {
        val experience = candidate.workExperiences.map { exp ->
            CurriculumExperience(
                company     = exp.company.orEmpty(),
                position    = exp.position.orEmpty(),
                description = exp.description.orEmpty(),
                period      = formatPeriod(exp.startDate, exp.endDate, exp.isCurrent)
            )
        }

        val education = candidate.educations.map { edu ->
            CurriculumEducation(
                institution = edu.institution.orEmpty(),
                degree      = buildDegreeLabel(edu.degree, edu.fieldOfStudy),
                period      = formatPeriod(edu.startDate, edu.endDate, false)
            )
        }

        val skills = candidate.skills
            .map { it.name }
            .sorted()

        val languages = candidate.languages.map { lang ->
            CurriculumLanguageItem(
                language = lang.language,
                level    = lang.level.orEmpty()
            )
        }

        return CurriculumContent(
            summary     = candidate.bio.orEmpty(),
            experience  = experience,
            education   = education,
            skills      = skills,
            languages   = languages,
            highlights  = emptyList()   // sin datos en el perfil para highlights
        )
    }

    /**
     * Formatea el período de una experiencia o educación.
     * Ejemplos:
     *   "Mar 2024 – Present"
     *   "Mar 2024 – Nov 2025"
     *   "2024"          (solo año si no hay mes)
     *   ""              (si no hay fechas)
     */
    private fun formatPeriod(
        startDate: LocalDate?,
        endDate: LocalDate?,
        isCurrent: Boolean
    ): String {
        if (startDate == null) return ""

        val start = startDate.format(periodFormatter)
        val end = when {
            isCurrent        -> "Present"
            endDate != null  -> endDate.format(periodFormatter)
            else             -> "Present"
        }

        return "$start – $end"
    }

    /**
     * "Bachillerato en Ingeniería en Sistemas de Información"
     * Si no hay fieldOfStudy, retorna solo el degree.
     */
    private fun buildDegreeLabel(degree: String?, fieldOfStudy: String?): String {
        if (degree.isNullOrBlank()) return ""
        if (fieldOfStudy.isNullOrBlank()) return degree
        return "$degree en $fieldOfStudy"
    }
}