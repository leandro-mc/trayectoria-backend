package com.edumora.trayectoria.application.usecase.ai

import com.edumora.trayectoria.application.port.output.AiGenerationPort
import com.edumora.trayectoria.infrastructure.ai.prompts.InterviewPrompts
import com.edumora.trayectoria.infrastructure.persistence.entity.InterviewMessageEntity
import com.edumora.trayectoria.infrastructure.persistence.entity.SimulatedInterviewEntity
import com.edumora.trayectoria.infrastructure.persistence.repository.CandidateProfileRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.InterviewMessageRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.JobOfferRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.SimulatedInterviewRepository
import com.edumora.trayectoria.infrastructure.persistence.repository.UserRepository
import com.edumora.trayectoria.shared.util.orThrow
import com.edumora.trayectoria.web.dto.request.ai.StartInterviewRequest
import com.edumora.trayectoria.web.dto.response.SimulatedInterviewResponse
import com.edumora.trayectoria.web.mapper.InterviewMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StartInterviewUseCase(
    private val userRepository: UserRepository,
    private val candidateProfileRepository: CandidateProfileRepository,
    private val jobOfferRepository: JobOfferRepository,
    private val interviewRepository: SimulatedInterviewRepository,
    private val messageRepository: InterviewMessageRepository,
    private val aiPort: AiGenerationPort,
    private val mapper: InterviewMapper
) {
    @Transactional
    fun execute(email: String, request: StartInterviewRequest): SimulatedInterviewResponse {
        val user = userRepository.findByEmail(email).orThrow("User not found")
        val candidate = candidateProfileRepository.findByUserId(user.id)
            .orThrow("Candidate profile not found")
        val jobOffer = jobOfferRepository.findByIdWithSkills(request.jobOfferId)
            .orThrow("Job offer not found: ${request.jobOfferId}")

        // 1. Crear la entrevista
        val interview = interviewRepository.save(
            SimulatedInterviewEntity(
                candidate = candidate,
                jobOffer  = jobOffer,
                status    = "IN_PROGRESS"
            )
        )

        // 2. Generar el resumen de la oferta para usar como contexto del sistema
        val jobContext = buildJobContext(jobOffer)
        val jobSummary = aiPort.generate(
            systemPrompt = "Summarize job offers concisely for use as interviewer context.",
            userPrompt   = InterviewPrompts.summaryPrompt(jobContext)
        )

        // 3. Guardar el resumen como metadata interna (mensaje de sistema no visible al candidato)
        messageRepository.save(
            InterviewMessageEntity(
                interview = interview,
                role      = "SYSTEM",     // No se retorna al frontend
                content   = jobSummary
            )
        )

        // 4. Generar el saludo inicial del entrevistador
        val greeting = aiPort.generate(
            systemPrompt = InterviewPrompts.systemPrompt(jobSummary),
            userPrompt   = InterviewPrompts.greetingPrompt()
        )

        // 5. Guardar el saludo como primer mensaje del ASSISTANT
        messageRepository.save(
            InterviewMessageEntity(
                interview = interview,
                role      = "ASSISTANT",
                content   = greeting
            )
        )

        // Recargar con mensajes para la respuesta
        val interviewWithMessages = interviewRepository.findByIdWithMessages(interview.id, candidate.userId)
            .orThrow("Interview not found after creation")

        return mapper.toDetailResponse(interviewWithMessages)
    }

    private fun buildJobContext(offer: com.edumora.trayectoria.infrastructure.persistence.entity.JobOfferEntity): String =
        """
        Position: ${offer.title}
        Company: ${offer.company?.companyName}
        Description: ${offer.description}
        Requirements: ${offer.requirements}
        Skills: ${offer.skills.joinToString(", ") { it.name }}
        ${offer.interviewInstructions?.let { "Special interview instructions: $it" } ?: ""}
        """.trimIndent()
}