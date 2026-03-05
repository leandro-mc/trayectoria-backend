package com.edumora.trayectoria.application.usecase.ai

import com.edumora.trayectoria.application.port.output.AiGenerationPort
import com.edumora.trayectoria.infrastructure.ai.prompts.CurriculumPrompts
import com.edumora.trayectoria.infrastructure.persistence.entity.CurriculumContent
import com.edumora.trayectoria.infrastructure.persistence.entity.GeneratedCurriculumEntity
import com.edumora.trayectoria.infrastructure.persistence.repository.CandidateProfileRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.GeneratedCurriculumRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.JobOfferRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.exception.InternalException
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.request.ai.GenerateCurriculumRequest
import com.edumora.trayectoria.web.dto.response.GeneratedCurriculumResponse
import com.edumora.trayectoria.web.mapper.CurriculumMapper
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GenerateCurriculumUseCase(
    private val userRepository: UserRepository,
    private val candidateProfileRepository: CandidateProfileRepository,
    private val jobOfferRepository: JobOfferRepository,
    private val curriculumRepository: GeneratedCurriculumRepository,
    private val aiPort: AiGenerationPort,
    private val objectMapper: ObjectMapper,
    private val mapper: CurriculumMapper
) {

    @Transactional
    fun execute(email: String, request: GenerateCurriculumRequest): GeneratedCurriculumResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        val profile = candidateProfileRepository.findByUserIdWithSkills(user.id)
            .orThrow("Candidate profile not found")
        val jobOffer = jobOfferRepository.findByIdWithSkills(request.jobOfferId)
            .orThrow("Job offer not found: ${request.jobOfferId}")

        // Construir el contexto del perfil en texto para el prompt
        val profileJson = objectMapper.writeValueAsString(buildProfileContext(profile))

        // Construir el resumen de la oferta para el prompt
        val jobOfferDetails = buildJobOfferContext(jobOffer)

        // Llamar a la IA — generateJson garantiza respuesta JSON parseable
        val aiJson = aiPort.generateJson(
            systemPrompt = CurriculumPrompts.systemPrompt(),
            userPrompt   = CurriculumPrompts.userPrompt(jobOfferDetails, profileJson)
        )

        // Parsear el JSON de la IA a nuestra estructura tipada
        val content = runCatching {
            objectMapper.readValue(aiJson, CurriculumContent::class.java)
        }.getOrElse {
            throw InternalException("Could not parse AI curriculum response: ${it.message}")
        }

        val curriculum = curriculumRepository.save(
            GeneratedCurriculumEntity(
                candidate     = profile,
                jobOffer      = jobOffer,
                content       = content,
                isAiGenerated = true
            )
        )

        return mapper.toResponse(curriculum)
    }

    private fun buildProfileContext(profile: com.edumora.trayectoria.infrastructure.persistence.entity.CandidateProfileEntity): Map<String, Any?> =
        mapOf(
            "name"       to "${profile.firstName} ${profile.lastName}",
            "bio"        to profile.bio,
            "location"   to profile.location,
            "skills"     to profile.skills.map { it.name },
            "experience" to profile.workExperiences.map {
                mapOf(
                    "company"     to it.company,
                    "position"    to it.position,
                    "description" to it.description,
                    "startDate"   to it.startDate,
                    "endDate"     to it.endDate,
                    "isCurrent"   to it.isCurrent
                )
            },
            "education"  to profile.educations.map {
                mapOf(
                    "institution"  to it.institution,
                    "degree"       to it.degree,
                    "fieldOfStudy" to it.fieldOfStudy,
                    "startDate"    to it.startDate,
                    "endDate"      to it.endDate
                )
            },
            "languages"  to profile.languages.map {
                mapOf("language" to it.language, "level" to it.level)
            }
        )

    private fun buildJobOfferContext(offer: com.edumora.trayectoria.infrastructure.persistence.entity.JobOfferEntity): String =
        """
        Title: ${offer.title}
        Company: ${offer.company?.companyName}
        Description: ${offer.description}
        Requirements: ${offer.requirements}
        Responsibilities: ${offer.responsibilities}
        Skills required: ${offer.skills.joinToString(", ") { it.name }}
        Work mode: ${offer.workMode}
        Job type: ${offer.jobType}
        """.trimIndent()
}